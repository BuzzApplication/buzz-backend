package com.buzz.auth;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.security.Principal;

import static com.buzz.auth.JWTUtil.parseJWT;
import static javax.ws.rs.core.HttpHeaders.WWW_AUTHENTICATE;
import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;
import static javax.ws.rs.core.Response.status;

/**
 * Created by toshikijahja on 10/22/18.
 */
@UserAuth
@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {

    private static final String REALM = "example";
    private static final String AUTHENTICATION_SCHEME = "Bearer";

    @Override
    public void filter(final ContainerRequestContext requestContext) throws IOException {
        // Get the Authorization header from the request
        final String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

        // Validate the Authorization header
        if (!isTokenBasedAuthentication(authorizationHeader)) {
            abortWithUnauthorized(requestContext);
            return;
        }

        // Extract the token from the Authorization header
        final String token = authorizationHeader.substring(AUTHENTICATION_SCHEME.length()).trim();

        try {
            final String guid = validateToken(token);

            final SecurityContext currentSecurityContext = requestContext.getSecurityContext();
            requestContext.setSecurityContext(new SecurityContext() {
                @Override
                public Principal getUserPrincipal() {
                    return () -> guid;
                }

                @Override
                public boolean isUserInRole(String role) {
                    return true;
                }

                @Override
                public boolean isSecure() {
                    return currentSecurityContext.isSecure();
                }

                @Override
                public String getAuthenticationScheme() {
                    return AUTHENTICATION_SCHEME;
                }
            });

        } catch (final Exception e) {
            abortWithUnauthorized(requestContext);
        }
    }

    /**
     * Check if the Authorization header is valid
     * It must not be null and must be prefixed with "Bearer" plus a whitespace
     * The authentication scheme comparison must be case-insensitive
     * @param authorizationHeader
     * @return
     */
    private boolean isTokenBasedAuthentication(final String authorizationHeader) {
        return authorizationHeader != null &&
                authorizationHeader.toLowerCase().startsWith(AUTHENTICATION_SCHEME.toLowerCase() + " ");
    }

    /**
     * Abort the filter chain with a 401 status code response
     * The WWW-Authenticate header is sent along with the response
     * @param requestContext
     */
    private void abortWithUnauthorized(final ContainerRequestContext requestContext) {
        requestContext.abortWith(status(UNAUTHORIZED)
                        .header(WWW_AUTHENTICATE, AUTHENTICATION_SCHEME + " realm=\"" + REALM + "\"")
                        .build());
    }

    /**
     * Check if the token was issued by the server and if it's not expired
     * Throw an Exception if the token is invalid
     * @param token
     * @throws Exception
     */
    private String validateToken(final String token) throws Exception {
        return parseJWT(token);
    }
}
