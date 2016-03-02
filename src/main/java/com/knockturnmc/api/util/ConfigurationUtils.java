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
import java.lang.reflect.Constructor;
import java.util.Properties;

/**
 * Provides utilities for loading configurations and loading mapped {@link NamedProperties}
 */
public final class ConfigurationUtils {

    private ConfigurationUtils() {}

    /**
     * Loads a standard Java {@link Properties} file.
     * If the desired file is not found, a file with the same name will be copied from the classpath to the datafolder.
     * If no default file was found, an empty file will be created.
     * @param classLoader the classloader to use for the default file
     * @param file the filename to create/load
     * @param datafolder the datafolder to use
     * @return the loaded/created file
     */
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

    /**
     * Loads a plain file.
     * If the desired file is not found, a file with the same name will be copied from the classpath to the datafolder.
     * If no default file was found in the classloader's classpath, an empty file will be created.
     * @param classLoader the classloader to use for the default file
     * @param file the filename to create/load
     * @param datafolder the datafolder to use
     * @return the loaded/created file
     * @throws IOException if something went wrong
     */
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


    /**
     * Loads a plain file.
     * If the desired file is not found, a file with the same name will be copied from the classpath to the current working directory.
     * If no default file was found in the classloader's classpath, an empty file will be created.
     * @param classLoader the classloader to use for the default file
     * @param file the filename to create/load
     * @return the loaded/created file
     * @throws IOException if something went wrong
     */
    public static File getConfigFile(ClassLoader classLoader, String file) throws IOException {
        return getConfigFile(classLoader, file, getDataFolder());
    }

    /**
     * Loads a standard Java {@link Properties} file.
     * If the desired file is not found, a file with the same name will be copied from the classpath to the current working directory.
     * If no default file was found, an empty file will be created.
     * @param classLoader the classloader to use for the default file
     * @param file the filename to create/load
     * @return the loaded/created file
     */
    public static Properties loadConfiguration(ClassLoader classLoader, String file) {
        return loadConfiguration(classLoader, file, getDataFolder());
    }

    /**
     * Loads a mapped {@link Properties} file and applies the mapping provided by the {@link NamedProperties}.
     * If the desired file was not found in the datafolder, a default file will be copied from the classpath.
     * @param classLoader the classloader to use for the default file
     * @param filename the filename
     * @param datafolder the datafolder
     * @param mapping the mapped file
     * @param <T> the type of the mapped file
     * @return the loaded configuration mapping
     */
    public static <T extends NamedProperties> T loadConfiguration(ClassLoader classLoader, String filename, File datafolder, Class<? extends T> mapping) {
        try {
            File file = getConfigFile(classLoader, filename, datafolder);
            Constructor<? extends T> constructor = mapping.getConstructor();
            constructor.setAccessible(true);
            T properties = constructor.newInstance();
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

    /**
     * Loads a mapped {@link Properties} file and applies the mapping provided by the {@link NamedProperties}.
     * If the desired file was not found in the current working directory, a default file will be copied from the classpath.
     * @param classLoader the classloader to use for the default file
     * @param filename the filename
     * @param mapping the mapped file
     * @param <T> the type of the mapped file
     * @return the loaded configuration mapping
     */
    public static <T extends NamedProperties> T loadConfiguration(ClassLoader classLoader, String filename, Class<? extends T> mapping) {
        return loadConfiguration(classLoader, filename, getDataFolder(), mapping);
    }

    /**
     * Gets the current working directory, this is also the default datafolder for all methods in {@link ConfigurationUtils}
     * @return the current working directory
     */
    public static File getDataFolder() {
        return new File(System.getProperty("user.dir"));
    }
}
