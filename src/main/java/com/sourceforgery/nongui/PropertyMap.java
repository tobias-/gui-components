package com.sourceforgery.nongui;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

public class PropertyMap<K, V> extends Property<SortedMap<K, V>> implements SortedMap<K, V> {
	public PropertyMap(final SortedMap<K, V> map) {
		set(map);
	}

	public PropertyMap() {
		this(new TreeMap<K, V>());
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
	public boolean containsKey(final Object key) {
		return get().containsKey(key);
	}

	@Override
	public boolean containsValue(final Object value) {
		return get().containsValue(value);
	}

	@Override
	public V get(final Object key) {
		return get().get(key);
	}

	@Override
	public V put(final K key, final V value) {
		V put = get().put(key, value);
		boolean equals = equals(put, value);
		if (!equals) {
			fireListeners(get(), get());
		}
		return put;
	}

	@Override
	public V remove(final Object key) {
		V remove = get().remove(key);
		if (remove != null) {
			fireListeners(null, get());
		}
		return remove;
	}

	@Override
	public void putAll(final Map<? extends K, ? extends V> m) {
		get().putAll(m);
		fireListeners(get(), get());
	}

	@Override
	public void clear() {
		if (get().isEmpty()) {
			get().clear();
		} else {
			get().clear();
			fireListeners(get(), get());
		}
	}

	@Override
	public Set<K> keySet() {
		return get().keySet();
	}

	@Override
	public Collection<V> values() {
		return get().values();
	}

	@Override
	public Set<java.util.Map.Entry<K, V>> entrySet() {
		return get().entrySet();
	}

	@Override
	public Comparator<? super K> comparator() {
		return get().comparator();
	}

	@Override
	public SortedMap<K, V> subMap(final K fromKey, final K toKey) {
		return get().subMap(fromKey, toKey);
	}

	@Override
	public SortedMap<K, V> headMap(final K toKey) {
		return get().headMap(toKey);
	}

	@Override
	public SortedMap<K, V> tailMap(final K fromKey) {
		return get().tailMap(fromKey);
	}

	@Override
	public K firstKey() {
		return get().firstKey();
	}

	@Override
	public K lastKey() {
		return get().lastKey();
	}
}
