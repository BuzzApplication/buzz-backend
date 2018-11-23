package com.buzz.requestBody;

import java.io.Serializable;

/**
 * Created by toshikijahja on 11/5/17.
 */
public class AuthenticationRequestBody implements Serializable {

    private String email;
    private String password;

    public AuthenticationRequestBody() {

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
}
