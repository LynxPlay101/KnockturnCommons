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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * This class provides the tools to communicate with a JSON RESTful Service
 */
public abstract class RestClient {

    private final ContentType contentType;

    protected RestClient(ContentType contentType) {
        this.contentType = contentType;
    }

    private HttpURLConnection getConnection(String path, String method) throws IOException {
        URL url = new URL(path);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoOutput(method.equals("POST"));
        connection.setRequestMethod(method);
        connection.setRequestProperty("Accept", contentType.getContentType());
        return connection;
    }

    /**
     * Executes a HTTP GET request to the given URL
     *
     * @param path the path to execute a request
     * @return the response
     * @throws IOException if the connection failed
     */
    protected String doGet(String path) throws IOException {
        HttpURLConnection connection = getConnection(path, "GET");
        return getResponse(connection);
    }

    /**
     * Executes a HTTP POST request to the given URL
     *
     * @param path the path to execute a request
     * @param body the request body
     * @return the response
     * @throws IOException if the connection failed
     */
    protected String doPost(String path, String body) throws IOException {
        HttpURLConnection connection = getConnection(path, "POST");
        connection.setRequestProperty("Content-Type", contentType.getContentType());
        OutputStream stream = connection.getOutputStream();
        stream.write(body.getBytes());
        stream.flush();

        return getResponse(connection);
    }

    private String getResponse(HttpURLConnection connection) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

        String output;
        StringBuilder sb = new StringBuilder();

        while ((output = reader.readLine()) != null) {
            sb.append(output);
        }
        return sb.toString();
    }
}
