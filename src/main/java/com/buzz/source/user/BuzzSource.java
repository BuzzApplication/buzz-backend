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
import com.buzz.requestBody.BuzzLikeRequestBody;
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

    /**
     * Validate user works at the company, unless it is at `Everyone` company (companyId = 1)
     * @param userId
     * @param companyId
     * @param userEmailDao
     * @throws Exception
     */
    public static void validateUserWorksAtCompany(final int userId, final int companyId, final UserEmailDao userEmailDao) throws Exception {
        final Optional<UserEmail> userEmail = userEmailDao.getByUserIdAndCompanyId(userId, companyId);
        if (!userEmail.isPresent() && companyId != 1) {
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

            return new BuzzView(buzz, false);
        }
    }

    @POST
    @Path("/buzz/like")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public BuzzListView likeBuzz(final BuzzLikeRequestBody buzzLikeRequestBody,
                                 @Context final SecurityContext securityContext) throws Exception {
        try (final SessionProvider sessionProvider = new SessionProvider()) {
            final UserDao userDao = new UserDao(sessionProvider);
            final BuzzDao buzzDao = new BuzzDao(sessionProvider);
            final BuzzLikeDao buzzLikeDao = new BuzzLikeDao(sessionProvider);

            final User user = userDao.getByGuid(securityContext.getUserPrincipal().getName()).get();
            final List<Buzz> buzzList = buzzDao.getByIds(buzzLikeRequestBody.getBuzzIds());
            if (buzzList.size() != buzzLikeRequestBody.getBuzzIds().size()) {
                throw new Exception();
            }

            // liked buzz
            final List<BuzzLike> buzzLiked = buzzLikeDao.getByUserIdAndBuzzIds(user.getId(), buzzLikeRequestBody.getBuzzIds());
            final List<Integer> buzzLikedIds = buzzLiked.stream().map(BuzzLike::getBuzzId).collect(toList());

            // making sure to not like liked buzz
            final List<Integer> filteredBuzzIds = buzzLikeRequestBody.getBuzzIds().stream()
                    .filter(buzzId -> !buzzLikedIds.contains(buzzId)).collect(toList());

            sessionProvider.startTransaction();
            buzzLikeDao.likeBuzz(user.getId(), filteredBuzzIds);
            filteredBuzzIds.forEach(buzzDao::updateLikesCount);
            sessionProvider.commitTransaction();

            final List<Buzz> updateBuzzList = buzzDao.getByIds(filteredBuzzIds);
            final List<BuzzLike> updatedBuzzLiked = buzzLikeDao.getByUserIdAndBuzzIds(user.getId(), filteredBuzzIds);
            final Set<Integer> updatedBuzzLikedIds = updatedBuzzLiked.stream().map(BuzzLike::getBuzzId).collect(toSet());

            final List<BuzzView> buzzViews = updateBuzzList.stream()
                    .map(buzz -> new BuzzView(buzz, updatedBuzzLikedIds.contains(buzz.getId()))).collect(toList());
            return new BuzzListView(buzzViews);
        }
    }

}