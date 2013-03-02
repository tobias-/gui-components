package com.sourceforgery.guicomponents;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.AbstractListModel;
import javax.swing.SwingUtilities;

import nongui.Filter;

public class SaneListModel<T> extends AbstractListModel {
	private static final long serialVersionUID = 1L;
	private final Set<T> list;
	private final List<T> shownList = new ArrayList<T>();
	@SuppressWarnings("unchecked")
	private Filter<T> filter = Filter.ALL_VISIBLE;

	public SaneListModel() {
		this(new Comparator<T>() {
			@Override
			public int compare(final T o1, final T o2) {
				return o1.toString().compareToIgnoreCase(o2.toString());
			}
		});
	}

	public SaneListModel(final Comparator<T> comparator) {
		list = new TreeSet<T>(comparator);
	}

	public synchronized void addAll(final Collection<? extends T> all) throws InterruptedException {
		list.addAll(all);
		updateShown();
	}

	private synchronized void updateShown() throws InterruptedException {
		int oldSize = getSize();
		shownList.clear();
		for (T item : list) {
			if (getFilter().isVisible(item)) {
				shownList.add(item);
			}
			if (Thread.currentThread().isInterrupted()) {
				throw new InterruptedException("Thread interrupted");
			}
		}
		if (oldSize != getSize()) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					fireContentsChanged(this, 0, getSize());
				}
			});
		}
	}

	@Override
	public synchronized int getSize() {
		return shownList.size();
	}

	@Override
	public synchronized T getElementAt(final int index) {
		return shownList.get(index);
	}

	public synchronized void clear() {
		int size = getSize();
		list.clear();
		shownList.clear();
		fireIntervalRemoved(this, 0, size);
	}

	public Filter<T> getFilter() {
		return filter;
	}

	public void setFilter(final Filter<T> filter) throws InterruptedException {
		this.filter = filter;
		updateShown();
	}
}