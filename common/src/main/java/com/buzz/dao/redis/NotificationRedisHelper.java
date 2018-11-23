package com.buzz.dao.redis;

import com.buzz.NotificationProto;
import com.buzz.model.Buzz;
import com.buzz.model.UserEmail;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import redis.clients.jedis.Jedis;

import java.util.List;

import static com.buzz.dao.redis.NotificationMessage.NOTIFICATION_COMMENT_MESSAGE;
import static com.buzz.dao.redis.RedisKeyBuilder.getNotificationKey;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

/**
 * Created by toshikijahja on 11/23/18.
 */
public class NotificationRedisHelper {

    private final Jedis jedis;
    private static final int MAX_CHAR_LIMIT = 50;

    public NotificationRedisHelper() {
        jedis = new Jedis();
    }

    public List<NotificationProto.Notification> getNotification(final int userId) {
        final List<String> notificationsString = jedis.lrange(getNotificationKey(userId), 0, -1);

        return notificationsString.stream()
            .map(notificationString -> {
                final NotificationProto.Notification.Builder notificationBuilder = NotificationProto.Notification.newBuilder();
                try {
                    JsonFormat.parser().merge(notificationString, notificationBuilder);
                } catch (final InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }

                return notificationBuilder.build();
            }).collect(toList());
    }

    public void addNotificationComment(final Buzz buzz, final UserEmail userEmail) throws InvalidProtocolBufferException {
        requireNonNull(buzz);
        requireNonNull(userEmail);
        final String message = buildNotificationMessage(buzz.getText(), userEmail.getCompany().getName());
        final NotificationProto.Notification notification = NotificationProto.Notification.newBuilder()
            .setItemId(buzz.getId())
            .setType(NotificationProto.Type.BUZZ)
            .setAction(NotificationProto.Action.COMMENT_ACTION)
            .setMessage(message)
            .setStatus(NotificationProto.Status.UNREAD)
            .build();
        jedis.lpush(getNotificationKey(userEmail.getUser().getId()), JsonFormat.printer().print(notification));
    }

    public String buildNotificationMessage(final String buzzText, final String companyName) {
        requireNonNull(buzzText);
        requireNonNull(companyName);
        final String shortenedBuzz = getBuzzTextShortenedIfNeeded(buzzText);
        return format(NOTIFICATION_COMMENT_MESSAGE, companyName, shortenedBuzz);
    }

    public String getBuzzTextShortenedIfNeeded(final String buzzText) {
        requireNonNull(buzzText);
        if (buzzText.length() <= 50) {
            return buzzText;
        }

        final List<String> words = asList(buzzText.split("\\s+"));
        StringBuilder stringBuilder = new StringBuilder();
        for (final String word : words) {
            if (stringBuilder.length() >= MAX_CHAR_LIMIT) {
                stringBuilder.append("...");
                break;
            }
            stringBuilder.append(word).append(" ");
        }

        return stringBuilder.toString();
    }
}
