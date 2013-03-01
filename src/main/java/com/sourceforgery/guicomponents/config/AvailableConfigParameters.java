package com.sourceforgery.guicomponents.config;

public interface AvailableConfigParameters {
	public String name();
	public String getParam();
	public Class<?> getClazz();
	public Boolean getBoolean();
	public Integer intValue();
	public<T> T get();

}
