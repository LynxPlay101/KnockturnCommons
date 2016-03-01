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

/**
 * Tuple class for sets of two objects
 *
 * @param <T0> object 1
 * @param <T1> object 2
 */
public class Tuple<T0, T1> {
    protected T0 _0;
    protected T1 _1;

    public Tuple(T0 _0, T1 _1) {
        this._0 = _0;
        this._1 = _1;
    }

    /**
     * Creates a new tuple instance with given inputs
     *
     * @param i1   input a
     * @param i2   input b
     * @param <I1> type a
     * @param <I2> type b
     * @return a tupl with given inputs
     */
    public static <I1, I2> Tuple<I1, I2> getTuple(I1 i1, I2 i2) {
        return new Tuple<>(i1, i2);
    }

    /**
     * Gets the first object
     *
     * @return first object
     */
    public T0 get_0() {
        return _0;
    }

    /**
     * Gets the second object
     *
     * @return second object
     */
    public T1 get_1() {
        return _1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Tuple tuple2 = (Tuple) o;
        return !(_0 != null ? !_0.equals(tuple2._0) : tuple2._0 != null) && !(_1 != null ? !_1.equals(tuple2._1) : tuple2._1 != null);
    }

    @Override
    public int hashCode() {
        int result = _0 != null ? _0.hashCode() : 0;
        result = 31 * result + (_1 != null ? _1.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "(" + _0 + ',' + _1 + ')';
    }
}