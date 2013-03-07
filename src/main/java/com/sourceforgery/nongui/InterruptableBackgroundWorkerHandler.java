package com.sourceforgery.nongui;

import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

public abstract class InterruptableBackgroundWorkerHandler<Args, Result> {

	private Thread currentlyRunning;

	private final Logger log = Logger.getLogger(getClass());

	private class InterrputableBackgroundWorker extends Thread {
		private final Args[] data;
		public InterrputableBackgroundWorker(final Args... data) {
			this.data = data;
		}
		@Override
		public void run() {
			try {
				final Result runWithData = runWithData(data);
				SwingUtilities.invokeAndWait(new Runnable() {
					@Override
					public void run() {
						runAfterInGui(runWithData);
					}
				});
			} catch (InterruptedException e) {
				// Normal. Ignore
			} catch (InvocationTargetException e) {
				log.error("Failed to invoke gui job", e);
			}
		}
	}

	public InterruptableBackgroundWorkerHandler() {
	}

	public synchronized void runInBackground(final Args... data) {
		if (currentlyRunning != null) {
			currentlyRunning.interrupt();
		}
		currentlyRunning = new InterrputableBackgroundWorker(data);
		currentlyRunning.start();
	}

	public abstract Result runWithData(Args... data) throws InterruptedException;
	public abstract void runAfterInGui(Result data);
}