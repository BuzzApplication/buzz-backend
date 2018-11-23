package com.buzz.source.user;

import com.buzz.NotificationProto;
import com.buzz.auth.UserAuth;
import com.buzz.dao.persistent.SessionProvider;
import com.buzz.dao.persistent.UserDao;
import com.buzz.dao.redis.NotificationRedisHelper;
import com.buzz.model.User;
import com.buzz.view.NotificationView;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Path("/user")
@UserAuth
public class NotificationSource {

    private final NotificationRedisHelper notificationRedisHelper;

    public NotificationSource() {
        notificationRedisHelper = new NotificationRedisHelper();
    }

    @GET
    @Path("/notification")
    @Produces(MediaType.APPLICATION_JSON)
    public List<NotificationView> getNotification(@Context final SecurityContext securityContext) {
        try (final SessionProvider sessionProvider = new SessionProvider()) {
            final UserDao userDao = new UserDao(sessionProvider);
            final User user = userDao.getByGuid(securityContext.getUserPrincipal().getName()).get();
            final List<NotificationProto.Notification> notifications = notificationRedisHelper.getNotification(user.getId());
            final List<NotificationView> notificationViews = notifications.stream().map(NotificationView::new).collect(toList());
            return notificationViews;
        }
    }
}
