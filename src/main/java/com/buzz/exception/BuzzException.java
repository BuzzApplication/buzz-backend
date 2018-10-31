package com.buzz.exception;

/**
 * Created by toshikijahja on 10/30/18.
 */
public class BuzzException extends RuntimeException {

    private final ResponseError responseError;

    public BuzzException(final ResponseError responseError) {
        super(responseError.getErrorDescription());
        this.responseError = responseError;
    }

    public ResponseError getResponseError() {
        return responseError;
    }
}
