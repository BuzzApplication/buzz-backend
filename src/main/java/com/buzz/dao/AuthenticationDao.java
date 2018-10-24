package com.buzz.dao;

import com.buzz.model.Authentication;
import com.buzz.requestBody.AuthenticationRequestBody;

import javax.naming.AuthenticationException;
import java.util.List;
import java.util.Objects;

/**
 * Created by toshikijahja on 6/7/17.
 */
public class AuthenticationDao extends BaseDao<Authentication> {

    public AuthenticationDao(final SessionProvider sessionProvider) {
        super(sessionProvider, Authentication.class);
    }

    public void createAuthentication(final AuthenticationRequestBody authenticationRequestBody) {
        getSessionProvider().startTransaction();
        final Authentication authentication = new Authentication.Builder()
                .email(authenticationRequestBody.getEmail())
                .password(authenticationRequestBody.getPassword())
                .build();
        getSessionProvider().getSession().persist(authentication);
        getSessionProvider().commitTransaction();
    }

    public String authenticate(final String email, final String password) throws Exception {
        final List<Authentication> authenticationList = getByField("email", email);
        if (authenticationList.size() != 1) {
            throw new AuthenticationException();
        }

        final Authentication authentication = authenticationList.get(0);
        if (!Objects.equals(authentication.getPassword(), password)) {
            throw new AuthenticationException();
        }

        return authentication.getGuid();
    }
}
