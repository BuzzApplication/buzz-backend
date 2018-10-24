package com.buzz.requestBody;

import java.io.Serializable;

/**
 * Created by toshikijahja on 11/5/17.
 */
public class CommentRequestBody implements Serializable {

    private String text;
    private int buzzId;
    private int userEmailId;

    public CommentRequestBody() {

    }

    public String getText() {
        return this.text;
    }

    public void setText(final String text) {
        this.text = text;
    }

    public int getBuzzId() {
        return this.buzzId;
    }

    public void setBuzzId(final int buzzId) {
        this.buzzId = buzzId;
    }

    public int getUserEmailId() {
        return this.userEmailId;
    }

    public void setUserEmailId(final int userEmailId) {
        this.userEmailId = userEmailId;
    }
}
