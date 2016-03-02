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

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Objects;
import java.util.Properties;

/**
 * Provides simple mapping solution for {@link Properties} based configuration.
 * When the {@link Properties#load} method is invoked, the mapping will be applied to the class members
 * An example:
 * <pre>
 * <code>public class TestProperties extends NamedProperties {
 *     {@literal @Property(value = "test")}
 *      public int test;
 *
 *     {@literal @Property(value = "defaulttest", defaultvalue = "-69")}
 *      public int defaulttest;
 *
 *     {@literal @Property(value = "test.2")}
 *      public String test2;
 *
 *     {@literal @Property(value = "time.unit", type = TimeUnit.class)}
 *      public TimeUnit timeUnit;
 * }</code></pre>
 * If a mapping was not found in the file, it will be created with the default value.
 * If no default value is present, it will be instatianted will the type's default value,
 * for primitives this is {@code 0} or {@code false} and for objects this will be {@code null}
 */
public abstract class NamedProperties extends Properties {

    @Override
    public synchronized void load(InputStream inStream) throws IOException {
        super.load(inStream);
        map();
    }

    @Override
    public synchronized void load(Reader reader) throws IOException {
        super.load(reader);
        map();
    }

    @Override
    public synchronized void loadFromXML(InputStream in) throws IOException {
        super.loadFromXML(in);
        map();
    }

    private void prepareMapping(Class clazz, NamedProperties instance) throws IllegalAccessException {
        for (Field field : clazz.getDeclaredFields()) {
            if (!field.isAnnotationPresent(Property.class))
                continue;
            Type type = field.getGenericType();
            Property property = field.getAnnotation(Property.class);
            field.setAccessible(true);
            String name = type.getTypeName();

            String val = getString(property.value());
            if (val == null) {
                val = property.defaultvalue();
                setProperty(property.value(), val);
            }

            if (property.type() != Void.class) {
                field.set(instance, Objects.equals(val, "") ? null : Enum.valueOf(property.type(), val));
            } else {
                switch (name) {
                    case "int":
                        int value;
                        try {
                            value = Integer.parseInt(Objects.equals(val, "") ? "0" : val);
                        } catch (NumberFormatException e) {
                            throw new RuntimeException("Invalid value for field " + property.value());
                        }
                        field.setInt(instance, value);
                        break;
                    case "java.lang.String":
                        field.set(instance, val);
                        break;
                    case "boolean":
                        field.setBoolean(instance, Boolean.parseBoolean(val));
                        break;
                    default:
                        throw new RuntimeException("Unsupported property type: " + name);
                }
            }
        }
    }

    private void map() {
        try {
            prepareMapping(getClass(), this);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String getString(String key) {
        return (String) get(key);
    }
}
