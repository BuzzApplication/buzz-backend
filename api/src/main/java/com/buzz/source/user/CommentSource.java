package com.buzz.source.user;

import com.buzz.auth.UserAuth;
import com.buzz.dao.persistent.BuzzDao;
import com.buzz.dao.persistent.BuzzFavoriteDao;
import com.buzz.dao.persistent.BuzzLikeDao;
import com.buzz.dao.persistent.CommentDao;
import com.buzz.dao.persistent.CommentLikeDao;
import com.buzz.dao.persistent.SessionProvider;
import com.buzz.dao.persistent.UserDao;
import com.buzz.dao.persistent.UserEmailDao;
import com.buzz.dao.persistent.UserPollDao;
import com.buzz.dao.redis.NotificationRedisHelper;
import com.buzz.exception.BuzzException;
import com.buzz.model.Buzz;
import com.buzz.model.BuzzFavorite;
import com.buzz.model.BuzzLike;
import com.buzz.model.Comment;
import com.buzz.model.CommentLike;
import com.buzz.model.User;
import com.buzz.model.UserEmail;
import com.buzz.model.UserPoll;
import com.buzz.requestBody.CommentLikeRequestBody;
import com.buzz.requestBody.CommentRequestBody;
import com.buzz.view.BuzzCommentListView;
import com.buzz.view.BuzzView;
import com.buzz.view.CommentView;
import com.google.protobuf.InvalidProtocolBufferException;

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
import static com.buzz.exception.BadRequest.COMMENT_NOT_EXIST;
import static com.buzz.exception.BadRequest.TEXT_CANNOT_BE_EMPTY;
import static com.buzz.exception.BadRequest.USER_EMAIL_NOT_EXIST;
import static com.buzz.exception.BadRequest.USER_EMAIL_NOT_MATCH;
import static com.buzz.source.user.BuzzSource.validateUserWorksAtCompany;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.apache.commons.lang.StringUtils.isEmpty;

@Path("/user")
@UserAuth
public class CommentSource {

    private final NotificationRedisHelper notificationRedisHelper;

    public CommentSource() {
        notificationRedisHelper = new NotificationRedisHelper();
    }

    @GET
    @Path("/comment")
    @Produces(MediaType.APPLICATION_JSON)
    public BuzzCommentListView getCommentByBuzzId(@QueryParam("buzzId") final int buzzId,
                                                  @QueryParam("start") @DefaultValue("0") final int start,
                                                  @QueryParam("limit") @DefaultValue("50") final int limit,
                                                  @Context final SecurityContext securityContext) {
        try (final SessionProvider sessionProvider = new SessionProvider()) {
            final UserDao userDao = new UserDao(sessionProvider);
            final BuzzDao buzzDao = new BuzzDao(sessionProvider);
            final BuzzLikeDao buzzLikeDao = new BuzzLikeDao(sessionProvider);
            final BuzzFavoriteDao buzzFavoriteDao = new BuzzFavoriteDao(sessionProvider);
            final CommentDao commentDao = new CommentDao(sessionProvider);
            final CommentLikeDao commentLikeDao = new CommentLikeDao(sessionProvider);
            final UserPollDao userPollDao = new UserPollDao(sessionProvider);
            final UserEmailDao userEmailDao = new UserEmailDao(sessionProvider);

            final User user = userDao.getByGuid(securityContext.getUserPrincipal().getName()).get();
            final Optional<Buzz> buzz = buzzDao.getByIdOptional(buzzId);
            if (!buzz.isPresent()) {
                throw new BuzzException(BUZZ_NOT_EXIST);
            }
            validateUserWorksAtCompany(user.getId(), buzz.get().getCompanyId(), userEmailDao);

            final List<Comment> commentList = commentDao.getByBuzzId(buzz.get().getId(), start, limit);
            final List<Integer> commentIds = commentList.stream().map(Comment::getId).collect(toList());
            final List<CommentLike> commentLikes = commentLikeDao.getByUserIdAndCommentIds(user.getId(), commentIds);
            final Set<Integer> commentLikeIds = commentLikes.stream().map(CommentLike::getCommentId).collect(toSet());

            final List<CommentView> commentViews = commentList.stream()
                    .map(comment -> new CommentView(comment, commentLikeIds.contains(comment.getId())))
                    .collect(toList());

            final Optional<BuzzLike> buzzLike = buzzLikeDao.getByUserIdAndBuzzId(user.getId(), buzz.get().getId());
            final Optional<BuzzFavorite> buzzFavorite = buzzFavoriteDao.getByUserIdAndBuzzId(user.getId(), buzz.get().getId());
            final List<UserPoll> userPolls = userPollDao.getByUserIdAndBuzzId(user.getId(), buzz.get().getId());
            final Map<Integer, List<UserPoll>> buzzIdToUserPolls = userPolls.stream()
                    .collect(groupingBy(UserPoll::getBuzzId));

            final BuzzView buzzView = new BuzzView(
                    buzz.get(),
                    buzzLike.isPresent(),
                    buzzFavorite.isPresent(),
                    buzzIdToUserPolls.containsKey(buzz.get().getId()) ?
                            buzzIdToUserPolls.get(buzz.get().getId()).stream().map(UserPoll::getPollId).collect(toList()) :
                            emptyList());
            return new BuzzCommentListView(buzzView, commentViews);
        }
    }

