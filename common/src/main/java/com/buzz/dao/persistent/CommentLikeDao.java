package com.buzz.dao.persistent;

import com.buzz.exception.BuzzException;
import com.buzz.model.CommentLike;
import org.hibernate.query.Query;

import java.util.List;
import java.util.Optional;

import static com.buzz.exception.BadRequest.COMMENT_LIKE_NOT_EXIST;
import static com.buzz.utils.QueryUtils.listObjectToSqlQueryInString;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

/**
 * Created by toshikijahja on 6/7/17.
 */
public class CommentLikeDao extends BaseDao<CommentLike> {

    public CommentLikeDao(final SessionProvider sessionProvider) {
        super(sessionProvider, CommentLike.class);
    }

    @SuppressWarnings("unchecked")
    public Optional<CommentLike> getByUserIdAndCommentId(final int userId,
                                                         final int commentId) {
        return getFirst(getByUserIdAndCommentIds(userId, singletonList(commentId)));
    }

    @SuppressWarnings("unchecked")
    public List<CommentLike> getByUserIdAndCommentIds(final int userId,
                                                      final List<Integer> commentIds) {
        if (commentIds.isEmpty()) {
            return emptyList();
        }
        final Query query = getSessionProvider().getSession().createQuery(
                "FROM " + clazz.getName() + " WHERE userId = :userId AND commentId IN " + listObjectToSqlQueryInString(commentIds));
        query.setParameter("userId", userId);
        return query.list();
    }

    public void likeComment(final int userId, int commentId) {
        likeComment(userId, singletonList(commentId));
    }

    public void likeComment(final int userId,
                            final List<Integer> commentIds) {
        if (commentIds.isEmpty()) {
            return;
        }
        getSessionProvider().startTransaction();
        commentIds.forEach(commentId -> {
            final CommentLike commentLike = new CommentLike.Builder().userId(userId).commentId(commentId).build();
            getSessionProvider().getSession().persist(commentLike);
        });
        getSessionProvider().commitTransaction();
    }

    public void dislikeComment(final int userId, final int commentId) {
        getSessionProvider().startTransaction();
        final Optional<CommentLike> commentLike = getByUserIdAndCommentId(userId, commentId);
        if (!commentLike.isPresent()) {
            throw new BuzzException(COMMENT_LIKE_NOT_EXIST);
        }
        getSessionProvider().getSession().delete(commentLike.get());
        getSessionProvider().commitTransaction();
    }
}
