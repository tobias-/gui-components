package com.sourceforgery.guicomponents.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import nongui.BeanUtil;
import nongui.IORuntimeException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

public class ConfigDatastore<ConfigParameters extends Enum<?> & AvailableConfigParameters> {

	private Properties config;
	private final Map<AvailableConfigParameters, Object> cachedValues;
	private Map<String, String> propOrigin;

	private final String configPath;
	private final Logger LOGGER = Logger.getLogger(getClass());
	private final List<ConfigParameters> availableEnums;
	private final Method enumValueOf;

	@SuppressWarnings("unchecked")
	public ConfigDatastore(final Class<ConfigParameters> availableEnums, final String configPath) {
		if (StringUtils.isEmpty(System.getProperty("config.file"))) {
			this.configPath = configPath;
		} else {
			this.configPath = System.getProperty("config.file");
		}
		LOGGER.info("Loading config " + this.configPath);
		cachedValues = Collections.synchronizedMap(new HashMap<AvailableConfigParameters, Object>());
		propOrigin = new HashMap<String, String>();
		try {
			this.availableEnums = Arrays.asList((ConfigParameters[]) availableEnums.getMethod("values").invoke(null));
			enumValueOf = availableEnums.getMethod("valueOf", String.class);
		} catch (SecurityException e) {
			throw new IllegalArgumentException(e);
		} catch (NoSuchMethodException e) {
			throw new IllegalArgumentException(e);
		} catch (IllegalAccessException e) {
			throw new IllegalArgumentException(e);
		} catch (InvocationTargetException e) {
			throw new IllegalArgumentException(e);
		}
		refresh();
	}

