package com.sourceforgery.nongui;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Property which fires on listeners when the value is changed
 */
public class Property<T> {
	private final transient List<WeakReference<PropertyListener<T>>> listeners = new LinkedList<WeakReference<PropertyListener<T>>>();
	private T value;

	public Property() {
	}

	public Property(final T origValue) {
		value = origValue;
	}

	protected boolean equals(final Object o1, final Object o2) {
		return o1 == o2 || o1 != null && o1.equals(o2);
	}

	public void set(final T newValue) {
		boolean updated = !equals(newValue, value);
		if (updated) {
			T origValue = value;
			value = newValue;
			fireListeners(origValue, newValue);
		}
	}

	protected void fireListeners(final T origValue, final T newValue) {
		boolean foundDeadWeak = false;
		for(WeakReference<PropertyListener<T>> pl : listeners) {
			PropertyListener<T> propertyListener = pl.get();
			if (propertyListener != null) {
				propertyListener.propertyUpdated(origValue, newValue);
			} else {
				foundDeadWeak = true;
			}
		}
		if (foundDeadWeak) {
			removeListener(null);
		}
	}

	public void addListener(final PropertyListener<T> listener) {
		listeners.add(new WeakReference<PropertyListener<T>>(listener));
	}

	public void removeListener(final PropertyListener<T> listener) {
		Iterator<WeakReference<PropertyListener<T>>> iter = listeners.iterator();
		while(iter.hasNext()) {
			PropertyListener<T> propertyListener = iter.next().get();
			if (propertyListener == null || propertyListener.equals(listener)) {
				iter.remove();
			}
		}
	}

	public T get() {
		return value;
	}
}