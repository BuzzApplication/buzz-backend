package com.buzz.requestBody;

import java.io.Serializable;

/**
 * Created by toshikijahja on 11/5/17.
 */
public class BuzzLikeRequestBody implements Serializable {

    private int buzzId;
    private boolean liked;

    public BuzzLikeRequestBody() {

    }

    public int getBuzzId() {
        return buzzId;
    }

    public void setBuzzId(final int buzzId) {
        this.buzzId = buzzId;
    }

    public boolean isLiked() {
        return liked;
    }

    public void setLiked(final boolean liked) {
        this.liked = liked;
    }
}
