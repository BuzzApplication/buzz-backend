package com.buzz.view;

import java.util.List;

/**
 * Created by toshikijahja on 10/18/17.
 */
public class BuzzCommentListView {

    private final BuzzView buzzView;
    private final List<CommentView> commentViews;

    public BuzzCommentListView(final BuzzView buzzView,
                               final List<CommentView> commentViews) {
        this.buzzView = buzzView;
        this.commentViews = commentViews;
    }

    public BuzzView getBuzz() {
        return buzzView;
    }

    public List<CommentView> getCommentList() {
        return commentViews;
    }
}
