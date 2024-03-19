/*
 * Copyright (c) 2022 Worldline Global Collect B.V
 */

package com.worldline.connect.android.example.java.exception;

public class IinStatusNotKnownException extends RuntimeException {

    private static final long serialVersionUID = 8536329875217499493L;

    public IinStatusNotKnownException() {
        super();
    }

    public IinStatusNotKnownException(String message) {
        super(message);
    }

    public IinStatusNotKnownException(Throwable t) {
        super(t);
    }

    public IinStatusNotKnownException(String message, Throwable t) {
        super(message, t);
    }
}
