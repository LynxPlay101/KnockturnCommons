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

import com.knockturnmc.api.util.ConfigurationUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ModuleLoaderTest {

    private static final String MODULE_FILE = "test-module-1.0-SNAPSHOT.jar";
    private File datafolder;
    private File moduleFile;

    @Before
    public void setUp() throws Exception {
        datafolder = new File(ConfigurationUtils.getDataFolder(), "modules");
        assertTrue(datafolder.mkdir());
        moduleFile = ConfigurationUtils.getConfigFile(getClass().getClassLoader(), MODULE_FILE, datafolder);
    }

    @After
    public void tearDown() throws Exception {
        assertTrue(moduleFile.delete());
        assertTrue(datafolder.delete());
    }

    @Test
    public void testModuleLoader() throws Exception {
        AtomicBoolean atomicBoolean = new AtomicBoolean(false);
        ModuleLoader<AtomicBoolean> moduleLoader = new ModuleLoader<>(datafolder, atomicBoolean);

        moduleLoader.loadModules();
        assertTrue(atomicBoolean.get());

        moduleLoader.unloadModules();
        assertFalse(atomicBoolean.get());
    }
}