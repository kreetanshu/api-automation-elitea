package com.openapistore.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Central place to read configuration.
 *
 * Resolution order:
 * 1) JVM system property: -Dapi.baseUrl
 * 2) src/test/resources/config.properties
 */
public final class Config {

  private static final String CONFIG_FILE = "config.properties";

  private static Config instance;
  private final Properties properties = new Properties();

  private Config() {
    try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(CONFIG_FILE)) {
      if (is != null) {
        properties.load(is);
      }
    } catch (IOException e) {
      throw new RuntimeException("Failed to load " + CONFIG_FILE, e);
    }

    // System properties override file properties
    for (String key : System.getProperties().stringPropertyNames()) {
      if (key.startsWith("api.") || key.startsWith("http.")) {
        properties.setProperty(key, System.getProperty(key));
      }
    }
  }

  public static synchronized Config getInstance() {
    if (instance == null) {
      instance = new Config();
    }
    return instance;
  }

  public String getBaseUrl() {
    String baseUrl = properties.getProperty("api.baseUrl", "https://fakestoreapi.com");
    if (baseUrl.endsWith("/")) {
      baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
    }
    return baseUrl;
  }

  public int getHttpTimeoutSeconds() {
    return Integer.parseInt(properties.getProperty("http.timeout.seconds", "30"));
  }
}
