package com.buzz.dao;

import com.buzz.model.User;
import org.apache.commons.lang.RandomStringUtils;
import org.hibernate.query.Query;

import java.util.Optional;

import static com.buzz.model.User.Status.ACTIVE;
import static java.util.Objects.requireNonNull;

/**
 * Created by toshikijahja on 6/7/17.
 */
public class UserDao extends BaseDao<User> {

    public UserDao(final SessionProvider sessionProvider) {
        super(sessionProvider, User.class);
    }

    public Optional<User> getByGuid(final String guid) {
        requireNonNull(guid);
        return getFirst(getByField("guid", guid));
    }

    public Optional<User> getByAlias(final String alias) {
        requireNonNull(alias);
        return getFirst(getByField("alias", alias));
    }

    public User createUser(final String guid) {
        requireNonNull(guid);
        getSessionProvider().startTransaction();
        final User user = new User.Builder()
                .status(ACTIVE)
                .guid(guid)
                .alias(generateRandomAlias())
                .build();
        getSessionProvider().getSession().persist(user);
        getSessionProvider().commitTransaction();
        return user;
    }

    private String generateRandomAlias() {
        return RandomStringUtils.random(6, true, true);
    }

    public void setAlias(final String alias, final int userId) {
        requireNonNull(alias);
        final Query query = getSessionProvider().getSession().createQuery(
                "UPDATE " + clazz.getName() + " SET alias = :alias WHERE id = :userId");
        query.setParameter("alias", alias);
        query.setParameter("userId", userId);
        query.executeUpdate();
    }
}
