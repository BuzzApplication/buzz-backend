package com.buzz.dao.persistent;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class SessionUtil {

    private static SessionUtil instance = new SessionUtil();
    private SessionFactory sessionFactory;

    public static SessionUtil getInstance(){
        return instance;
    }

    private SessionUtil() {
        final Configuration configuration = new Configuration();
        configuration.configure("hibernate.cfg.xml");
        sessionFactory = configuration.buildSessionFactory();
    }

    public static Session getSession(){
        return getInstance().sessionFactory.openSession();
    }

}