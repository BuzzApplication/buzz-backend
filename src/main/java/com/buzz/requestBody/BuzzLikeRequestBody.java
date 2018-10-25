package com.buzz.requestBody;

import java.io.Serializable;
import java.util.List;

/**
 * Created by toshikijahja on 11/5/17.
 */
public class BuzzLikeRequestBody implements Serializable {

    private List<Integer> buzzIds;

    public BuzzLikeRequestBody() {

    }

    public List<Integer> getBuzzIds() {
        return buzzIds;
    }

    public void setBuzzIds(final List<Integer> buzzIds) {
        this.buzzIds = buzzIds;
    }
}