    @POST
    @Path("/comment")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public CommentView postComment(final CommentRequestBody commentRequestBody,
                                   @Context final SecurityContext securityContext) throws InvalidProtocolBufferException {
        if(isEmpty(commentRequestBody.getText())) {
            throw new BuzzException(TEXT_CANNOT_BE_EMPTY);
        }

        try (final SessionProvider sessionProvider = new SessionProvider()) {
            final UserDao userDao = new UserDao(sessionProvider);
            final BuzzDao buzzDao = new BuzzDao(sessionProvider);
            final UserEmailDao userEmailDao = new UserEmailDao(sessionProvider);
            final CommentDao commentDao = new CommentDao(sessionProvider);

            final User user = userDao.getByGuid(securityContext.getUserPrincipal().getName()).get();
            final Optional<Buzz> buzz = buzzDao.getByIdOptional(commentRequestBody.getBuzzId());

            if (!buzz.isPresent()) {
                throw new BuzzException(BUZZ_NOT_EXIST);
            }

            final Optional<UserEmail> userEmail = userEmailDao.getByIdOptional(commentRequestBody.getUserEmailId());
            if (!userEmail.isPresent()) {
                throw new BuzzException(USER_EMAIL_NOT_EXIST);
            }

            if (userEmail.get().getUser().getId() != user.getId()) {
                throw new BuzzException(USER_EMAIL_NOT_MATCH);
            }

            validateUserWorksAtCompany(user.getId(), buzz.get().getCompanyId(), userEmailDao);

            sessionProvider.startTransaction();
            final Comment comment = commentDao.postComment(commentRequestBody, userEmail.get(), buzzDao);
            notificationRedisHelper.addNotificationComment(buzz.get(), userEmail.get());
            sessionProvider.commitTransaction();

            return new CommentView(comment, false);
        }
    }

    @POST
    @Path("/comment/like")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public CommentView likeComment(final CommentLikeRequestBody commentLikeRequestBody,
                                       @Context final SecurityContext securityContext) throws Exception {
        try (final SessionProvider sessionProvider = new SessionProvider()) {
            final UserDao userDao = new UserDao(sessionProvider);
            final UserEmailDao userEmailDao = new UserEmailDao(sessionProvider);
            final CommentDao commentDao = new CommentDao(sessionProvider);
            final CommentLikeDao commentLikeDao = new CommentLikeDao(sessionProvider);

            final User user = userDao.getByGuid(securityContext.getUserPrincipal().getName()).get();
            final Optional<Comment> comment = commentDao.getByIdOptional(commentLikeRequestBody.getCommentId());
            if (!comment.isPresent()) {
                throw new BuzzException(COMMENT_NOT_EXIST);
            }

            final Optional<UserEmail> userEmail = userEmailDao.getByUserIdNotEveryone(user.getId());
            if (!userEmail.isPresent()) {
                throw new BuzzException(USER_EMAIL_NOT_EXIST);
            }

            // already liked/unliked
            final Optional<CommentLike> commentLiked = commentLikeDao.getByUserIdAndCommentId(user.getId(), commentLikeRequestBody.getCommentId());
            if (commentLikeRequestBody.isLiked() == commentLiked.isPresent()) {
                return new CommentView(comment.get(), commentLiked.isPresent());
            }

            sessionProvider.startTransaction();
            if (commentLikeRequestBody.isLiked()) {
                commentLikeDao.likeComment(user.getId(), commentLikeRequestBody.getCommentId());
                commentDao.increaseLikesCount(comment.get().getId());
            } else {
                commentLikeDao.dislikeComment(user.getId(), commentLikeRequestBody.getCommentId());
                commentDao.decreaseLikesCount(comment.get().getId());
            }

            notificationRedisHelper.addNotificationLikeComment(comment.get(), userEmail.get());
            sessionProvider.commitTransaction();
            sessionProvider.getSession().refresh(comment.get());

            return new CommentView(comment.get(), commentLikeRequestBody.isLiked());
        }
    }

}