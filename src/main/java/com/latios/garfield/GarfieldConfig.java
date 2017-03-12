package com.latios.garfield;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zebin
 * @since 2016-10-16.
 */
public class GarfieldConfig {

    private static GarfieldConfig config = null;
    private static final Logger LOG = Logger.getLogger(GarfieldConfig.class);
    private Map<String, String> configs = new HashMap<>();

    public static GarfieldConfig getInstance() {
        if (config == null) {
            synchronized (GarfieldConfig.class) {
                if (config == null) {
                    config = new GarfieldConfig();
                }
            }
        }
        return config;
    }

    private GarfieldConfig() {
        try {
            FileReader fileReader = new FileReader(GarfieldConsts.FILE_NAME_WATCHING_CONFIG);
            BufferedReader input = new BufferedReader(fileReader);
            String line;
            while ((line = input.readLine()) != null) {
                if (!line.startsWith("#") && line.contains("=")) {
                    String[] strings = line.split("\\=", 2);
                    configs.put(strings[0], strings[1]);
                }
            }
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
            System.exit(1);
        }
    }

    public String get(String key) {
        return configs.get(key);
    }

    public Integer getAsInt(String key) {
        String val = configs.get(key);
        return val == null ? null : Integer.valueOf(val);
    }

    public Long getAsLong(String key) {
        String val = configs.get(key);
        return val == null ? null : Long.valueOf(val);
    }
}
