package com.labcorp.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class ConfigManager {
    private static final Properties PROPS = new Properties();

    static {
        try (InputStream in = ConfigManager.class.getClassLoader().getResourceAsStream("config/ui.properties")) {
            if (in == null) {
                throw new RuntimeException("config/ui.properties not found");
            }
            PROPS.load(in);
        } catch (IOException e) {
            throw new RuntimeException("Unable to load ui.properties", e);
        }
    }

    private ConfigManager() {}

    public static String get(String key) {
        String value = PROPS.getProperty(key);
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Missing config key: " + key);
        }
        return value.trim();
    }
}