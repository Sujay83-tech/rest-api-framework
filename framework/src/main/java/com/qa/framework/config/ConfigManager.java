package com.qa.framework.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * ConfigManager — reads config.properties once at startup.
 * All tests call ConfigManager.get("BASE_URL") etc.
 * Changing the URL in config.properties updates every test.
 */
public class ConfigManager {

    private static Properties props;
    private static final String CONFIG_PATH = "src/test/resources/config.properties";

    // Static block: loads the file once when the class is first used
    static {
        props = new Properties();
        try (FileInputStream fis = new FileInputStream(CONFIG_PATH)) {
            props.load(fis);
            System.out.println("[ConfigManager] Config loaded from: " + CONFIG_PATH);
        } catch (IOException e) {
            throw new RuntimeException("[ConfigManager] Failed to load config.properties: " + e.getMessage());
        }
    }

    /**
     * Get a property value by key.
     * Throws a clear error if the key is missing — easier to debug.
     */
    public static String get(String key) {
        String value = props.getProperty(key);
        if (value == null || value.trim().isEmpty()) {
            throw new RuntimeException("[ConfigManager] Missing key in config.properties: '" + key + "'");
        }
        return value.trim();
    }

    /**
     * Get a property value with a fallback default.
     * Use this for optional config keys.
     */
    public static String get(String key, String defaultValue) {
        return props.getProperty(key, defaultValue).trim();
    }

    /**
     * Get a property as an integer (for timeouts etc.)
     */
    public static int getInt(String key) {
        return Integer.parseInt(get(key));
    }

}
