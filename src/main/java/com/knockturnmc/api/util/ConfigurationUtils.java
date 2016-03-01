/*
The MIT License (MIT)

Copyright (c) 2016 Sven Olderaan, http://knockturnmc.com/

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE
 */

package com.knockturnmc.api.util;

import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.Properties;

public final class ConfigurationUtils {

    private ConfigurationUtils() {}

    public static Properties loadConfiguration(ClassLoader classLoader, String file, File datafolder) {
        try {
            File config = getConfigFile(classLoader, file, datafolder);
            Properties props = new Properties();
            FileInputStream stream = new FileInputStream(config);
            props.load(stream);
            stream.close();
            return props;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static File getConfigFile(ClassLoader classLoader, String file, File datafolder) throws IOException {
        File config = new File(datafolder, file);
        datafolder.mkdirs();
        if (!config.exists()) {
            System.out.println("No configuration file found. Copying default configuration...");
            InputStream in = classLoader.getResourceAsStream(file);
            if (in != null) {
                if (!config.createNewFile())
                    System.out.println("Failed creating default file.");
                OutputStream out = new FileOutputStream(config);
                IOUtils.copy(in, out);
                in.close();
                out.flush();
                out.close();
            } else {
                config.createNewFile();
            }
        }
        return config;
    }

    public static File getConfigFile(ClassLoader classLoader, String file) throws IOException {
        return getConfigFile(classLoader, file, getDataFolder());
    }

    public static Properties loadConfiguration(ClassLoader classLoader, String file) {
        return loadConfiguration(classLoader, file, getDataFolder());
    }

    public static <T extends NamedProperties> T loadConfiguration(ClassLoader classLoader, String filename, File datafolder, Class<? extends T> mapping) {
        try {
            File file = getConfigFile(classLoader, filename, datafolder);
            T properties = mapping.newInstance();
            FileInputStream stream = new FileInputStream(file);
            properties.load(stream);
            stream.close();

            OutputStream fos = new FileOutputStream(file);

            properties.store(fos, "Configuration for " + filename);
            fos.close();
            return properties;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T extends NamedProperties> T loadConfiguration(ClassLoader classLoader, String filename, Class<? extends T> mapping) {
        return loadConfiguration(classLoader, filename, getDataFolder(), mapping);
    }

    public static File getDataFolder() {
        return new File(System.getProperty("user.dir"));
    }
}
