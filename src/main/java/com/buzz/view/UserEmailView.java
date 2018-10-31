package com.buzz.view;

import com.buzz.model.UserEmail;


/**
 * Created by toshikijahja on 10/18/17.
 */
public class UserEmailView extends BaseView {

    private final UserEmail userEmail;

    public UserEmailView(final UserEmail userEmail) {
        this.userEmail = userEmail;
    }

    public int getId() {
        return userEmail.getId();
    }

    public String getEmail() {
        return userEmail.getEmail();
    }

    public CompanyView getCompany() {
        return new CompanyView(userEmail.getCompany());
    }

}
