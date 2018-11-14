package com.buzz.view;

import com.buzz.model.Feedback;

/**
 * Created by toshikijahja on 10/18/17.
 */
public class FeedbackView extends BaseView {

    private final Feedback feedback;

    public FeedbackView(final Feedback feedback) {
        this.feedback = feedback;
    }

    public int getId() {
        return feedback.getId();
    }

    public int getUserId() {
        return feedback.getUserId();
    }

    public String getText() {
        return feedback.getText();
    }

    public int getVersion() {
        return feedback.getVersion();
    }

    public String getUserAgent() {
        return feedback.getUserAgent();
    }

    public String getSystemName() {
        return feedback.getSystemName();
    }

    public String getSystemVersion() {
        return feedback.getSystemVersion();
    }

    public String getCarrier() {
        return feedback.getCarrier();
    }

    public String getIpAddr() {
        return feedback.getIpAddr();
    }

    public String getLocale() {
        return feedback.getLocale();
    }
}
