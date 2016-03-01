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
import java.util.Properties;

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
                field.set(instance, Enum.valueOf(property.type(), val));
            } else {
                switch (name) {
                    case "int":
                        int value;
                        try {
                            value = Integer.parseInt(val);
                        } catch (NumberFormatException e) {
                            throw new RuntimeException("Invalid value for field " + property.value());
                        }
                        field.setInt(instance, value);
                        break;
                    case "java.lang.String":
                        field.set(instance, val);
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
