package com.sourceforgery.guicomponents;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import javax.swing.AbstractListModel;

import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;
import com.google.common.collect.TreeMultiset;
import com.sourceforgery.nongui.Filter;

public class SaneListModel<T> extends AbstractListModel {
	private static final long serialVersionUID = 1L;
	protected Multiset<T> list;
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

	public void setComparator(final Comparator<T> newComparator) {
		Multiset<T> list2 = TreeMultiset.create(newComparator);
		list2.addAll(list);
		list = list2;
		updateShown();
	}

	public SaneListModel(final Comparator<T> comparator) {
		list = TreeMultiset.create(comparator);
	}

	public void addAll(final Collection<? extends T> all) {
		list.addAll(all);
		updateShown();
	}

	public boolean add(final T object) {
		boolean b = list.add(object);
		updateShown();
		return b;
	}

	public synchronized void updateShown() {
		if (!updatedEnabled) {
			hasDelayedUpdates = true;
			return;
		}
		fireIntervalRemoved(this, 0, getSize());
		hasDelayedUpdates = false;
		shownList.clear();
		for (T item : list) {
			if (getFilter().isVisible(item)) {
				shownList.add(item);
			}
		}
		fireIntervalAdded(this, 0, getSize());
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
		if (updatedEnabled) {
			fireIntervalRemoved(this, 0, size);
		} else {
			hasDelayedUpdates = true;
		}
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
			if (updatedEnabled) {
				fireIntervalRemoved(this, idx, idx);
			} else {
				hasDelayedUpdates = true;
			}
		}
		return list.remove(object);
	}

	public Multiset<T> getSet() {
		return Multisets.unmodifiableMultiset(list);
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