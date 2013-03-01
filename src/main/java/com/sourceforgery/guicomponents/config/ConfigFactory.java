package com.sourceforgery.guicomponents.config;

import org.apache.log4j.Logger;

public class ConfigFactory {

	private static ConfigDatastore<?> instance;
	private static Exception oldConfigStack;
	private static Logger logger = Logger.getLogger(ConfigFactory.class);

	public static void init(final ConfigDatastore<?> datastore) {
		if (instance != null) {
			logger.fatal("Initialized more than once. Previous initialization", oldConfigStack);
			throw new RuntimeException("You cannot initialize DataFactory more than once");
		}
		oldConfigStack = new Exception();
		instance = datastore;
	}

	public static ConfigDatastore<?> instance() {
		if (instance == null) {
			RuntimeException internalServerException = new RuntimeException(
					"You have to init the connector before you can fetch the instance");
			internalServerException.printStackTrace();
			throw internalServerException;
		}
		return instance;
	}

	public static void removeInstance() {
		instance = null;
	}
}
