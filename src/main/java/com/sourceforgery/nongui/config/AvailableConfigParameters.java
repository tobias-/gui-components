package com.sourceforgery.nongui.config;

public interface AvailableConfigParameters {
	String name();
	String getParam();
	Class<?> getClazz();
	Boolean getBoolean();
	Integer intValue();
	<T> T get();
}
