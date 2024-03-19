/*
 * Copyright (c) 2022 Worldline Global Collect B.V
 */

package com.worldline.connect.android.example.java.model;

import java.io.Serializable;

/**
 * Pojo that represents a ShowData KeyValuePair
 *
 */
public class KeyValuePair implements Serializable {

    private static final long serialVersionUID = -2060644958674272920L;

    private String key;
    private String value;

    public KeyValuePair() { }

    public  String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }

}
