package com.buzz.requestBody;

import java.io.Serializable;
import java.util.List;

/**
 * Created by toshikijahja on 11/5/17.
 */
public class CommentLikeRequestBody implements Serializable {

    private List<Integer> commentIds;

    public CommentLikeRequestBody() {

    }

    public List<Integer> getCommentIds() {
        return commentIds;
    }

    public void setCommentIds(final List<Integer> commentIds) {
        this.commentIds = commentIds;
    }
}
