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

import java.nio.ByteBuffer;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Provides some utilities
 */
public final class Utils {

    private static Pattern IP_PATTERN = Pattern.compile(
            "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");

    private Utils() {
    }

    /**
     * Parses a given string into a UUID. String can be with or without hyphens (-).
     *
     * @param uuid the string to parse
     * @return the parsed string.
     * Returns {@code null} if string is invalid.
     */
    public static UUID formatUUID(String uuid) {
        try {
            if (uuid == null)
                throw new IllegalArgumentException("UUID can not be null");
            try {
                return UUID.fromString(uuid);
            } catch (Exception e) {
                StringBuilder sb = new StringBuilder(uuid);
                for (int i = 0; i <= 3; i++) {
                    sb.insert(8 + (i * 5), "-");
                }
                return UUID.fromString(sb.toString());
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid UUID", e);
        }
    }

    /**
     * Gets the uuid from a from bytes
     *
     * @param raw the bytes
     * @return the uuid
     */
    public static UUID getUUID(byte[] raw) {
        ByteBuffer buffer = ByteBuffer.wrap(raw);
        return new UUID(buffer.getLong(0), buffer.getLong(8));
    }

    /**
     * Gets the bytes from a uuid
     *
     * @param uuid the uuid
     * @return the bytes
     */
    public static byte[] getBytes(UUID uuid) {
        ByteBuffer buffer = ByteBuffer.wrap(new byte[16]);
        buffer.putLong(uuid.getMostSignificantBits());
        buffer.putLong(uuid.getLeastSignificantBits());
        return buffer.array();
    }

    /**
     * Evaluates a character from a string
     *
     * @param string the string
     * @return null if string is null or empty
     */
    public static Character evaluateCharacter(String string) {
        if (string == null)
            return null;
        if (string.length() != 1)
            return null;
        return string.charAt(0);
    }

    /**
     * Gets the enum object from a string
     *
     * @param clazz the enum type
     * @param name  the enum name
     * @param <T>   the enum type
     * @return the enum or null if not found
     */
    public static <T extends Enum<T>> T evaluateEnum(Class<T> clazz, String name) {
        if (name == null || name.length() < 1)
            return null;
        try {
            return Enum.valueOf(clazz, name);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Concatinates a string with a given seperator
     * If the seperator is {@code null} it will be interpreted as an empty string
     * @param seperator the seperator between the strings
     * @param args the strings to concatinate
     * @return the concatinated string
     */
    public static String join(String seperator, String[] args) {
        if (args == null)
            throw new IllegalArgumentException("args can not be null");
        if (seperator == null)
            seperator = "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < args.length; i++) {
            String s = args[i];
            sb.append(s);
            if (i != args.length - 1)
                sb.append(seperator);
        }
        return sb.toString();
    }

    /**
     * Validate ip address with regular expression
     * @param ip ip address for validation
     * @return true valid ip address, false invalid ip address
     */
    public static boolean validateIP(final String ip){
        Matcher matcher = IP_PATTERN.matcher(ip);
        return matcher.matches();
    }
}
