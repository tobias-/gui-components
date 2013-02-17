package com.sourceforgery.guicomponents;

import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

public abstract class InterruptableBackgroundWorkerHandler<Args, Result> {

	private Thread currentlyRunning;

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
				e.printStackTrace();
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