package com.buzz.exception;

/**
 * Created by toshikijahja on 10/30/18.
 */
public interface ResponseError {
    String getErrorKey();
    String getErrorDescription();
    int getHttpResponseCode();
}
