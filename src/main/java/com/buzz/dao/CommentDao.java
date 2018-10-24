package com.buzz.dao;

import com.buzz.model.Comment;
import com.buzz.model.UserEmail;
import com.buzz.requestBody.CommentRequestBody;

import java.util.List;

import static com.buzz.dao.BaseDao.Sort.DESC;

/**
 * Created by toshikijahja on 6/7/17.
 */
public class CommentDao extends BaseDao<Comment> {

    public CommentDao(final SessionProvider sessionProvider) {
        super(sessionProvider, Comment.class);
    }

    public List<Comment> getByBuzzId(final int buzzId) {
        return getByFieldSorted("buzzId", buzzId, "created", DESC);
    }

    public Comment postComment(final CommentRequestBody buzzRequestBody,
                               final UserEmail userEmail,
                               final int buzzId,
                               final BuzzDao buzzDao) {
        getSessionProvider().startTransaction();
        final Comment comment = new Comment.Builder()
                .alias(userEmail.getUser().getAlias())
                .buzzId(buzzId)
                .text(buzzRequestBody.getText())
                .userEmail(userEmail)
                .build();
        getSessionProvider().getSession().persist(comment);
        buzzDao.updateCommentsCount(buzzId);
        getSessionProvider().commitTransaction();
        return comment;
    }
}
