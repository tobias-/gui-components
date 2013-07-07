package com.sourceforgery.guicomponents;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.AbstractListModel;

import com.sourceforgery.nongui.Filter;

public class SaneListModel<T> extends AbstractListModel {
	private static final long serialVersionUID = 1L;
	protected final Set<T> list;
	protected final List<T> shownList = new ArrayList<T>();
	@SuppressWarnings("unchecked")
	protected Filter<T> filter = Filter.ALL_VISIBLE;
	protected boolean updatedEnabled = true;
	protected boolean hasDelayedUpdates;

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

	public void addAll(final Collection<? extends T> all) throws InterruptedException {
		list.addAll(all);
		updateShown();
	}

	public boolean add(final T object) {
		boolean b = list.add(object);
		updateShown();
		return b;
	}

	private void updateShown() {
		if (!updatedEnabled) {
			hasDelayedUpdates = true;
			return;
		}
		hasDelayedUpdates = false;
		int oldSize = getSize();
		shownList.clear();
		for (T item : list) {
			if (getFilter().isVisible(item)) {
				shownList.add(item);
			}
		}
		if (oldSize != getSize()) {
			fireContentsChanged(this, 0, getSize());
		}
	}

	@Override
	public int getSize() {
		return shownList.size();
	}

	@Override
	public T getElementAt(final int index) {
		return shownList.get(index);
	}

	public void clear() {
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

	public boolean remove(final T object) {
		int idx = shownList.indexOf(object);
		if (idx >= 0) {
			shownList.remove(idx);
			fireIntervalRemoved(this, idx, idx);
		}
		return list.remove(object);
	}

	public Set<T> getSet() {
		return Collections.unmodifiableSet(list);
	}

	public boolean isUpdatedEnabled() {
		return updatedEnabled;
	}

	public void setUpdatedEnabled(final boolean updatedEnabled) {
		this.updatedEnabled = updatedEnabled;
		if (updatedEnabled && hasDelayedUpdates) {
			updateShown();
		}
	}
}