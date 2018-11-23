package com.buzz.requestBody;

import java.io.Serializable;

/**
 * Created by toshikijahja on 11/5/17.
 */
public class AuthenticationVerificationRequestBody implements Serializable {

    private String email;
    private String password;
    private String verificationCode;

    public AuthenticationVerificationRequestBody() {

    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public String getVerificationCode() {
        return this.verificationCode;
    }

    public void setVerificationCode(final String verificationCode) {
        this.verificationCode = verificationCode;
    }
}
