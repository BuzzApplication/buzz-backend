package com.buzz.source.user;

import com.buzz.auth.UserAuth;
import com.buzz.dao.BuzzDao;
import com.buzz.dao.BuzzFavoriteDao;
import com.buzz.dao.BuzzLikeDao;
import com.buzz.dao.CompanyDao;
import com.buzz.dao.SessionProvider;
import com.buzz.dao.UserDao;
import com.buzz.dao.UserEmailDao;
import com.buzz.exception.BuzzException;
import com.buzz.model.Buzz;
import com.buzz.model.BuzzFavorite;
import com.buzz.model.BuzzLike;
import com.buzz.model.Company;
import com.buzz.model.User;
import com.buzz.model.UserEmail;
import com.buzz.requestBody.BuzzFavoriteRequestBody;
import com.buzz.requestBody.BuzzLikeRequestBody;
import com.buzz.requestBody.BuzzRequestBody;
import com.buzz.view.BuzzListWithCompanyView;
import com.buzz.view.BuzzView;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static com.buzz.exception.BadRequest.BUZZ_NOT_EXIST;
import static com.buzz.exception.BadRequest.COMPANY_NOT_EXIST;
import static com.buzz.exception.BadRequest.TEXT_CANNOT_BE_EMPTY;
import static com.buzz.exception.BadRequest.USER_EMAIL_NOT_EXIST;
import static com.buzz.exception.BadRequest.USER_EMAIL_NOT_MATCH;
import static com.buzz.exception.BadRequest.USER_NOT_IN_COMPANY;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.apache.commons.lang.StringUtils.isEmpty;

@Path("/user")
@UserAuth
public class BuzzSource {

    @GET
    @Path("/buzz")
    @Produces(MediaType.APPLICATION_JSON)
    public List<BuzzListWithCompanyView> getBuzzByCompanyId(@QueryParam("companyIds") final List<Integer> companyIds,
                                                            @QueryParam("start") @DefaultValue("0") final int start,
                                                            @QueryParam("limit") @DefaultValue("50") final int limit,
                                                            @Context final SecurityContext securityContext) {
        if (companyIds.isEmpty()) {
            return emptyList();
        }
        try (final SessionProvider sessionProvider = new SessionProvider()) {
            final UserDao userDao = new UserDao(sessionProvider);
            final BuzzDao buzzDao = new BuzzDao(sessionProvider);
            final BuzzLikeDao buzzLikeDao = new BuzzLikeDao(sessionProvider);
            final BuzzFavoriteDao buzzFavoriteDao = new BuzzFavoriteDao(sessionProvider);
            final UserEmailDao userEmailDao = new UserEmailDao(sessionProvider);

            final User user = userDao.getByGuid(securityContext.getUserPrincipal().getName()).get();
            validateUserWorksAtCompany(user.getId(), companyIds, userEmailDao);

            final List<Buzz> buzzList = buzzDao.getByCompanyIds(companyIds, start, limit);
            final List<Integer> buzzIds = buzzList.stream().map(Buzz::getId).collect(toList());
            final List<BuzzLike> buzzLikes = buzzLikeDao.getByUserIdAndBuzzIds(user.getId(), buzzIds);
            final Set<Integer> buzzLikeIds = buzzLikes.stream().map(BuzzLike::getBuzzId).collect(toSet());
            final List<BuzzFavorite> buzzFavorites = buzzFavoriteDao.getByUserIdAndBuzzIds(user.getId(), buzzIds);
            final Set<Integer> buzzFavoriteIds = buzzFavorites.stream().map(BuzzFavorite::getBuzzId).collect(toSet());

            final List<BuzzView> buzzViews = buzzList.stream()
                    .map(buzz -> new BuzzView(buzz, buzzLikeIds.contains(buzz.getId()), buzzFavoriteIds.contains(buzz.getId())))
                    .collect(toList());
            final Map<Integer, List<BuzzView>> buzzListByCompanyId = buzzViews.stream().collect(groupingBy(BuzzView::getCompanyId));

            return buzzListByCompanyId.entrySet().stream()
                    .map(entry -> new BuzzListWithCompanyView(entry.getKey(), entry.getValue()))
                    .collect(toList());
        }
    }

