package com.buzz.dao;

import com.buzz.model.Buzz;
import com.buzz.model.Company;
import com.buzz.model.UserEmail;
import com.buzz.requestBody.BuzzRequestBody;
import org.hibernate.query.Query;

import java.util.List;

import static com.buzz.dao.BaseDao.Sort.DESC;
import static java.util.Objects.requireNonNull;

/**
 * Created by toshikijahja on 6/7/17.
 */
public class BuzzDao extends BaseDao<Buzz> {

    public BuzzDao(final SessionProvider sessionProvider) {
        super(sessionProvider, Buzz.class);
    }

    public List<Buzz> getByUserId(final int userId,
                                  final int start,
                                  final int limit) {
        return getByFieldSortedAndPaginated("userEmail.user.id", userId, "created", DESC, start, limit);
    }

    public List<Buzz> getByCompanyIds(final List<Integer> companyIds,
                                      final int start,
                                      final int limit) {
        return getByFieldSortedAndPaginated("companyId", companyIds, "created", DESC, start, limit);
    }

    public Buzz postBuzz(final BuzzRequestBody buzzRequestBody,
                         final UserEmail userEmail,
                         final Company company) {
        requireNonNull(buzzRequestBody);
        requireNonNull(userEmail);
        requireNonNull(company);
        getSessionProvider().startTransaction();
        final Buzz buzz = new Buzz.Builder()
                .alias(buzzRequestBody.isAnonymous() ? null : userEmail.getUser().getAlias())
                .companyId(company.getId())
                .text(buzzRequestBody.getText())
                .userEmail(userEmail)
                .build();
        getSessionProvider().getSession().persist(buzz);
        getSessionProvider().commitTransaction();
        return buzz;
    }

    public void updateCommentsCount(final int buzzId) {
        final Query query = getSessionProvider().getSession().createQuery(
                "UPDATE " + clazz.getName() + " SET commentsCount = commentsCount + 1 WHERE id = :buzzId");
        query.setParameter("buzzId", buzzId);
        query.executeUpdate();
    }

    public void increaseLikesCount(final int buzzId) {
        final Query query = getSessionProvider().getSession().createQuery(
                "UPDATE " + clazz.getName() + " SET likesCount = likesCount + 1 WHERE id = :buzzId");
        query.setParameter("buzzId", buzzId);
        query.executeUpdate();
    }

    public void decreaseLikesCount(final int buzzId) {
        final Query query = getSessionProvider().getSession().createQuery(
                "UPDATE " + clazz.getName() + " SET likesCount = likesCount - 1 WHERE id = :buzzId");
        query.setParameter("buzzId", buzzId);
        query.executeUpdate();
    }
}
