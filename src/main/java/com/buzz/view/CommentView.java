package com.buzz.view;

import com.buzz.model.Comment;
import com.buzz.utils.TimeUtils;

/**
 * Created by toshikijahja on 10/18/17.
 */
public class CommentView {

    private final Comment comment;
    private final boolean liked;

    public CommentView(final Comment comment) {
        this(comment, false);
    }

    public CommentView(final Comment comment,
                       final boolean liked) {
        this.comment = comment;
        this.liked = liked;
    }

    public int getId() {
        return comment.getId();
    }

    public String getText() {
        return comment.getText();
    }

    public CompanyView getUserCompany() {
        return new CompanyView(comment.getUserEmail().getCompany());
    }

    public String getAlias() {
        return comment.getAlias();
    }

    public int getLikesCount() {
        return comment.getLikesCount();
    }

    public boolean isLiked() {
        return liked;
    }

    public TimeView getTimePassed() {
        return TimeUtils.getTimePassed(comment.getCreated());
    }
}