    @GET
    @Path("/buzz/posted")
    @Produces(MediaType.APPLICATION_JSON)
    public List<BuzzView> getPostedBuzz(@QueryParam("start") @DefaultValue("0") final int start,
                                                       @QueryParam("limit") @DefaultValue("50") final int limit,
                                                       @Context final SecurityContext securityContext) {
        try (final SessionProvider sessionProvider = new SessionProvider()) {
            final UserDao userDao = new UserDao(sessionProvider);
            final BuzzDao buzzDao = new BuzzDao(sessionProvider);
            final BuzzLikeDao buzzLikeDao = new BuzzLikeDao(sessionProvider);
            final BuzzFavoriteDao buzzFavoriteDao = new BuzzFavoriteDao(sessionProvider);

            final User user = userDao.getByGuid(securityContext.getUserPrincipal().getName()).get();

            final List<Buzz> buzzList = buzzDao.getByUserId(user.getId(), start, limit);
            final List<Integer> buzzIds = buzzList.stream().map(Buzz::getId).collect(toList());
            final List<BuzzLike> buzzLikes = buzzLikeDao.getByUserIdAndBuzzIds(user.getId(), buzzIds);
            final Set<Integer> buzzLikeIds = buzzLikes.stream().map(BuzzLike::getBuzzId).collect(toSet());
            final List<BuzzFavorite> buzzFavorites = buzzFavoriteDao.getByUserIdAndBuzzIds(user.getId(), buzzIds);
            final Set<Integer> buzzFavoriteIds = buzzFavorites.stream().map(BuzzFavorite::getBuzzId).collect(toSet());

            final List<BuzzView> buzzViews = buzzList.stream()
                    .map(buzz -> new BuzzView(buzz, buzzLikeIds.contains(buzz.getId()), buzzFavoriteIds.contains(buzz.getId())))
                    .collect(toList());

            return buzzViews;
        }
    }

    /**
     * Validate user works at the company
     * @param userId
     * @param companyId
     * @param userEmailDao
     * @throws Exception
     */
    public static void validateUserWorksAtCompany(final int userId, final int companyId, final UserEmailDao userEmailDao) {
        validateUserWorksAtCompany(userId, singletonList(companyId), userEmailDao);
    }

    /**
     * Validate user works at these companies
     * @param userId
     * @param companyIds
     * @param userEmailDao
     * @throws Exception
     */
    public static void validateUserWorksAtCompany(final int userId, final List<Integer> companyIds, final UserEmailDao userEmailDao) {
        final List<UserEmail> userEmails = userEmailDao.getByUserIdAndCompanyIds(userId, companyIds);
        if (userEmails.size() != companyIds.size()) {
            throw new BuzzException(USER_NOT_IN_COMPANY);
        }
    }

    @POST
    @Path("/buzz")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public BuzzView postBuzz(final BuzzRequestBody buzzRequestBody,
                             @Context final SecurityContext securityContext) {
        if(isEmpty(buzzRequestBody.getText())) {
            throw new BuzzException(TEXT_CANNOT_BE_EMPTY);
        }

        try (final SessionProvider sessionProvider = new SessionProvider()) {
            final UserDao userDao = new UserDao(sessionProvider);
            final BuzzDao buzzDao = new BuzzDao(sessionProvider);
            final UserEmailDao userEmailDao = new UserEmailDao(sessionProvider);
            final CompanyDao companyDao = new CompanyDao(sessionProvider);

            final User user = userDao.getByGuid(securityContext.getUserPrincipal().getName()).get();
            final Optional<Company> company = companyDao.getByIdOptional(buzzRequestBody.getCompanyId());

            if (!company.isPresent()) {
                throw new BuzzException(COMPANY_NOT_EXIST);
            }

            final Optional<UserEmail> userEmail = userEmailDao.getByIdOptional(buzzRequestBody.getUserEmailId());
            if (!userEmail.isPresent()) {
                throw new BuzzException(USER_EMAIL_NOT_EXIST);
            }

            if (userEmail.get().getUser().getId() != user.getId()) {
                throw new BuzzException(USER_EMAIL_NOT_MATCH);
            }

            validateUserWorksAtCompany(user.getId(), company.get().getId(), userEmailDao);

            final Buzz buzz = buzzDao.postBuzz(buzzRequestBody, userEmail.get(), company.get());

            return new BuzzView(buzz, false, false);
        }
    }

