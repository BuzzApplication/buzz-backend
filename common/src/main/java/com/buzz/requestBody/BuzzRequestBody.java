package com.buzz.requestBody;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by toshikijahja on 11/5/17.
 */
public class BuzzRequestBody implements Serializable {

    private String text;
    private int companyId;
    private int userEmailId;
    private boolean anonymous;
    private List<String> polls = new ArrayList<>();

    public BuzzRequestBody() {

    }

    public String getText() {
        return this.text;
    }

    public void setText(final String text) {
        this.text = text;
    }

    public int getCompanyId() {
        return this.companyId;
    }

    public void setCompanyId(final int companyId) {
        this.companyId = companyId;
    }

    public int getUserEmailId() {
        return this.userEmailId;
    }

    public void setUserEmailId(final int userEmailId) {
        this.userEmailId = userEmailId;
    }

    public boolean isAnonymous() {
        return this.anonymous;
    }

    public void setAnonymous(final boolean anonymous) {
        this.anonymous = anonymous;
    }

    public List<String> getPolls() {
        return this.polls;
    }

    public void setPolls(final List<String> polls) {
        this.polls = polls;
    }
}
