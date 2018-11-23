package com.buzz.api.source;

import com.buzz.dao.AuthenticationDao;
import com.buzz.dao.CompanyEmailDao;
import com.buzz.dao.SessionProvider;
import com.buzz.dao.UserEmailDao;
import com.buzz.api.email.EmailClient;
import com.buzz.exception.BuzzException;
import com.buzz.model.Authentication;
import com.buzz.model.CompanyEmail;
import com.buzz.model.UserEmail;
import com.buzz.requestBody.AuthenticationRequestBody;
import com.buzz.requestBody.AuthenticationVerificationRequestBody;
import com.buzz.view.BaseView;
import com.buzz.view.TokenView;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Optional;

import static com.buzz.api.auth.JWTUtil.createJWT;
import static com.buzz.api.auth.JWTUtil.getSubject;
import static com.buzz.exception.BadRequest.COMPANY_EMAIL_NOT_EXIST;
import static com.buzz.exception.BadRequest.USER_EMAIL_NOT_EXIST;
import static com.buzz.exception.GenericError.FOUND;
import static com.buzz.model.Authentication.Status.UNVERIFIED;
import static java.util.Objects.requireNonNull;

/**
 * Created by toshikijahja on 10/22/18.
 */
@Path("/authentication")
public class AuthenticationSource {

    private final EmailClient emailClient = new EmailClient();

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public BaseView createAuthentication(final AuthenticationRequestBody authenticationRequestBody) throws Exception {
        requireNonNull(authenticationRequestBody.getEmail());
        requireNonNull(authenticationRequestBody.getPassword());
        try (final SessionProvider sessionProvider = new SessionProvider()) {
            final AuthenticationDao authenticationDao = new AuthenticationDao(sessionProvider);
            final UserEmailDao userEmailDao = new UserEmailDao(sessionProvider);
            final CompanyEmailDao companyEmailDao = new CompanyEmailDao(sessionProvider);

            // check if email is already being taken
            final Optional<UserEmail> userEmail = userEmailDao.getByEmail(authenticationRequestBody.getEmail());
            if (userEmail.isPresent()) {
                throw new BuzzException(USER_EMAIL_NOT_EXIST);
            }

            // check if email is whitelisted
            final Optional<CompanyEmail> companyEmail = companyEmailDao.getByEmail(authenticationRequestBody.getEmail());
            if (!companyEmail.isPresent()) {
                throw new BuzzException(COMPANY_EMAIL_NOT_EXIST);
            }

            final Authentication authentication = authenticationDao.createAuthentication(authenticationRequestBody);

            // send verification code email
//            emailClient.sendEmail(authentication.getEmail(), authentication.getVerificationCode());
        }
        return new BaseView();
    }

    @POST
    @Path("/authenticate")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public TokenView authenticateUser(final AuthenticationRequestBody authenticationRequestBody) throws Exception {
        requireNonNull(authenticationRequestBody.getEmail());
        requireNonNull(authenticationRequestBody.getPassword());

        try (final SessionProvider sessionProvider = new SessionProvider()) {
            final AuthenticationDao authenticationDao = new AuthenticationDao(sessionProvider);
            final Authentication authentication = authenticationDao.authenticate(
                    authenticationRequestBody.getEmail(),
                    authenticationRequestBody.getPassword());

            if (authentication.getStatus() == UNVERIFIED) {
                throw new BuzzException(FOUND);
            }

            final String token = issueToken(authentication.getGuid(), authenticationRequestBody.getEmail());
            return new TokenView(token);
        }

    }

    @POST
    @Path("/verify")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public TokenView verifyUser(final AuthenticationVerificationRequestBody authenticationVerificationRequestBody) throws Exception {
        requireNonNull(authenticationVerificationRequestBody.getEmail());
        requireNonNull(authenticationVerificationRequestBody.getPassword());
        requireNonNull(authenticationVerificationRequestBody.getVerificationCode());

        try (final SessionProvider sessionProvider = new SessionProvider()) {
            final AuthenticationDao authenticationDao = new AuthenticationDao(sessionProvider);
            final Authentication authentication = authenticationDao.authenticate(
                    authenticationVerificationRequestBody.getEmail(),
                    authenticationVerificationRequestBody.getPassword());

            if (authentication.getStatus() == UNVERIFIED) {
                authenticationDao.verify(authenticationVerificationRequestBody.getEmail(), authenticationVerificationRequestBody.getVerificationCode());
            }

            sessionProvider.startTransaction();
            authenticationDao.setVerified(authentication.getGuid());
            sessionProvider.commitTransaction();
            final String token = issueToken(authentication.getGuid(), authenticationVerificationRequestBody.getEmail());
            return new TokenView(token);
        }
    }

    private String issueToken(final String guid, final String email) {
        return createJWT(guid, getSubject(guid, email));
    }
}
