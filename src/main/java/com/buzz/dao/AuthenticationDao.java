package com.buzz.dao;

import com.buzz.model.Authentication;
import com.buzz.requestBody.AuthenticationRequestBody;
import org.hibernate.query.Query;

import javax.naming.AuthenticationException;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import static com.buzz.model.Authentication.Status.UNVERIFIED;
import static com.buzz.model.Authentication.Status.VERIFIED;
import static java.util.Objects.requireNonNull;

/**
 * Created by toshikijahja on 6/7/17.
 */
public class AuthenticationDao extends BaseDao<Authentication> {

    public AuthenticationDao(final SessionProvider sessionProvider) {
        super(sessionProvider, Authentication.class);
    }

    public Authentication createAuthentication(final AuthenticationRequestBody authenticationRequestBody) {
        requireNonNull(authenticationRequestBody);
        getSessionProvider().startTransaction();
        final Authentication authentication = new Authentication.Builder()
                .email(authenticationRequestBody.getEmail())
                .password(authenticationRequestBody.getPassword())
                .status(UNVERIFIED)
                .verificationCode(generateVerificationToken())
                .build();
        getSessionProvider().getSession().persist(authentication);
        getSessionProvider().commitTransaction();
        return authentication;
    }

    private String generateVerificationToken() {
        final Random rand = new Random();
        return String.format("%04d%n", rand.nextInt(10000));
    }

    public Authentication authenticate(final String email, final String password) throws Exception {
        requireNonNull(email);
        requireNonNull(password);
        final List<Authentication> authenticationList = getByField("email", email);
        if (authenticationList.size() != 1) {
            throw new AuthenticationException();
        }

        final Authentication authentication = authenticationList.get(0);
        if (!Objects.equals(authentication.getPassword(), password)) {
            throw new AuthenticationException();
        }

        return authentication;
    }

    public Authentication verify(final String email, final String verificationCode) throws Exception {
        requireNonNull(email);
        requireNonNull(verificationCode);
        final List<Authentication> authenticationList = getByField("email", email);
        if (authenticationList.size() != 1) {
            throw new AuthenticationException();
        }

        final Authentication authentication = authenticationList.get(0);
        if (!Objects.equals(authentication.getVerificationCode(), verificationCode)) {
            throw new AuthenticationException();
        }

        return authentication;
    }

    public void setVerified(final String guid) {
        requireNonNull(guid);
        final Query query = getSessionProvider().getSession().createQuery(
                "UPDATE " + clazz.getName() + " SET status = '" + VERIFIED.toString() + "' WHERE guid = :guid");
        query.setParameter("guid", guid);
        query.executeUpdate();
    }
}
