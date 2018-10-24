package com.buzz.dao;

import com.buzz.model.Buzz;
import com.buzz.model.Company;
import com.buzz.model.UserEmail;
import com.buzz.requestBody.BuzzRequestBody;
import org.hibernate.query.Query;

import java.util.List;

import static com.buzz.dao.BaseDao.Sort.DESC;

/**
 * Created by toshikijahja on 6/7/17.
 */
public class BuzzDao extends BaseDao<Buzz> {

    public BuzzDao(final SessionProvider sessionProvider) {
        super(sessionProvider, Buzz.class);
    }

    public List<Buzz> getByCompanyId(final int companyId) {
        return getByFieldSorted("company.id", companyId, "created", DESC);
    }

    public Buzz postBuzz(final BuzzRequestBody buzzRequestBody,
                         final UserEmail userEmail,
                         final Company company) {
        getSessionProvider().startTransaction();
        final Buzz buzz = new Buzz.Builder()
                .alias(userEmail.getUser().getAlias())
                .company(company)
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
}
