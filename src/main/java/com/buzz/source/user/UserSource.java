package com.buzz.source.user;

import com.buzz.auth.UserAuth;
import com.buzz.dao.SessionProvider;
import com.buzz.dao.UserDao;
import com.buzz.dao.UserEmailDao;
import com.buzz.model.User;
import com.buzz.model.UserEmail;
import com.buzz.view.UserEmailListView;
import com.buzz.view.UserView;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;
import java.util.List;

public class UserSource {

    @GET
    @UserAuth
    @Produces(MediaType.APPLICATION_JSON)
    public UserView getUser(@Context final SecurityContext securityContext) {
        try (final SessionProvider sessionProvider = new SessionProvider()) {
            final UserDao userDao = new UserDao(sessionProvider);
            final User user = userDao.getByGuid(securityContext.getUserPrincipal().getName()).get();
            return new UserView(user);
        }
    }

    @GET
    @UserAuth
    @Path("/email")
    @Produces(MediaType.APPLICATION_JSON)
    public UserEmailListView getUserEmail(@Context final SecurityContext securityContext) {
        try (final SessionProvider sessionProvider = new SessionProvider()) {
            final UserEmailDao userEmailDao = new UserEmailDao(sessionProvider);
            final UserDao userDao = new UserDao(sessionProvider);
            final User user = userDao.getByGuid(securityContext.getUserPrincipal().getName()).get();
            final List<UserEmail> userEmails = userEmailDao.getByUserId(user.getId());
            return new UserEmailListView(userEmails, user);
        }
    }
}