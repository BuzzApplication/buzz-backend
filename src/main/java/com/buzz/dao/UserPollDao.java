package com.buzz.dao;

import com.buzz.model.UserPoll;
import com.buzz.requestBody.PollRequestBody;
import org.hibernate.query.Query;

import java.util.List;
import java.util.Optional;

import static com.buzz.utils.QueryUtils.listObjectToSqlQueryInString;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.Objects.requireNonNull;

/**
 * Created by toshikijahja on 6/7/17.
 */
public class UserPollDao extends BaseDao<UserPoll> {

    public UserPollDao(final SessionProvider sessionProvider) {
        super(sessionProvider, UserPoll.class);
    }

    @SuppressWarnings("unchecked")
    public Optional<UserPoll> getByUserIdAndPollId(final int userId,
                                                   final int pollId) {
        return getFirst(getByUserIdAndPollIds(userId, singletonList(pollId)));
    }

    @SuppressWarnings("unchecked")
    public List<UserPoll> getByUserIdAndPollIds(final int userId,
                                                final List<Integer> pollIds) {
        if (pollIds.isEmpty()) {
            return emptyList();
        }
        final Query query = getSessionProvider().getSession().createQuery(
                "FROM " + clazz.getName() + " WHERE userId = :userId AND pollId IN " + listObjectToSqlQueryInString(pollIds));
        query.setParameter("userId", userId);
        return query.list();
    }

    @SuppressWarnings("unchecked")
    public List<UserPoll> getByUserIdAndBuzzId(final int userId,
                                               final int buzzId) {
        return getByUserIdAndBuzzIds(userId, singletonList(buzzId));
    }

    @SuppressWarnings("unchecked")
    public List<UserPoll> getByUserIdAndBuzzIds(final int userId,
                                                final List<Integer> buzzIds) {
        if (buzzIds.isEmpty()) {
            return emptyList();
        }
        final Query query = getSessionProvider().getSession().createQuery(
                "FROM " + clazz.getName() + " WHERE userId = :userId AND buzzId IN " + listObjectToSqlQueryInString(buzzIds));
        query.setParameter("userId", userId);
        return query.list();
    }

    public UserPoll poll(final PollRequestBody pollRequestBody,
                         final int userId) {
        requireNonNull(pollRequestBody);
        getSessionProvider().startTransaction();
        final UserPoll userPoll = new UserPoll.Builder()
                .pollId(pollRequestBody.getPollId())
                .userId(userId)
                .buzzId(pollRequestBody.getBuzzId())
                .build();
        getSessionProvider().getSession().persist(userPoll);
        getSessionProvider().commitTransaction();
        return userPoll;
    }

}
