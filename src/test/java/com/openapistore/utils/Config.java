package com.openapistore.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class Config {
    private static final Properties PROPERTIES = new Properties();

    static {
        try (InputStream is = Config.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (is != null) {
                PROPERTIES.load(is);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config.properties", e);
        }
    }

    private Config() {
    }

    public static String getBaseUrl() {
        String fromSystem = System.getProperty("baseUrl");
        if (fromSystem != null && !fromSystem.isBlank()) {
            return fromSystem;
        }
        return PROPERTIES.getProperty("baseUrl", "https://fakestoreapi.com");
    }
}
