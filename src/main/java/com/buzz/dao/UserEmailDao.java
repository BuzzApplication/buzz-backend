package com.buzz.dao;

import com.buzz.model.UserEmail;

import java.util.List;

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
}
