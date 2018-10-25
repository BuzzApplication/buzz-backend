package com.buzz.view;

import java.util.List;

/**
 * Created by toshikijahja on 10/18/17.
 */
public class CommentListView {

    private final List<CommentView> commentViews;

    public CommentListView(final List<CommentView> commentViews) {
        this.commentViews = commentViews;
    }

    public List<CommentView> getCommentList() {
        return commentViews;
    }
}
