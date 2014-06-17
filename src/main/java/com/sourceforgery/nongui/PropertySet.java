package com.sourceforgery.nongui;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public class PropertySet<T> extends Property<Set<T>> implements Set<T> {
	public PropertySet(Set<T> list) {
		set(list);
	}

	public PropertySet() {
	}
	
	@Override
	public boolean add(T value) {
		boolean add = get().add(value);
		if (add) {
			fireListeners(null, get());
		}
		return add;
	}

	@Override
	public int size() {
		return get().size();
	}

	@Override
	public boolean isEmpty() {
		return get().isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		return get().contains(get());
	}

	@Override
	public Iterator<T> iterator() {
		return get().iterator();
	}

	@Override
	public Object[] toArray() {
		return get().toArray();
	}

	@Override
	public <X> X[] toArray(X[] a) {
		return get().toArray(a);
	}

	@Override
	public boolean remove(Object o) {
		boolean remove = get().remove(o);
		if (remove) {
			fireListeners(null, get());
		}
		return remove;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return get().containsAll(c);
	}

	@Override
	public boolean addAll(Collection<? extends T> c) {
		boolean addAll = get().addAll(c);
		if (addAll) {
			fireListeners(null, get());
		}
		return addAll;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		boolean retainAll = get().retainAll(c);
		if (retainAll) {
			fireListeners(null, get());
		}
		return retainAll;
		
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		boolean removeAll = get().removeAll(c);
		if (removeAll) {
			fireListeners(null, get());
		}
		return removeAll;
	}

	@Override
	public void clear() {
		boolean wasEmpty = get().isEmpty();
		get().clear();
		if (!wasEmpty) {
			fireListeners(null, get());
		}
	}

}
