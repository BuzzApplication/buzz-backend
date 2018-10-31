package com.buzz.exception;

/**
 * Created by toshikijahja on 10/30/18.
 */
public enum Unauthorized implements ResponseError {
    UNAUTHORIZED(401),
    FORBIDDEN(403);

    private final int responseCode;

    Unauthorized(final int responseCode) {
        this.responseCode = responseCode;
    }

    public int getHttpResponseCode() {
        return responseCode;
    }

    public String getErrorKey() {
        return name();
    }

    public String getErrorDescription() {
        return name();
    }
}