    @POST
    @Path("/buzz/like")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public BuzzView likeBuzz(final BuzzLikeRequestBody buzzLikeRequestBody,
                             @Context final SecurityContext securityContext) {
        try (final SessionProvider sessionProvider = new SessionProvider()) {
            final UserDao userDao = new UserDao(sessionProvider);
            final BuzzDao buzzDao = new BuzzDao(sessionProvider);
            final BuzzLikeDao buzzLikeDao = new BuzzLikeDao(sessionProvider);
            final BuzzFavoriteDao buzzFavoriteDao = new BuzzFavoriteDao(sessionProvider);

            final User user = userDao.getByGuid(securityContext.getUserPrincipal().getName()).get();
            final Optional<Buzz> buzz = buzzDao.getByIdOptional(buzzLikeRequestBody.getBuzzId());
            if (!buzz.isPresent()) {
                throw new BuzzException(BUZZ_NOT_EXIST);
            }

            final Optional<BuzzFavorite> buzzFavorite = buzzFavoriteDao.getByUserIdAndBuzzId(user.getId(), buzzLikeRequestBody.getBuzzId());

            // already liked/unliked
            final Optional<BuzzLike> buzzLiked = buzzLikeDao.getByUserIdAndBuzzId(user.getId(), buzzLikeRequestBody.getBuzzId());
            if (buzzLikeRequestBody.isLiked() == buzzLiked.isPresent()) {
                return new BuzzView(buzz.get(), buzzLiked.isPresent(), buzzFavorite.isPresent());
            }

            sessionProvider.startTransaction();
            if (buzzLikeRequestBody.isLiked()) {
                buzzLikeDao.likeBuzz(user.getId(), buzzLikeRequestBody.getBuzzId());
                buzzDao.increaseLikesCount(buzz.get().getId());
            } else {
                buzzLikeDao.dislikeBuzz(user.getId(), buzzLikeRequestBody.getBuzzId());
                buzzDao.decreaseLikesCount(buzz.get().getId());
            }
            sessionProvider.commitTransaction();

            return new BuzzView(buzz.get(), buzzLikeRequestBody.isLiked(), buzzFavorite.isPresent());
        }
    }

    @POST
    @Path("/buzz/favorite")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public BuzzView favoriteBuzz(final BuzzFavoriteRequestBody buzzFavoriteRequestBody,
                                 @Context final SecurityContext securityContext) {
        try (final SessionProvider sessionProvider = new SessionProvider()) {
            final UserDao userDao = new UserDao(sessionProvider);
            final BuzzDao buzzDao = new BuzzDao(sessionProvider);
            final BuzzFavoriteDao buzzFavoriteDao = new BuzzFavoriteDao(sessionProvider);
            final BuzzLikeDao buzzLikeDao = new BuzzLikeDao(sessionProvider);

            final User user = userDao.getByGuid(securityContext.getUserPrincipal().getName()).get();
            final Optional<Buzz> buzz = buzzDao.getByIdOptional(buzzFavoriteRequestBody.getBuzzId());
            if (!buzz.isPresent()) {
                throw new BuzzException(BUZZ_NOT_EXIST);
            }

            final Optional<BuzzLike> buzzLike = buzzLikeDao.getByUserIdAndBuzzId(user.getId(), buzzFavoriteRequestBody.getBuzzId());

            // already favorite/unfavorite
            final Optional<BuzzFavorite> buzzFavorited = buzzFavoriteDao.getByUserIdAndBuzzId(user.getId(), buzzFavoriteRequestBody.getBuzzId());
            if (buzzFavoriteRequestBody.isFavorited() == buzzFavorited.isPresent()) {
                return new BuzzView(buzz.get(), buzzLike.isPresent(), buzzFavorited.isPresent());
            }

            sessionProvider.startTransaction();
            if (buzzFavoriteRequestBody.isFavorited()) {
                buzzFavoriteDao.favoriteBuzz(user.getId(), buzzFavoriteRequestBody.getBuzzId());
                buzzDao.increaseLikesCount(buzz.get().getId());
            } else {
                buzzFavoriteDao.unfavoriteBuzz(user.getId(), buzzFavoriteRequestBody.getBuzzId());
                buzzDao.decreaseLikesCount(buzz.get().getId());
            }
            sessionProvider.commitTransaction();

            return new BuzzView(buzz.get(), buzzLike.isPresent(), buzzFavoriteRequestBody.isFavorited());
        }
    }

}