package com.buzz.source;

import com.buzz.dao.AuthenticationDao;
import com.buzz.dao.SessionProvider;
import com.buzz.requestBody.AuthenticationRequestBody;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static com.buzz.auth.JWTUtil.createJWT;
import static com.buzz.auth.JWTUtil.getSubject;
import static java.util.Objects.requireNonNull;

/**
 * Created by toshikijahja on 10/22/18.
 */
@Path("/authentication")
public class AuthenticationSource {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createAuthentication(final AuthenticationRequestBody authenticationRequestBody) {
        requireNonNull(authenticationRequestBody.getEmail());
        requireNonNull(authenticationRequestBody.getPassword());
        try (final SessionProvider sessionProvider = new SessionProvider()) {
            final AuthenticationDao authenticationDao = new AuthenticationDao(sessionProvider);
            authenticationDao.createAuthentication(authenticationRequestBody);
        }
        return Response.ok().build();
    }

    @POST
    @Path("/authenticate")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response authenticateUser(final AuthenticationRequestBody authenticationRequestBody) {
        requireNonNull(authenticationRequestBody.getEmail());
        requireNonNull(authenticationRequestBody.getPassword());

        try {
            final String guid = authenticate(authenticationRequestBody.getEmail(), authenticationRequestBody.getPassword());
            final String token = issueToken(guid, authenticationRequestBody.getEmail());
            return Response.ok(token).build();
        } catch (final Exception e) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
    }

    private String authenticate(final String email, final String password) throws Exception {
        try (final SessionProvider sessionProvider = new SessionProvider()) {
            final AuthenticationDao authenticationDao = new AuthenticationDao(sessionProvider);
            return authenticationDao.authenticate(email, password);
        }
    }

    private String issueToken(final String guid, final String email) {
        return createJWT(guid, getSubject(guid, email));
    }
}
