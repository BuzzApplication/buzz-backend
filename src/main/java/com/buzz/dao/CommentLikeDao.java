package com.buzz.dao;

import com.buzz.model.CommentLike;
import org.hibernate.query.Query;

import java.util.List;
import java.util.Optional;

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
}
