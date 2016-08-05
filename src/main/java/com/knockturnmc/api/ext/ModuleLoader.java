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

package com.knockturnmc.api.ext;

import lombok.extern.log4j.Log4j;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Represents a module loader that can load external classes from module files
 *
 * @param <T> the parent instance class
 */
@Log4j
public class ModuleLoader<T> {

    private final Map<Loadable<T>, Module> modules;
    private final File moduleDir;
    private final T parent;
    private final String moduleExtension;

    /**
     * Creates a new module loader
     *
     * @param moduleDir the module directory
     * @param parent    the class that is loading the modules
     */
    public ModuleLoader(File moduleDir, T parent) {
        this(moduleDir, ".jar", parent);
    }

    private ModuleLoader(File moduleDir, String moduleExtension, T parent) {
        this.parent = parent;
        this.moduleDir = moduleDir;
        this.modules = new HashMap<>();
        this.moduleExtension = moduleExtension;
    }

    private static boolean isModule(Class clazz) {
        return clazz.isAnnotationPresent(Module.class) && Loadable.class.isAssignableFrom(clazz);
    }

    /**
     * Can be overriden to alter behavior when loading a module
     *
     * @param module the module that is loaded
     */
    protected void handle(Loadable<T> module, Module meta) {

    }

    /**
     * Gets all the modules
     *
     * @return the modules
     */
    public Map<Loadable<T>, Module> getModules() {
        return new HashMap<>(modules);
    }

    /**
     * Loads the modules
     */
    public final void loadModules() {
        if (moduleDir.exists() && moduleDir.isDirectory()) {
            File[] moduleFiles = moduleDir.listFiles();
            if (moduleFiles == null)
                return;
            for (File moduleFile : moduleFiles) {
                if (!moduleFile.isDirectory() && moduleFile.getName().endsWith(moduleExtension)) {
                    loadModule(moduleFile);
                }
            }
        } else {
            if (!moduleDir.mkdirs()) {
                throw new RuntimeException("Failed to create module folder!");
            }
        }
    }

    public final void unloadModules() {
        for (Loadable<T> module : modules.keySet()) {
            try {
                try {
                    module.onDisable(parent);
                } catch (Exception e) {
                    log.error("Failed to disable module", e);
                }
                URLClassLoader cl = (URLClassLoader) module.getClass().getClassLoader();
                cl.close();
            } catch (IOException e) {
                log.error("Failed to unload module", e);
            }
        }
    }

    /**
     * Gets the file of the module
     *
     * @param module the module
     * @return the file
     */
    public String getModuleFile(Loadable<T> module) {
        URLClassLoader cl = (URLClassLoader) module.getClass().getClassLoader();
        try {
            return cl.getURLs()[0].toURI().getPath();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets the module directory
     *
     * @return module directory
     */
    public File getModuleDir() {
        return moduleDir;
    }

    /**
     * Gets the extensions that are loaded
     *
     * @return the extensions
     */
    public String getModuleExtension() {
        return moduleExtension;
    }

    private void loadModule(File moduleFile) {
        try (JarFile jarFile = new JarFile(moduleFile)) {
            URL[] urls = {new URL("jar:file:" + moduleFile.getPath() + "!/")};
            URLClassLoader cl = URLClassLoader.newInstance(urls, getClass().getClassLoader());
            Enumeration<JarEntry> e = jarFile.entries();
            while (e.hasMoreElements()) {
                JarEntry entry = e.nextElement();
                if (entry.isDirectory() || !entry.getName().endsWith(".class"))
                    continue;
                String className = entry.getName().substring(0, entry.getName().length() - 6);
                className = className.replace('/', '.');
                Class clazz = Class.forName(className, true, cl);
                if (isModule(clazz)) {
                    Module module = (Module) clazz.getAnnotation(Module.class);
                    Loadable<T> moduleInstance = load(clazz, module);
                    modules.put(moduleInstance, module);
                }
            }
        } catch (Exception e) {
            log.error("Failed to load module file", e);
        }
    }

    private Loadable<T> load(Class moduleClass, Module meta) throws Exception {
        Loadable<T> module = (Loadable<T>) moduleClass.newInstance();
        try {
            module.onEnable(parent);
            handle(module, meta);
        } catch (Exception e) {
            log.error("Error during module initialization", e);
        }
        return module;
    }
}