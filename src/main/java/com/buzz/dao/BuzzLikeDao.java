package com.buzz.dao;

import com.buzz.model.BuzzLike;
import org.hibernate.query.Query;

import java.util.List;
import java.util.Optional;

import static com.buzz.utils.QueryUtils.listObjectToSqlQueryInString;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

/**
 * Created by toshikijahja on 6/7/17.
 */
public class BuzzLikeDao extends BaseDao<BuzzLike> {

    public BuzzLikeDao(final SessionProvider sessionProvider) {
        super(sessionProvider, BuzzLike.class);
    }

    @SuppressWarnings("unchecked")
    public Optional<BuzzLike> getByUserIdAndBuzzId(final int userId,
                                                   final int buzzId) {
        return getFirst(getByUserIdAndBuzzIds(userId, singletonList(buzzId)));
    }

    @SuppressWarnings("unchecked")
    public List<BuzzLike> getByUserIdAndBuzzIds(final int userId,
                                                final List<Integer> buzzIds) {
        if (buzzIds.isEmpty()) {
            return emptyList();
        }
        final Query query = getSessionProvider().getSession().createQuery(
                "FROM " + clazz.getName() + " WHERE userId = :userId AND buzzId IN " + listObjectToSqlQueryInString(buzzIds));
        query.setParameter("userId", userId);
        return query.list();
    }

    public void likeBuzz(final int userId,
                         final List<Integer> buzzIds) {
        if (buzzIds.isEmpty()) {
            return;
        }
        getSessionProvider().startTransaction();
        buzzIds.forEach(buzzId -> {
            final BuzzLike buzzLike = new BuzzLike.Builder().userId(userId).buzzId(buzzId).build();
            getSessionProvider().getSession().persist(buzzLike);
        });
        getSessionProvider().commitTransaction();
    }
}
