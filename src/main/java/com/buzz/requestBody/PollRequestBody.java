package com.buzz.requestBody;

import java.io.Serializable;

/**
 * Created by toshikijahja on 11/5/17.
 */
public class PollRequestBody implements Serializable {

    private int buzzId;
    private int pollId;

    public PollRequestBody() {

    }

    public int getBuzzId() {
        return this.buzzId;
    }

    public void setBuzzId(final int buzzId) {
        this.buzzId = buzzId;
    }

    public int getPollId() {
        return this.pollId;
    }

    public void setPollId(final int pollId) {
        this.pollId = pollId;
    }
}
