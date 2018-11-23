package com.buzz.source.user;

import com.buzz.auth.UserAuth;

import javax.ws.rs.Path;

@Path("/user")
@UserAuth
public class NotificationSource {

//    @GET
//    @Path("/notification")
//    @Produces(MediaType.APPLICATION_JSON)
//    public UserView getNotification(@Context final SecurityContext securityContext) {
//        try (final SessionProvider sessionProvider = new SessionProvider()) {
//            final UserDao userDao = new UserDao(sessionProvider);
//            final User user = userDao.getByGuid(securityContext.getUserPrincipal().getName()).get();
//            return new UserView(user);
//        }
//    }
}
