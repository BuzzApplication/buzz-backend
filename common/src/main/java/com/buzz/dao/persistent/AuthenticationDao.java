package com.buzz.dao.persistent;

import com.buzz.exception.BuzzException;
import com.buzz.model.Authentication;
import com.buzz.requestBody.AuthenticationRequestBody;
import org.hibernate.query.Query;
import org.mindrot.jbcrypt.BCrypt;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;

import static com.buzz.exception.Unauthorized.UNAUTHORIZED;
import static com.buzz.model.Authentication.Status.UNVERIFIED;
import static com.buzz.model.Authentication.Status.VERIFIED;
import static java.util.Objects.requireNonNull;
import static org.mindrot.jbcrypt.BCrypt.checkpw;
import static org.mindrot.jbcrypt.BCrypt.hashpw;

/**
 * Created by toshikijahja on 6/7/17.
 */
public class AuthenticationDao extends BaseDao<Authentication> {

    public AuthenticationDao(final SessionProvider sessionProvider) {
        super(sessionProvider, Authentication.class);
    }

    public Optional<Authentication> getByGuid(final String guid) {
        requireNonNull(guid);
        return getFirst(getByField("guid", guid));
    }

    public Authentication createAuthentication(final AuthenticationRequestBody authenticationRequestBody) {
        requireNonNull(authenticationRequestBody);
        getSessionProvider().startTransaction();
        final Authentication authentication = new Authentication.Builder()
                .email(authenticationRequestBody.getEmail())
                .password(hashpw(authenticationRequestBody.getPassword(), BCrypt.gensalt(12)))
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

    public Authentication authenticate(final String email, final String password) {
        requireNonNull(email);
        requireNonNull(password);
        final List<Authentication> authenticationList = getByField("email", email);
        if (authenticationList.size() != 1) {
            throw new BuzzException(UNAUTHORIZED);
        }

        final Authentication authentication = authenticationList.get(0);
        if (!checkpw(password, authentication.getPassword())) {
            throw new BuzzException(UNAUTHORIZED);
        }

        return authentication;
    }

    public Authentication verify(final String email, final String verificationCode) {
        requireNonNull(email);
        requireNonNull(verificationCode);
        final List<Authentication> authenticationList = getByField("email", email);
        if (authenticationList.size() != 1) {
            throw new BuzzException(UNAUTHORIZED);
        }

        final Authentication authentication = authenticationList.get(0);
        if (!Objects.equals(authentication.getVerificationCode(), verificationCode)) {
            throw new BuzzException(UNAUTHORIZED);
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
