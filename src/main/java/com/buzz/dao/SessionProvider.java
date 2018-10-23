package com.buzz.dao;

import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 * Created by toshikijahja on 6/8/17.
 */
public class SessionProvider implements AutoCloseable {

    private final Session session;
    private Transaction transaction;
    private int transactionDepth;

    public SessionProvider() {
        this.session = SessionUtil.getSession();
        this.transactionDepth = 0;
    }

    public Session getSession() {
        return session;
    }

    public void startTransaction() {
        if (null != transaction && transaction.isActive()) {
            transactionDepth++;
            return;
        }
        transaction = session.beginTransaction();
        transactionDepth++;
    }

    public void commitTransaction() {
        if (1 == transactionDepth) {
            transaction.commit();
            transactionDepth--;
        } else if (transactionDepth > 1) {
            transactionDepth--;
        } else {
            throw new IllegalStateException("Trying to commit transaction without starting transaction");
        }
    }

    @Override
    public void close() {
        if (null != transaction && transaction.isActive()) {
            transaction.rollback();
        }
        session.close();
    }

}
