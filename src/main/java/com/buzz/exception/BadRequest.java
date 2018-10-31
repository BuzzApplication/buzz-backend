package com.buzz.exception;

/**
 * Created by toshikijahja on 10/30/18.
 */
public enum BadRequest implements ResponseError {
    ALIAS_EXIST("Alias already exists"),
    BUZZ_NOT_EXIST("Buzz does not exist"),
    BUZZ_LIKE_NOT_EXIST("Buzz like does not exist"),
    COMMENT_NOT_EXIST("Comment does not exist"),
    COMMENT_LIKE_NOT_EXIST("Comment like does not exist"),
    COMPANY_NOT_EXIST("Company does not work in the company"),
    COMPANY_EMAIL_NOT_EXIST("Company email does not work in the company"),
    EMPTY_REQUEST_BODY("Request body cannot be empty"),
    INVALID_EMAIL_FORMAT("Email format is invalid"),
    TEXT_CANNOT_BE_EMPTY("Text cannot be empty"),
    USER_EMAIL_NOT_EXIST("User email does not work in the company"),
    USER_EMAIL_NOT_MATCH("User email does not match with current logged in user"),
    USER_NOT_IN_COMPANY("User does not work in the company");

    private final String errorDescription;

    BadRequest(final String errorDescription) {
        this.errorDescription = errorDescription;
    }

    public int getHttpResponseCode() {
        return 400;
    }

    public String getErrorKey() {
        return name();
    }

    public String getErrorDescription() {
        return errorDescription;
    }
}
