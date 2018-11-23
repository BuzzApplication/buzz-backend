package com.buzz.dao.redis;

import static java.lang.String.format;

/**
 * Created by toshikijahja on 11/23/18.
 */
public class RedisKeyBuilder {

    public static final String NOTIFICATION = "NOTIF_%s";

    public static String getNotificationKey(final int userId) {
        return format(NOTIFICATION, String.valueOf(userId));
    }

}
