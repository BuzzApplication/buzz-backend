package com.buzz.api.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.sql.Date;
import java.time.Instant;

import static java.time.temporal.ChronoUnit.HOURS;
import static org.testng.Assert.assertEquals;

/**
 * Created by toshikijahja on 10/23/18.
 */
public class JWTUtil {

    private static final String KEY = "buzz-token-sign-key";
    private static final String ISSUER = "buzz-backend";

    public static String createJWT(final String id, final String subject) {
        return createJWT(id, subject, null);
    }

    public static String createJWT(final String id, final String subject, final Integer expirationInHours) {
        final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

        final byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(KEY);
        final Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());
        final Instant now = Instant.now();

        final JwtBuilder builder = Jwts.builder()
                .setId(id)
                .setSubject(subject)
                .setIssuer(ISSUER)
                .setIssuedAt(Date.from(now))
                .signWith(signatureAlgorithm, signingKey);

        if (expirationInHours != null) {
            builder.setExpiration(Date.from(now.plus(expirationInHours, HOURS)));
        }

        //Builds the JWT and serializes it to a compact, URL-safe string
        return builder.compact();
    }

    public static String parseJWT(final String jwt) {
        final Claims claims = Jwts.parser()
                .setSigningKey(DatatypeConverter.parseBase64Binary(KEY))
                .parseClaimsJws(jwt).getBody();
        assertEquals(claims.getIssuer(), ISSUER);
        return claims.getId();
    }

    public static String getSubject(final String guid, final String email) {
        return guid + "###" + email;
    }
}
