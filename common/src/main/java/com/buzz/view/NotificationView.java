package com.buzz.view;

import com.buzz.NotificationProto;

/**
 * Created by toshikijahja on 11/23/17.
 */
public class NotificationView extends BaseView {

    private final NotificationProto.Notification notification;

    public NotificationView(final NotificationProto.Notification notification) {
        this.notification = notification;
    }

    public String getMessage() {
        return notification.getMessage();
    }

    public NotificationProto.Status getStatus() {
        return notification.getStatus();
    }

    public NotificationProto.Type getType() {
        return notification.getType();
    }

    public int getItemId() {
        return notification.getItemId();
    }

    public NotificationProto.Action getAction() {
        return notification.getAction();
    }
}
