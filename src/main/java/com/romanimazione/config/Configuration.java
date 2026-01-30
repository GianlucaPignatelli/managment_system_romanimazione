package com.romanimazione.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Configuration {

    private final Properties properties;

    public Configuration() {
        properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (input != null) {
                properties.load(input);
            }
        } catch (IOException e) {
            // Log error or ignore
        }
    }

    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
}
