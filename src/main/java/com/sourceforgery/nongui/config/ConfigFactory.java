package com.sourceforgery.nongui.config;

import org.apache.log4j.Logger;

public class ConfigFactory {

	private static ConfigDatastore<?> instance;
	private static Throwable oldConfigStack;
	private static Logger logger = Logger.getLogger(ConfigFactory.class);

	private ConfigFactory() {
	}

	public static void init(final ConfigDatastore<?> datastore) {
		if (instance != null) {
			logger.fatal("Initialized more than once. Previous initialization", oldConfigStack);
			throw new IllegalStateException("You cannot initialize DataFactory more than once");
		}
		oldConfigStack = new Throwable();
		instance = datastore;
	}

	public static ConfigDatastore<?> instance() {
		if (instance == null) {
			throw new IllegalStateException("You have to init the connector before you can fetch the instance");
		}
		return instance;
	}

	public static void removeInstance() {
		instance = null;
	}
}
