package com.buzz.source.user;

import com.buzz.auth.UserAuth;
import com.buzz.dao.BuzzDao;
import com.buzz.dao.BuzzLikeDao;
import com.buzz.dao.CommentDao;
import com.buzz.dao.CommentLikeDao;
import com.buzz.dao.SessionProvider;
import com.buzz.dao.UserDao;
import com.buzz.dao.UserEmailDao;
import com.buzz.model.Buzz;
import com.buzz.model.BuzzLike;
import com.buzz.model.Comment;
import com.buzz.model.CommentLike;
import com.buzz.model.User;
import com.buzz.model.UserEmail;
import com.buzz.requestBody.CommentRequestBody;
import com.buzz.view.BuzzCommentListView;
import com.buzz.view.BuzzView;
import com.buzz.view.CommentView;

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

import static com.buzz.source.user.BuzzSource.validateUserWorksAtCompany;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

@Path("/user")
@UserAuth
public class CommentSource {

    @GET
    @Path("/comment")
    @Produces(MediaType.APPLICATION_JSON)
    public BuzzCommentListView getCommentByBuzzId(@QueryParam("buzzId") final int buzzId,
                                                  @Context final SecurityContext securityContext) throws Exception {
        try (final SessionProvider sessionProvider = new SessionProvider()) {
            final UserDao userDao = new UserDao(sessionProvider);
            final BuzzDao buzzDao = new BuzzDao(sessionProvider);
            final BuzzLikeDao buzzLikeDao = new BuzzLikeDao(sessionProvider);
            final CommentDao commentDao = new CommentDao(sessionProvider);
            final CommentLikeDao commentLikeDao = new CommentLikeDao(sessionProvider);
            final UserEmailDao userEmailDao = new UserEmailDao(sessionProvider);

            final User user = userDao.getByGuid(securityContext.getUserPrincipal().getName()).get();
            final Optional<Buzz> buzz = buzzDao.getByIdOptional(buzzId);
            if (!buzz.isPresent()) {
                throw new Exception();
            }
            validateUserWorksAtCompany(user.getId(), buzz.get().getCompany().getId(), userEmailDao);

            final List<Comment> commentList = commentDao.getByBuzzId(buzz.get().getId());
            final List<Integer> commentIds = commentList.stream().map(Comment::getId).collect(toList());
            final List<CommentLike> commentLikes = commentLikeDao.getByUserIdAndCommentIds(user.getId(), commentIds);
            final Set<Integer> commentLikeIds = commentLikes.stream().map(CommentLike::getCommentId).collect(toSet());

            final List<CommentView> commentViews = commentList.stream()
                    .map(comment -> new CommentView(comment, commentLikeIds.contains(comment.getId())))
                    .collect(toList());

            final Optional<BuzzLike> buzzLike = buzzLikeDao.getByUserIdAndBuzzId(user.getId(), buzz.get().getId());

            final BuzzView buzzView = new BuzzView(buzz.get(), buzzLike.isPresent());
            return new BuzzCommentListView(buzzView, commentViews);
        }
    }

    @POST
    @Path("/comment")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public CommentView postComment(final CommentRequestBody commentRequestBody,
                                   @Context final SecurityContext securityContext) throws Exception {
        try (final SessionProvider sessionProvider = new SessionProvider()) {
            final UserDao userDao = new UserDao(sessionProvider);
            final BuzzDao buzzDao = new BuzzDao(sessionProvider);
            final UserEmailDao userEmailDao = new UserEmailDao(sessionProvider);
            final CommentDao commentDao = new CommentDao(sessionProvider);

            final User user = userDao.getByGuid(securityContext.getUserPrincipal().getName()).get();
            final Optional<Buzz> buzz = buzzDao.getByIdOptional(commentRequestBody.getBuzzId());

            if (!buzz.isPresent()) {
                throw new Exception();
            }

            final Optional<UserEmail> userEmail = userEmailDao.getByIdOptional(commentRequestBody.getUserEmailId());
            if (!userEmail.isPresent()) {
                throw new Exception();
            }

            if (userEmail.get().getUser().getId() != user.getId()) {
                throw new Exception();
            }

            requireNonNull(commentRequestBody.getText());
            validateUserWorksAtCompany(user.getId(), buzz.get().getCompany().getId(), userEmailDao);

            final Comment comment = commentDao.postComment(commentRequestBody, userEmail.get(), buzz.get().getId(), buzzDao);

            return new CommentView(comment);
        }
    }

}