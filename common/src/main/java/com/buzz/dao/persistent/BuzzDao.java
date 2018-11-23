package com.buzz.dao.persistent;

import com.buzz.model.Buzz;
import com.buzz.model.Company;
import com.buzz.model.Poll;
import com.buzz.model.UserEmail;
import com.buzz.requestBody.BuzzRequestBody;
import org.hibernate.query.Query;

import java.time.Instant;
import java.util.List;

import static com.buzz.utils.QueryUtils.listObjectToSqlQueryInString;
import static java.lang.String.join;
import static java.time.temporal.ChronoUnit.DAYS;
import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;

/**
 * Created by toshikijahja on 6/7/17.
 */
public class BuzzDao extends BaseDao<Buzz> {

    private final PollDao pollDao;

    public BuzzDao(final SessionProvider sessionProvider) {
        super(sessionProvider, Buzz.class);
        pollDao = new PollDao(sessionProvider);
    }

    public List<Buzz> getByUserId(final int userId,
                                  final int start,
                                  final int limit) {
        return getByFieldSortedAndPaginated("userEmail.user.id", userId, "created", Sort.DESC, start, limit);
    }

    public List<Buzz> getByIds(final List<Integer> buzzIds,
                               final int start,
                               final int limit) {
        return getByFieldSortedAndPaginated("id", buzzIds, "created", Sort.DESC, start, limit);
    }

    @SuppressWarnings("unchecked")
    public List<Buzz> getTrending(final int maxDays,
                                  final int limit) {
        final Query query = getSessionProvider().getSession().createQuery(
                "FROM " + clazz.getName() + " WHERE created >= :maxDays ORDER BY commentsCount DESC, likesCount DESC");
        query.setParameter("maxDays", Instant.now().minus(maxDays, DAYS));
        query.setMaxResults(limit);
        return query.list();
    }

    public List<Buzz> getByCompanyIds(final List<Integer> companyIds,
                                      final int start,
                                      final int limit) {
        return getByFieldSortedAndPaginated("companyId", companyIds, "created", Sort.DESC, start, limit);
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
        final List<Poll> polls = pollDao.savePolls(buzzRequestBody, buzz);
        buzz.setPolls(polls);
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

    @SuppressWarnings("unchecked")
    public List<Buzz> search(final String text,
                             final List<Integer> companyIds,
                             final int start,
                             final int limit) {
        final String queryString = "SELECT b FROM Buzz b " +
                "INNER JOIN Company c ON c.id = b.companyId " +
                "WHERE (b.text LIKE :searchedText " +
                "OR c.name LIKE :searchedText) " +
                "AND c.id IN " + listObjectToSqlQueryInString(companyIds) + " " +
                "ORDER BY b.commentsCount DESC, b.likesCount DESC";
        final Query query = getSessionProvider().getSession().createQuery(queryString);
        query.setParameter("searchedText", buildSearchedString(text));
        query.setFirstResult(start);
        query.setMaxResults(limit);
        return query.list();
    }

    private String buildSearchedString(final String text) {
        final List<String> textList = asList(text.split("\\s+"));
        return "%" + join("%", textList) + "%";
    }
}
