package com.buzz.dao;

import jersey.repackaged.com.google.common.collect.Iterables;
import org.hibernate.query.Query;

import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static java.util.Objects.requireNonNull;

/**
 * Created by toshikijahja on 7/29/17.
 */
public class BaseDao<T> {

    private static final String DATA = "data";
    private static final String SORT_FIELD = "sortField";

    private final SessionProvider sessionProvider;
    protected final Class clazz;

    protected BaseDao(final SessionProvider sessionProvider,
                   final Class clazz) {
        this.sessionProvider = sessionProvider;
        this.clazz = clazz;
    }

    public SessionProvider getSessionProvider() {
        return this.sessionProvider;
    }

    public enum Sort {
        ASC, DESC
    }

    @SuppressWarnings("unchecked")
    public T getById(final int id) {
        return (T) getSessionProvider().getSession().load(clazz, id);
    }

    @SuppressWarnings("unchecked")
    public Optional<T> getByIdOptional(final int id) {
        final T result = (T) getSessionProvider().getSession().get(clazz, id);
        return result == null ? Optional.empty() : Optional.of(result);
    }

    @SuppressWarnings("unchecked")
    public List<T> getByIds(final List<Integer> ids) {
        if (ids.isEmpty()) {
            return emptyList();
        }
        final Query query = getSessionProvider().getSession().createQuery("FROM " + clazz.getName() + " WHERE id IN :ids");
        query.setParameterList("ids", ids);
        return query.list();
    }

    @SuppressWarnings("unchecked")
    protected List<T> getByField(final String field, final String data) {
        requireNonNull(field);
        final Query query = getSessionProvider().getSession().createQuery("FROM " + clazz.getName() + " WHERE " + field + " = :" + DATA);
        query.setParameter(DATA, data);
        return query.list();
    }

    @SuppressWarnings("unchecked")
    protected List<T> getByField(final String field, final Integer data) {
        requireNonNull(field);
        final Query query = getSessionProvider().getSession().createQuery("FROM " + clazz.getName() + " WHERE " + field + " = :" + DATA);
        query.setParameter(DATA, data);
        return query.list();
    }

    @SuppressWarnings("unchecked")
    protected List<T> getByField(final String field, final Boolean data) {
        requireNonNull(field);
        final Query query = getSessionProvider().getSession().createQuery("FROM " + clazz.getName() + " WHERE " + field + " = :" + DATA);
        query.setParameter(DATA, data);
        return query.list();
    }

    @SuppressWarnings("unchecked")
    protected List<T> getByFieldSorted(final String field, final String data, final String sortField, final Sort sort) {
        requireNonNull(field);
        requireNonNull(sortField);
        requireNonNull(sort);
        final Query query = getSessionProvider().getSession().createQuery(
                "FROM " + clazz.getName() + " WHERE " + field + " = :" + DATA + " ORDER BY :" + SORT_FIELD + " " + sort);
        query.setParameter(DATA, data);
        query.setParameter(SORT_FIELD, sortField);
        return query.list();
    }

    @SuppressWarnings("unchecked")
    protected List<T> getByFieldSorted(final String field, final Integer data, final String sortField, final Sort sort) {
        requireNonNull(field);
        requireNonNull(sortField);
        requireNonNull(sort);
        final Query query = getSessionProvider().getSession().createQuery(
                "FROM " + clazz.getName() + " WHERE " + field + " = :" + DATA + " ORDER BY :" + SORT_FIELD + " " + sort);
        query.setParameter(DATA, data);
        query.setParameter(SORT_FIELD, sortField);
        return query.list();
    }

    @SuppressWarnings("unchecked")
    protected List<T> getByFieldSorted(final String field, final Boolean data, final String sortField, final Sort sort) {
        requireNonNull(field);
        requireNonNull(sortField);
        requireNonNull(sort);
        final Query query = getSessionProvider().getSession().createQuery(
                "FROM " + clazz.getName() + " WHERE " + field + " = :" + DATA + " ORDER BY :" + SORT_FIELD + " " + sort);
        query.setParameter(DATA, data);
        query.setParameter(SORT_FIELD, sortField);
        return query.list();
    }

    protected Optional<T> getFirst(final List<T> list) {
        return Optional.ofNullable(Iterables.getFirst(list, null));
    }

}
