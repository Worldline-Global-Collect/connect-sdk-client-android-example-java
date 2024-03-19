/*
 * Copyright (c) 2022 Worldline Global Collect B.V
 */

package com.worldline.connect.android.example.java.exception;

public class ThirdPartyStatusNotAllowedException extends RuntimeException {

    private static final long serialVersionUID = 3271047769311985926L;

    public ThirdPartyStatusNotAllowedException() {
        super();
    }

    public ThirdPartyStatusNotAllowedException(String message) {
        super(message);
    }

    public ThirdPartyStatusNotAllowedException(Throwable t) {
        super(t);
    }

    public ThirdPartyStatusNotAllowedException(String message, Throwable t) {
        super(message, t);
    }
}
