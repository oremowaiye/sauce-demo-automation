package com.ore.saucedemo.config;

import com.ore.saucedemo.exceptions.FrameworkException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Reads the framework's configuration.
 *
 * <p>Values load once from {@code config.properties} on the classpath. A system property
 * of the same name overrides any of them, so one suite can run headless in CI and in a
 * visible browser locally:
 *
 * <pre>mvn test -Dbrowser=firefox -Dheadless=false</pre>
 */
public final class ConfigReader {

    private static final String CONFIG_FILE = "config.properties";
    private static final Properties PROPERTIES = load();

    private ConfigReader() {
    }

    private static Properties load() {
        Properties properties = new Properties();
        try (InputStream stream = ConfigReader.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (stream == null) {
                throw new FrameworkException(CONFIG_FILE + " was not found on the classpath");
            }
            properties.load(stream);
            return properties;
        } catch (IOException e) {
            throw new FrameworkException("Unable to read " + CONFIG_FILE, e);
        }
    }

    /** A system property beats the file, so CI and local runs can share one config. */
    public static String get(String key) {
        String override = System.getProperty(key);
        if (override != null && !override.isBlank()) {
            return override.trim();
        }
        String value = properties().getProperty(key);
        if (value == null) {
            throw new FrameworkException("No configuration value for key: " + key);
        }
        return value.trim();
    }

    public static String get(String key, String defaultValue) {
        String override = System.getProperty(key);
        if (override != null && !override.isBlank()) {
            return override.trim();
        }
        return properties().getProperty(key, defaultValue).trim();
    }

    public static int getInt(String key) {
        String value = get(key);
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new FrameworkException("Configuration key '" + key + "' is not a number: " + value, e);
        }
    }

    public static boolean getBoolean(String key) {
        return Boolean.parseBoolean(get(key));
    }

    public static String baseUrl() {
        return get("base.url");
    }

    public static String apiBaseUrl() {
        return get("api.base.url");
    }

    public static String password() {
        return get("password");
    }

    public static int explicitTimeout() {
        return getInt("timeout.explicit");
    }

    public static int pageLoadTimeout() {
        return getInt("timeout.pageload");
    }

    private static Properties properties() {
        return PROPERTIES;
    }
}
