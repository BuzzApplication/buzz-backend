package com.buzz.source.user;

import com.buzz.auth.UserAuth;
import com.buzz.dao.BuzzDao;
import com.buzz.dao.BuzzFavoriteDao;
import com.buzz.dao.BuzzLikeDao;
import com.buzz.dao.CompanyDao;
import com.buzz.dao.PollDao;
import com.buzz.dao.SessionProvider;
import com.buzz.dao.UserDao;
import com.buzz.dao.UserEmailDao;
import com.buzz.dao.UserPollDao;
import com.buzz.exception.BuzzException;
import com.buzz.model.Buzz;
import com.buzz.model.BuzzFavorite;
import com.buzz.model.BuzzLike;
import com.buzz.model.Company;
import com.buzz.model.Poll;
import com.buzz.model.User;
import com.buzz.model.UserEmail;
import com.buzz.model.UserPoll;
import com.buzz.requestBody.BuzzFavoriteRequestBody;
import com.buzz.requestBody.BuzzLikeRequestBody;
import com.buzz.requestBody.BuzzRequestBody;
import com.buzz.requestBody.PollRequestBody;
import com.buzz.view.BuzzListWithCompanyView;
import com.buzz.view.BuzzView;
import com.mysql.cj.core.util.StringUtils;

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
import static com.buzz.exception.BadRequest.POLL_ALREADY_EXIST;
import static com.buzz.exception.BadRequest.POLL_NOT_EXIST;
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

    private static final int TRENDING_BUZZ_MAX_DAYS = 14;
    private static final int TRENDING_BUZZ_LIMIT = 10;

    @GET
    @Path("/buzz/search")
    @Produces(MediaType.APPLICATION_JSON)
    public List<BuzzView> searchBuzz(@QueryParam("text") final String text,
                                         @QueryParam("start") @DefaultValue("0") final int start,
                                         @QueryParam("limit") @DefaultValue("50") final int limit,
                                         @Context final SecurityContext securityContext) {
        if (StringUtils.isEmptyOrWhitespaceOnly(text)) {
            return emptyList();
        }
        try (final SessionProvider sessionProvider = new SessionProvider()) {
            final UserDao userDao = new UserDao(sessionProvider);
            final BuzzDao buzzDao = new BuzzDao(sessionProvider);
            final BuzzLikeDao buzzLikeDao = new BuzzLikeDao(sessionProvider);
            final BuzzFavoriteDao buzzFavoriteDao = new BuzzFavoriteDao(sessionProvider);
            final UserPollDao userPollDao = new UserPollDao(sessionProvider);
            final UserEmailDao userEmailDao = new UserEmailDao(sessionProvider);

            final User user = userDao.getByGuid(securityContext.getUserPrincipal().getName()).get();
            final List<UserEmail> userEmails = userEmailDao.getByUserId(user.getId());
            final List<Integer> companyIds = userEmails.stream().map(ue -> ue.getCompany().getId()).collect(toList());

            final List<Buzz> buzzList = buzzDao.search(text, companyIds, start, limit);
            if (buzzList.isEmpty()) {
                return emptyList();
            }
            final List<Integer> buzzIds = buzzList.stream().map(Buzz::getId).collect(toList());
            final List<BuzzLike> buzzLikes = buzzLikeDao.getByUserIdAndBuzzIds(user.getId(), buzzIds);
            final Set<Integer> buzzLikeIds = buzzLikes.stream().map(BuzzLike::getBuzzId).collect(toSet());
            final List<BuzzFavorite> buzzFavorites = buzzFavoriteDao.getByUserIdAndBuzzIds(user.getId(), buzzIds);
            final Set<Integer> buzzFavoriteIds = buzzFavorites.stream().map(BuzzFavorite::getBuzzId).collect(toSet());
            final List<UserPoll> userPolls = userPollDao.getByUserIdAndBuzzIds(user.getId(), buzzIds);
            final Map<Integer, List<UserPoll>> buzzIdToUserPolls = userPolls.stream()
                    .collect(groupingBy(UserPoll::getBuzzId));

            final List<BuzzView> buzzViews = buzzList.stream()
                    .map(buzz -> new BuzzView(
                            buzz,
                            buzzLikeIds.contains(buzz.getId()),
                            buzzFavoriteIds.contains(buzz.getId()),
                            buzzIdToUserPolls.containsKey(buzz.getId()) ?
                                    buzzIdToUserPolls.get(buzz.getId()).stream().map(UserPoll::getPollId).collect(toList()) :
                                    emptyList()))
                        .collect(toList());

            return buzzViews;
        }
    }

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
            final UserPollDao userPollDao = new UserPollDao(sessionProvider);
            final UserEmailDao userEmailDao = new UserEmailDao(sessionProvider);

            final User user = userDao.getByGuid(securityContext.getUserPrincipal().getName()).get();
            validateUserWorksAtCompany(user.getId(), companyIds, userEmailDao);

            final List<Buzz> buzzList = buzzDao.getByCompanyIds(companyIds, start, limit);
            if (buzzList.isEmpty()) {
                return emptyList();
            }
            final List<Integer> buzzIds = buzzList.stream().map(Buzz::getId).collect(toList());
            final List<BuzzLike> buzzLikes = buzzLikeDao.getByUserIdAndBuzzIds(user.getId(), buzzIds);
            final Set<Integer> buzzLikeIds = buzzLikes.stream().map(BuzzLike::getBuzzId).collect(toSet());
            final List<BuzzFavorite> buzzFavorites = buzzFavoriteDao.getByUserIdAndBuzzIds(user.getId(), buzzIds);
            final Set<Integer> buzzFavoriteIds = buzzFavorites.stream().map(BuzzFavorite::getBuzzId).collect(toSet());
            final List<UserPoll> userPolls = userPollDao.getByUserIdAndBuzzIds(user.getId(), buzzIds);
            final Map<Integer, List<UserPoll>> buzzIdToUserPolls = userPolls.stream()
                    .collect(groupingBy(UserPoll::getBuzzId));

            final List<BuzzView> buzzViews = buzzList.stream()
                    .map(buzz -> new BuzzView(
                            buzz,
                            buzzLikeIds.contains(buzz.getId()),
                            buzzFavoriteIds.contains(buzz.getId()),
                            buzzIdToUserPolls.containsKey(buzz.getId()) ?
                                    buzzIdToUserPolls.get(buzz.getId()).stream().map(UserPoll::getPollId).collect(toList()) :
                                    emptyList()))
                    .collect(toList());
            final Map<Integer, List<BuzzView>> buzzListByCompanyId = buzzViews.stream().collect(groupingBy(BuzzView::getCompanyId));

            return buzzListByCompanyId.entrySet().stream()
                    .map(entry -> new BuzzListWithCompanyView(entry.getKey(), entry.getValue()))
                    .collect(toList());
        }
    }

    @GET
    @Path("/buzz/trending")
    @Produces(MediaType.APPLICATION_JSON)
    public List<BuzzView> getTrendingBuzz(@Context final SecurityContext securityContext) {
        try (final SessionProvider sessionProvider = new SessionProvider()) {
            final UserDao userDao = new UserDao(sessionProvider);
            final BuzzDao buzzDao = new BuzzDao(sessionProvider);
            final BuzzLikeDao buzzLikeDao = new BuzzLikeDao(sessionProvider);
            final BuzzFavoriteDao buzzFavoriteDao = new BuzzFavoriteDao(sessionProvider);
            final UserPollDao userPollDao = new UserPollDao(sessionProvider);

            final User user = userDao.getByGuid(securityContext.getUserPrincipal().getName()).get();

            final List<Buzz> buzzList = buzzDao.getTrending(TRENDING_BUZZ_MAX_DAYS, TRENDING_BUZZ_LIMIT);
            if (buzzList.isEmpty()) {
                return emptyList();
            }
            final List<Integer> buzzIds = buzzList.stream().map(Buzz::getId).collect(toList());
            final List<BuzzLike> buzzLikes = buzzLikeDao.getByUserIdAndBuzzIds(user.getId(), buzzIds);
            final Set<Integer> buzzLikeIds = buzzLikes.stream().map(BuzzLike::getBuzzId).collect(toSet());
            final List<BuzzFavorite> buzzFavorites = buzzFavoriteDao.getByUserIdAndBuzzIds(user.getId(), buzzIds);
            final Set<Integer> buzzFavoriteIds = buzzFavorites.stream().map(BuzzFavorite::getBuzzId).collect(toSet());
            final List<UserPoll> userPolls = userPollDao.getByUserIdAndBuzzIds(user.getId(), buzzIds);
            final Map<Integer, List<UserPoll>> buzzIdToUserPolls = userPolls.stream()
                    .collect(groupingBy(UserPoll::getBuzzId));

            final List<BuzzView> buzzViews = buzzList.stream()
                    .map(buzz -> new BuzzView(
                            buzz,
                            buzzLikeIds.contains(buzz.getId()),
                            buzzFavoriteIds.contains(buzz.getId()),
                            buzzIdToUserPolls.containsKey(buzz.getId()) ?
                                    buzzIdToUserPolls.get(buzz.getId()).stream().map(UserPoll::getPollId).collect(toList()) :
                                    emptyList()))
                    .collect(toList());

            return buzzViews;
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
            final UserPollDao userPollDao = new UserPollDao(sessionProvider);

            final User user = userDao.getByGuid(securityContext.getUserPrincipal().getName()).get();

            final List<Buzz> buzzList = buzzDao.getByUserId(user.getId(), start, limit);
            if (buzzList.isEmpty()) {
                return emptyList();
            }
            final List<Integer> buzzIds = buzzList.stream().map(Buzz::getId).collect(toList());
            final List<BuzzLike> buzzLikes = buzzLikeDao.getByUserIdAndBuzzIds(user.getId(), buzzIds);
            final Set<Integer> buzzLikeIds = buzzLikes.stream().map(BuzzLike::getBuzzId).collect(toSet());
            final List<BuzzFavorite> buzzFavorites = buzzFavoriteDao.getByUserIdAndBuzzIds(user.getId(), buzzIds);
            final Set<Integer> buzzFavoriteIds = buzzFavorites.stream().map(BuzzFavorite::getBuzzId).collect(toSet());
            final List<UserPoll> userPolls = userPollDao.getByUserIdAndBuzzIds(user.getId(), buzzIds);
            final Map<Integer, List<UserPoll>> buzzIdToUserPolls = userPolls.stream()
                    .collect(groupingBy(UserPoll::getBuzzId));

            final List<BuzzView> buzzViews = buzzList.stream()
                    .map(buzz -> new BuzzView(
                            buzz,
                            buzzLikeIds.contains(buzz.getId()),
                            buzzFavoriteIds.contains(buzz.getId()),
                            buzzIdToUserPolls.containsKey(buzz.getId()) ?
                                    buzzIdToUserPolls.get(buzz.getId()).stream().map(UserPoll::getPollId).collect(toList()) :
                                    emptyList()))
                    .collect(toList());

            return buzzViews;
        }
    }

    @GET
    @Path("/buzz/favorite")
    @Produces(MediaType.APPLICATION_JSON)
    public List<BuzzView> getFavoriteBuzz(@QueryParam("start") @DefaultValue("0") final int start,
                                          @QueryParam("limit") @DefaultValue("50") final int limit,
                                          @Context final SecurityContext securityContext) {
        try (final SessionProvider sessionProvider = new SessionProvider()) {
            final UserDao userDao = new UserDao(sessionProvider);
            final BuzzDao buzzDao = new BuzzDao(sessionProvider);
            final BuzzLikeDao buzzLikeDao = new BuzzLikeDao(sessionProvider);
            final BuzzFavoriteDao buzzFavoriteDao = new BuzzFavoriteDao(sessionProvider);
            final UserPollDao userPollDao = new UserPollDao(sessionProvider);

            final User user = userDao.getByGuid(securityContext.getUserPrincipal().getName()).get();

            final List<BuzzFavorite> buzzFavorites = buzzFavoriteDao.getByUserId(user.getId());
            if (buzzFavorites.isEmpty()) {
                return emptyList();
            }
            final List<Integer> buzzIds = buzzFavorites.stream().map(BuzzFavorite::getBuzzId).collect(toList());

            final List<Buzz> buzzList = buzzDao.getByIds(buzzIds, start, limit);
            final List<BuzzLike> buzzLikes = buzzLikeDao.getByUserIdAndBuzzIds(user.getId(), buzzIds);
            final Set<Integer> buzzLikeIds = buzzLikes.stream().map(BuzzLike::getBuzzId).collect(toSet());
            final List<UserPoll> userPolls = userPollDao.getByUserIdAndBuzzIds(user.getId(), buzzIds);
            final Map<Integer, List<UserPoll>> buzzIdToUserPolls = userPolls.stream()
                    .collect(groupingBy(UserPoll::getBuzzId));

            final List<BuzzView> buzzViews = buzzList.stream()
                    .map(buzz -> new BuzzView(
                            buzz,
                            buzzLikeIds.contains(buzz.getId()),
                            true,
                            buzzIdToUserPolls.containsKey(buzz.getId()) ?
                                    buzzIdToUserPolls.get(buzz.getId()).stream().map(UserPoll::getPollId).collect(toList()) :
                                    emptyList()))
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

            return new BuzzView(buzz, false, false, emptyList());
        }
    }

    @POST
    @Path("/buzz/poll")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public BuzzView poll(final PollRequestBody pollRequestBody,
                         @Context final SecurityContext securityContext) {
        try (final SessionProvider sessionProvider = new SessionProvider()) {
            final UserDao userDao = new UserDao(sessionProvider);
            final BuzzLikeDao buzzLikeDao = new BuzzLikeDao(sessionProvider);
            final BuzzFavoriteDao buzzFavoriteDao = new BuzzFavoriteDao(sessionProvider);
            final PollDao pollDao = new PollDao(sessionProvider);
            final UserPollDao userPollDao = new UserPollDao(sessionProvider);

            final User user = userDao.getByGuid(securityContext.getUserPrincipal().getName()).get();

            final Optional<UserPoll> userPolls = userPollDao.getByUserIdAndPollId(user.getId(), pollRequestBody.getPollId());
            if (userPolls.isPresent()) {
                throw new BuzzException(POLL_ALREADY_EXIST);
            }

            final Optional<Poll> poll = pollDao.getByIdOptional(pollRequestBody.getPollId());
            if (!poll.isPresent()) {
                throw new BuzzException(POLL_NOT_EXIST);
            }

            sessionProvider.startTransaction();
            userPollDao.poll(pollRequestBody.getPollId(), poll.get().getBuzz().getId(), user.getId());
            pollDao.increasePollCount(pollRequestBody.getPollId());
            sessionProvider.commitTransaction();
            sessionProvider.getSession().refresh(poll.get());

            final Buzz buzz = poll.get().getBuzz();
            final Optional<BuzzFavorite> buzzFavorite = buzzFavoriteDao.getByUserIdAndBuzzId(user.getId(), buzz.getId());
            final Optional<BuzzLike> buzzLike = buzzLikeDao.getByUserIdAndBuzzId(user.getId(), buzz.getId());

            return new BuzzView(buzz, buzzLike.isPresent(), buzzFavorite.isPresent(), singletonList(poll.get().getId()));
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
            final UserPollDao userPollDao = new UserPollDao(sessionProvider);

            final User user = userDao.getByGuid(securityContext.getUserPrincipal().getName()).get();
            final Optional<Buzz> buzz = buzzDao.getByIdOptional(buzzLikeRequestBody.getBuzzId());
            if (!buzz.isPresent()) {
                throw new BuzzException(BUZZ_NOT_EXIST);
            }

            final Optional<BuzzFavorite> buzzFavorite = buzzFavoriteDao.getByUserIdAndBuzzId(user.getId(), buzzLikeRequestBody.getBuzzId());
            final List<UserPoll> userPolls = userPollDao.getByUserIdAndBuzzId(user.getId(), buzz.get().getId());
            final Map<Integer, List<UserPoll>> buzzIdToUserPolls = userPolls.stream()
                    .collect(groupingBy(UserPoll::getBuzzId));

            // already liked/unliked
            final Optional<BuzzLike> buzzLiked = buzzLikeDao.getByUserIdAndBuzzId(user.getId(), buzzLikeRequestBody.getBuzzId());
            if (buzzLikeRequestBody.isLiked() == buzzLiked.isPresent()) {
                return new BuzzView(
                        buzz.get(),
                        buzzLiked.isPresent(),
                        buzzFavorite.isPresent(),
                        buzzIdToUserPolls.containsKey(buzz.get().getId()) ?
                                buzzIdToUserPolls.get(buzz.get().getId()).stream().map(UserPoll::getPollId).collect(toList()) :
                                emptyList());
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
            sessionProvider.getSession().refresh(buzz.get());

            return new BuzzView(
                    buzz.get(),
                    buzzLikeRequestBody.isLiked(),
                    buzzFavorite.isPresent(),
                    buzzIdToUserPolls.containsKey(buzz.get().getId()) ?
                            buzzIdToUserPolls.get(buzz.get().getId()).stream().map(UserPoll::getPollId).collect(toList()) :
                            emptyList());
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
            final UserPollDao userPollDao = new UserPollDao(sessionProvider);

            final User user = userDao.getByGuid(securityContext.getUserPrincipal().getName()).get();
            final Optional<Buzz> buzz = buzzDao.getByIdOptional(buzzFavoriteRequestBody.getBuzzId());
            if (!buzz.isPresent()) {
                throw new BuzzException(BUZZ_NOT_EXIST);
            }

            final Optional<BuzzLike> buzzLike = buzzLikeDao.getByUserIdAndBuzzId(user.getId(), buzzFavoriteRequestBody.getBuzzId());
            final List<UserPoll> userPolls = userPollDao.getByUserIdAndBuzzId(user.getId(), buzz.get().getId());
            final Map<Integer, List<UserPoll>> buzzIdToUserPolls = userPolls.stream()
                    .collect(groupingBy(UserPoll::getBuzzId));

            // already favorite/unfavorite
            final Optional<BuzzFavorite> buzzFavorited = buzzFavoriteDao.getByUserIdAndBuzzId(user.getId(), buzzFavoriteRequestBody.getBuzzId());
            if (buzzFavoriteRequestBody.isFavorited() == buzzFavorited.isPresent()) {
                return new BuzzView(
                        buzz.get(),
                        buzzLike.isPresent(),
                        buzzFavorited.isPresent(),
                        buzzIdToUserPolls.containsKey(buzz.get().getId()) ?
                                buzzIdToUserPolls.get(buzz.get().getId()).stream().map(UserPoll::getPollId).collect(toList()) :
                                emptyList());
            }

            sessionProvider.startTransaction();
            if (buzzFavoriteRequestBody.isFavorited()) {
                buzzFavoriteDao.favoriteBuzz(user.getId(), buzzFavoriteRequestBody.getBuzzId());
            } else {
                buzzFavoriteDao.unfavoriteBuzz(user.getId(), buzzFavoriteRequestBody.getBuzzId());
            }

            sessionProvider.commitTransaction();
            sessionProvider.getSession().refresh(buzz.get());

            return new BuzzView(
                    buzz.get(),
                    buzzLike.isPresent(),
                    buzzFavoriteRequestBody.isFavorited(),
                    buzzIdToUserPolls.containsKey(buzz.get().getId()) ?
                            buzzIdToUserPolls.get(buzz.get().getId()).stream().map(UserPoll::getPollId).collect(toList()) :
                            emptyList());
        }
    }

}