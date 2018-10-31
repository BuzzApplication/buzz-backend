package com.buzz.dao;

import com.buzz.exception.BuzzException;
import com.buzz.model.CompanyEmail;

import java.util.List;
import java.util.Optional;

import static com.buzz.exception.BadRequest.INVALID_EMAIL_FORMAT;
import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;

/**
 * Created by toshikijahja on 6/7/17.
 */
public class CompanyEmailDao extends BaseDao<CompanyEmail> {

    public CompanyEmailDao(final SessionProvider sessionProvider) {
        super(sessionProvider, CompanyEmail.class);
    }

    public Optional<CompanyEmail> getByEmail(final String fullEmail) {
        requireNonNull(fullEmail);
        final List<String> email = asList(fullEmail.split("@"));
        if (email.size() != 2) {
            throw new BuzzException(INVALID_EMAIL_FORMAT);
        }
        return getFirst(getByField("email", email.get(1)));
    }
}
