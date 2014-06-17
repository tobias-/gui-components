package com.sourceforgery.nongui;

public interface PropertyListener<T> {
	void propertyUpdated(T origValue, T newValue);
}
