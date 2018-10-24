package com.buzz.dao;

import com.buzz.model.UserEmail;
import org.hibernate.query.Query;

import java.util.List;
import java.util.Optional;

/**
 * Created by toshikijahja on 6/7/17.
 */
public class UserEmailDao extends BaseDao<UserEmail> {

    public UserEmailDao(final SessionProvider sessionProvider) {
        super(sessionProvider, UserEmail.class);
    }

    public List<UserEmail> getByUserId(final int userId) {
        return getByField("user.id", userId);
    }

    @SuppressWarnings("unchecked")
    public Optional<UserEmail> getByUserIdAndCompanyId(final int userId, final int companyId) {
        final Query query = getSessionProvider().getSession().createQuery(
                "FROM " + clazz.getName() + " WHERE user.id = :userId AND company.id = :companyId");
        query.setParameter("userId", userId);
        query.setParameter("companyId", companyId);
        return getFirst(query.list());
    }
}
