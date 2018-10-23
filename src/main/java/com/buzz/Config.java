package com.buzz;

import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import static com.buzz.Constants.SETTINGS_FILE_PATH;

/**
 * Created by toshikijahja on 10/24/17.
 */
public class Config {

    private Configuration config;

    public Config() {
        try {
            config = new PropertiesConfiguration(SETTINGS_FILE_PATH);
        } catch (final ConfigurationException e) {
            e.printStackTrace();
        }
    }

    public String getString(final String key) {
        return config.getString(key);
    }

    public int getInteger(final String key) {
        return config.getInt(key);
    }

    public Boolean getBoolean(final String key) {
        return config.getBoolean(key);
    }

    public List<Object> getList(final String key) {
        return config.getList(key);
    }
}
