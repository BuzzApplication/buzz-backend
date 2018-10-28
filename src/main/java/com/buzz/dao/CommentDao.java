package com.buzz.dao;

import com.buzz.model.Comment;
import com.buzz.model.UserEmail;
import com.buzz.requestBody.CommentRequestBody;
import org.hibernate.query.Query;

import java.util.List;

import static com.buzz.dao.BaseDao.Sort.DESC;
import static java.util.Objects.requireNonNull;

/**
 * Created by toshikijahja on 6/7/17.
 */
public class CommentDao extends BaseDao<Comment> {

    public CommentDao(final SessionProvider sessionProvider) {
        super(sessionProvider, Comment.class);
    }

    public List<Comment> getByBuzzId(final int buzzId,
                                     final int start,
                                     final int limit) {
        return getByFieldSortedAndPaginated("buzzId", buzzId, "created", DESC, start, limit);
    }

    public Comment postComment(final CommentRequestBody commentRequestBody,
                               final UserEmail userEmail,
                               final BuzzDao buzzDao) {
        requireNonNull(commentRequestBody);
        requireNonNull(userEmail);
        requireNonNull(buzzDao);
        getSessionProvider().startTransaction();
        final Comment comment = new Comment.Builder()
                .alias(userEmail.getUser().getAlias())
                .buzzId(commentRequestBody.getBuzzId())
                .text(commentRequestBody.getText())
                .userEmail(userEmail)
                .build();
        getSessionProvider().getSession().persist(comment);
        buzzDao.updateCommentsCount(commentRequestBody.getBuzzId());
        getSessionProvider().commitTransaction();
        return comment;
    }

    public void increaseLikesCount(final int commentId) {
        final Query query = getSessionProvider().getSession().createQuery(
                "UPDATE " + clazz.getName() + " SET likesCount = likesCount + 1 WHERE id = :commentId");
        query.setParameter("commentId", commentId);
        query.executeUpdate();
    }

    public void decreaseLikesCount(final int commentId) {
        final Query query = getSessionProvider().getSession().createQuery(
                "UPDATE " + clazz.getName() + " SET likesCount = likesCount - 1 WHERE id = :commentId");
        query.setParameter("commentId", commentId);
        query.executeUpdate();
    }
}
