package com.buzz.source.user;

import com.buzz.auth.UserAuth;
import com.buzz.dao.BuzzDao;
import com.buzz.dao.BuzzLikeDao;
import com.buzz.dao.CompanyDao;
import com.buzz.dao.SessionProvider;
import com.buzz.dao.UserDao;
import com.buzz.dao.UserEmailDao;
import com.buzz.model.Buzz;
import com.buzz.model.BuzzLike;
import com.buzz.model.Company;
import com.buzz.model.User;
import com.buzz.model.UserEmail;
import com.buzz.requestBody.BuzzRequestBody;
import com.buzz.view.BuzzListView;
import com.buzz.view.BuzzView;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

@Path("/user")
@UserAuth
public class BuzzSource {

    @GET
    @Path("/buzz")
    @Produces(MediaType.APPLICATION_JSON)
    public BuzzListView getBuzzByCompanyId(@QueryParam("companyId") final int companyId,
                                           @Context final SecurityContext securityContext) throws Exception {
        try (final SessionProvider sessionProvider = new SessionProvider()) {
            final UserDao userDao = new UserDao(sessionProvider);
            final BuzzDao buzzDao = new BuzzDao(sessionProvider);
            final BuzzLikeDao buzzLikeDao = new BuzzLikeDao(sessionProvider);
            final UserEmailDao userEmailDao = new UserEmailDao(sessionProvider);

            final User user = userDao.getByGuid(securityContext.getUserPrincipal().getName()).get();
            validateUserWorksAtCompany(user.getId(), companyId, userEmailDao);

            final List<Buzz> buzzList = buzzDao.getByCompanyId(companyId);
            final List<Integer> buzzIds = buzzList.stream().map(Buzz::getId).collect(toList());
            final List<BuzzLike> buzzLikes = buzzLikeDao.getByUserIdAndBuzzIds(user.getId(), buzzIds);
            final Set<Integer> buzzLikeIds = buzzLikes.stream().map(BuzzLike::getBuzzId).collect(toSet());

            final List<BuzzView> buzzViews = buzzList.stream()
                    .map(buzz -> new BuzzView(buzz, buzzLikeIds.contains(buzz.getId())))
                    .collect(toList());

            return new BuzzListView(buzzViews);
        }
    }

    public static void validateUserWorksAtCompany(final int userId, final int companyId, final UserEmailDao userEmailDao) throws Exception {
        final Optional<UserEmail> userEmail = userEmailDao.getByUserIdAndCompanyId(userId, companyId);
        if (!userEmail.isPresent()) {
            throw new Exception();
        }
    }

    @POST
    @Path("/buzz")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public BuzzView postBuzz(final BuzzRequestBody buzzRequestBody,
                             @Context final SecurityContext securityContext) throws Exception {
        try (final SessionProvider sessionProvider = new SessionProvider()) {
            final UserDao userDao = new UserDao(sessionProvider);
            final BuzzDao buzzDao = new BuzzDao(sessionProvider);
            final UserEmailDao userEmailDao = new UserEmailDao(sessionProvider);
            final CompanyDao companyDao = new CompanyDao(sessionProvider);

            final User user = userDao.getByGuid(securityContext.getUserPrincipal().getName()).get();
            final Optional<Company> company = companyDao.getByIdOptional(buzzRequestBody.getCompanyId());

            if (!company.isPresent()) {
                throw new Exception();
            }

            final Optional<UserEmail> userEmail = userEmailDao.getByIdOptional(buzzRequestBody.getUserEmailId());
            if (!userEmail.isPresent()) {
                throw new Exception();
            }

            if (userEmail.get().getUser().getId() != user.getId()) {
                throw new Exception();
            }

            requireNonNull(buzzRequestBody.getText());
            validateUserWorksAtCompany(user.getId(), company.get().getId(), userEmailDao);

            final Buzz buzz = buzzDao.postBuzz(buzzRequestBody, userEmail.get(), company.get());

            return new BuzzView(buzz);
        }
    }

}