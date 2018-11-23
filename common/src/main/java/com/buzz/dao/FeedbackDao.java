package com.buzz.dao;

import com.buzz.model.Feedback;
import com.buzz.model.Report;
import com.buzz.requestBody.FeedbackRequestBody;

import static java.util.Objects.requireNonNull;

/**
 * Created by toshikijahja on 11/7/18.
 */
public class FeedbackDao extends BaseDao<Report> {

    public FeedbackDao(final SessionProvider sessionProvider) {
        super(sessionProvider, Report.class);
    }

    public Feedback submitFeedback(final FeedbackRequestBody feedbackRequestBody,
                                   final int userId) {
        requireNonNull(feedbackRequestBody);
        getSessionProvider().startTransaction();
        final Feedback feedback = new Feedback.Builder()
                .userId(userId)
                .text(feedbackRequestBody.getText())
                .version(feedbackRequestBody.getVersion())
                .userAgent(feedbackRequestBody.getUserAgent())
                .systemName(feedbackRequestBody.getSystemName())
                .systemVersion(feedbackRequestBody.getSystemVersion())
                .carrier(feedbackRequestBody.getCarrier())
                .ipAddr(feedbackRequestBody.getIpAddr())
                .locale(feedbackRequestBody.getLocale())
                .build();
        getSessionProvider().getSession().persist(feedback);
        getSessionProvider().commitTransaction();
        return feedback;
    }
}
