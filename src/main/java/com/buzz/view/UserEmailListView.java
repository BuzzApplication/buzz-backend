package com.buzz.view;

import com.buzz.model.User;
import com.buzz.model.UserEmail;

import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * Created by toshikijahja on 10/18/17.
 */
public class UserEmailListView extends BaseView {

    private final List<UserEmailView> userEmailViews;
    private final UserView userView;

    public UserEmailListView(final List<UserEmail> userEmails,
                             final User user) {
        this.userEmailViews = userEmails.stream().map(UserEmailView::new).collect(toList());
        this.userView = new UserView(user);
    }

    public List<UserEmailView> getUserEmails() {
        return userEmailViews;
    }

    public UserView getUser() {
        return userView;
    }

}