	@SuppressWarnings("unchecked")
	private ConfigParameters valueOf(final String name) {
		try {
			return (ConfigParameters) enumValueOf.invoke(null, name);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

	public final synchronized void refresh() {
		Properties props = new Properties();
		Map<String, String> propOrigin = new HashMap<String, String>();
		loadConfigs(props, propOrigin);
		config = props;
		this.propOrigin = propOrigin;
		cachedValues.clear();
		validateConfig();
	}

	public void waitForDone() {
	}

	protected void loadConfigs(final Properties props, final Map<String, String> propOrigin) {
		loadEnumDefaults(props, propOrigin);
		try {
			loadFileProperties(props, propOrigin);
		} catch (IOException e) {
			LOGGER.error("Failed to load file properties" + e.getMessage());
		}
		loadEnvironmentProperties(props, propOrigin);
		loadSystemProperties(props, propOrigin);
	}

	protected void loadEnumDefaults(final Properties props, final Map<String, String> propOrigin) {
		for (AvailableConfigParameters conf : availableEnums) {
			props.setProperty(conf.name(), conf.getParam());
			propOrigin.put(conf.name(), "Enum");
		}
	}

	protected void loadEnvironmentProperties(final Properties props, final Map<String, String> propOrigin) {
		for (AvailableConfigParameters conf : availableEnums) {
			String systemParamValue = System.getenv(conf.toString());
			if (systemParamValue != null) {
				props.setProperty(conf.name(), systemParamValue);
				propOrigin.put(conf.name(), "Env");
			}
		}
	}

	protected void loadSystemProperties(final Properties props, final Map<String, String> propOrigin) {
		for (AvailableConfigParameters conf : availableEnums) {
			String systemParamValue = System.getProperty(conf.toString());
			if (systemParamValue != null) {
				props.setProperty(conf.name(), systemParamValue);
				propOrigin.put(conf.name(), "System Property");
			}
		}
	}

	protected void loadFileProperties(final Properties props, final Map<String, String> propOrigin) throws IOException {
		File file = new File(configPath);
		if (file.canRead() && file.exists() && file.isFile()) {
			loadConfig(props, file.getAbsoluteFile(), new LinkedList<File>(), propOrigin);
		}
	}

	protected void loadConfig(final Properties config, final File file, final List<File> stack,
			final Map<String, String> propOrigin) throws IOException {
		if (stack.contains(file)) {
			LOGGER.error("Config loop detected! " + stack + ". Skipped.");
			return;
		}
		LOGGER.debug("Reading configuration from: " + file.toString());
		Reader reader = new InputStreamReader(new FileInputStream(file), "UTF-8");
		try {
			stack.add(file);
			LineNumberReader lineNumberReader = new LineNumberReader(reader);
			String line;
			while ((line = lineNumberReader.readLine()) != null) {
				String trimmedLine = line.trim();
				if (!trimmedLine.startsWith("#") && !trimmedLine.isEmpty()) {
					if (trimmedLine.startsWith("load ")) {
						File subConfig = new File(trimmedLine.split(" +", 2)[1]).getAbsoluteFile();
						try {
							loadConfig(config, subConfig, stack, propOrigin);
						} catch (IOException e) {
							LOGGER.debug("Couldn't open/read subconfig " + subConfig + ". Skipped.");
							LOGGER.debug(e.getMessage());
						}
					} else {
						String[] split = trimmedLine.split("\\s*=\\s*", 2);
						if (split.length < 2) {
							throw new IOException("Error on line " + lineNumberReader.getLineNumber());
						}
						config.put(split[0], split[1]);
						propOrigin.put(split[0], file.toString() + ":" + lineNumberReader.getLineNumber());
					}
				}
			}
		} catch (IOException e) {
			throw new IOException("Error parsing in " + file, e);
		} finally {
			stack.remove(file);
			IOUtils.closeQuietly(reader);
		}
	}

	/**
	 *  Please use getters in the class where you implement AvailableConfigParameters and do not use this method directly.
	 *  Simple example of method in e.g. ConfigParameters:
	 *  <code><p>
	 *  public Integer getInteger() {<br>
	 *	&nbsp;&nbsp;return ConfigFactory.instance().getCastConfig(this);<br>
	 *  }<p>
	 *   </code>
	 *  Usage of the config value:<br>
	 *  <code>
	 *  setDefaultLength(ConfigParameters.DEFAULT_LENGTH_OF_SOMETHING.getInteger());<br>
	 *  </code><p>
	 *  Hint: <b>import static</b> is your friend when working with config variables<br>
	 *  @param configParameter The config parameter to read
	 * 
	 */
	public String getConfig(final AvailableConfigParameters configParameter) {
		return config.getProperty(configParameter.toString());
	}

	/**
	 *  Please use getters in the class where you implement AvailableConfigParameters and do not use this method directly.
	 *  Simple example of method in e.g. ConfigParameters:
	 *  <code><p>
	 *  public Integer getInteger() {<br>
	 *	&nbsp;&nbsp;return ConfigFactory.instance().getCastConfig(this);<br>
	 *  }<p>
	 *   </code>
	 *  Usage of the config value:<br>
	 *  <code>
	 *  setDefaultLength(ConfigParameters.DEFAULT_LENGTH_OF_SOMETHING.getInteger());<br>
	 *  </code><p>
	 *  Hint: <b>import static</b> is your friend when working with config variables<br>
	 *  @param configParameter The config parameter to read
	 * 
	 */
	@SuppressWarnings("unchecked")
	public <S> S getCastConfig(final AvailableConfigParameters configParameter) {
		if (configParameter.getClazz() == null) {
			throw new ConfigParsingException("Cannot cast to null class");
		} else {
			S value = (S) cachedValues.get(configParameter);
			if (value == null) {
				value = (S) BeanUtil.valueOf(configParameter.getClazz(), getConfig(configParameter));
				if (value != null) {
					cachedValues.put(configParameter, value);
				}
			}
			return value;
		}
	}

	protected void validateConfig() {
		for (ConfigParameters parameter : availableEnums) {
			String value = config.getProperty(parameter.name());
			String printedValue = value;
			if (parameter.toString().toLowerCase().matches(".*(secret|passwd|password|key).*")) {
				printedValue = value.replaceAll(".", "*");
			}
			try {
				if (parameter.getClazz() != null) {
					getCastConfig(parameter);
				}
			} catch (RuntimeException e) {
				LOGGER.error("Could not parse " + parameter.name() + " illegal value " + printedValue, e);
			}
			String message = parameter.name() + " = " + printedValue + " from " + propOrigin.get(parameter.name());
			LOGGER.debug(message);
		}
		for (String propName : config.stringPropertyNames()) {
			try {
				valueOf(propName);
			} catch (EnumConstantNotPresentException e) {
				String error = "WARNING!! Unused property present from: " + propOrigin.get(propName);
				LOGGER.fatal(error);
				System.exit(1);
			}
		}
	}

	protected Object isParseable(final String key, final String value) {
		AvailableConfigParameters configParameter = valueOf(key);
		if (configParameter.getClazz() == null) {
			throw new ConfigParsingException("Cannot cast to null class");
		} else {
			try {
				config.getProperty(configParameter.toString());
				return BeanUtil.valueOf(configParameter.getClazz(), value);
			} catch (RuntimeException e) {
			}
		}
		return null;
	}

	private static final String comments = "You're welcome to modify this file"
			+ "but comments will be deleted next time you quit the program";

	public void saveConfig() {
		try {
			config.store(new OutputStreamWriter(new FileOutputStream(configPath), "UTF-8"), comments);
		} catch (IOException e) {
			throw new IORuntimeException(e);
		}
	}

	public void clearConfig() {
		File file = new File(configPath);
		if (file.exists()) {
			file.delete();
		}
	}

	public void setConfig(final AvailableConfigParameters param, final String value) {
		boolean success = false;
		String origValue = getConfig(param);
		try {
			config.put(param.name(), value);
			cachedValues.remove(param);
			if (param.getClazz() != null) {
				getCastConfig(param);
			}
			success = true;
		} finally {
			if (!success) {
				config.put(param, origValue);
				cachedValues.remove(param);
				getCastConfig(param);
			}
		}
	}

	public Properties getConfig() {
		return config;
	}

	public void setConfig(final Properties config) {
		this.config = config;
	}

	public Map<AvailableConfigParameters, Object> getCachedValues() {
		return cachedValues;
	}

	public Map<String, String> getPropOrigin() {
		return propOrigin;
	}

	public void setPropOrigin(final Map<String, String> propOrigin) {
		this.propOrigin = propOrigin;
	}
}
