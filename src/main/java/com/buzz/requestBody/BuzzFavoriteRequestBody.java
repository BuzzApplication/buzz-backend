package com.buzz.requestBody;

import java.io.Serializable;

/**
 * Created by toshikijahja on 11/5/17.
 */
public class BuzzFavoriteRequestBody implements Serializable {

    private int buzzId;
    private boolean favorited;

    public BuzzFavoriteRequestBody() {

    }

    public int getBuzzId() {
        return buzzId;
    }

    public void setBuzzId(final int buzzId) {
        this.buzzId = buzzId;
    }

    public boolean isFavorited() {
        return favorited;
    }

    public void setFavorited(final boolean favorited) {
        this.favorited = favorited;
    }
}
