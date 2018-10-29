package com.buzz.view;

import com.buzz.model.Buzz;
import com.buzz.utils.TimeUtils;

/**
 * Created by toshikijahja on 10/18/17.
 */
public class BuzzView {

    private final Buzz buzz;
    private final boolean liked;

    public BuzzView(final Buzz buzz,
                    final boolean liked) {
        this.buzz = buzz;
        this.liked = liked;
    }

    public int getId() {
        return buzz.getId();
    }

    public String getText() {
        return buzz.getText();
    }

    public int getCompanyId() {
        return buzz.getCompanyId();
    }

    public String getAlias() {
        return buzz.getAlias() == null ? "" : buzz.getAlias();
    }

    public CompanyView getUserCompany() {
        return new CompanyView(buzz.getUserEmail().getCompany());
    }

    public int getLikesCount() {
        return buzz.getLikesCount();
    }

    public int getCommentsCount() {
        return buzz.getCommentsCount();
    }

    public boolean isLiked() {
        return liked;
    }

    public TimeView getTimePassed() {
        return TimeUtils.getTimePassed(buzz.getCreated());
    }
}
