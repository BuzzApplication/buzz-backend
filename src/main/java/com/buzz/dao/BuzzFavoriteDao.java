package com.buzz.dao;

import com.buzz.exception.BuzzException;
import com.buzz.model.BuzzFavorite;
import org.hibernate.query.Query;

import java.util.List;
import java.util.Optional;

import static com.buzz.exception.BadRequest.BUZZ_FAVORITE_NOT_EXIST;
import static com.buzz.utils.QueryUtils.listObjectToSqlQueryInString;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

/**
 * Created by toshikijahja on 6/7/17.
 */
public class BuzzFavoriteDao extends BaseDao<BuzzFavorite> {

    public BuzzFavoriteDao(final SessionProvider sessionProvider) {
        super(sessionProvider, BuzzFavorite.class);
    }

    @SuppressWarnings("unchecked")
    public Optional<BuzzFavorite> getByUserIdAndBuzzId(final int userId,
                                                       final int buzzId) {
        return getFirst(getByUserIdAndBuzzIds(userId, singletonList(buzzId)));
    }

    @SuppressWarnings("unchecked")
    public List<BuzzFavorite> getByUserIdAndBuzzIds(final int userId,
                                                    final List<Integer> buzzIds) {
        if (buzzIds.isEmpty()) {
            return emptyList();
        }
        final Query query = getSessionProvider().getSession().createQuery(
                "FROM " + clazz.getName() + " WHERE userId = :userId AND buzzId IN " + listObjectToSqlQueryInString(buzzIds));
        query.setParameter("userId", userId);
        return query.list();
    }

    public void favoriteBuzz(final int userId, final int buzzId) {
        favoriteBuzz(userId, singletonList(buzzId));
    }

    public void favoriteBuzz(final int userId,
                         final List<Integer> buzzIds) {
        if (buzzIds.isEmpty()) {
            return;
        }
        getSessionProvider().startTransaction();
        buzzIds.forEach(buzzId -> {
            final BuzzFavorite buzzLike = new BuzzFavorite.Builder().userId(userId).buzzId(buzzId).build();
            getSessionProvider().getSession().persist(buzzLike);
        });
        getSessionProvider().commitTransaction();
    }

    public void unfavoriteBuzz(final int userId, final int buzzId) {
        getSessionProvider().startTransaction();
        final Optional<BuzzFavorite> buzzLike = getByUserIdAndBuzzId(userId, buzzId);
        if (!buzzLike.isPresent()) {
            throw new BuzzException(BUZZ_FAVORITE_NOT_EXIST);
        }
        getSessionProvider().getSession().delete(buzzLike.get());
        getSessionProvider().commitTransaction();
    }
}
