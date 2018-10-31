package com.buzz.exception;

/**
 * Created by toshikijahja on 10/30/18.
 */
public enum GenericError implements ResponseError {
    FOUND(302),
    RESOURCE_NOT_FOUND(404),
    INTERNAL_ERROR(500);
    private final int responseCode;

    GenericError(final int responseCode) {
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
