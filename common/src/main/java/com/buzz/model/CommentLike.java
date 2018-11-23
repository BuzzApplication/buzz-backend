package com.buzz.model;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import java.io.Serializable;
import java.time.Instant;

/**
 * Created by toshikijahja on 7/29/17.
 */
@Entity
@IdClass(CommentLike.CommentLikePK.class)
public class CommentLike {

    @Id
    @Column
    private int commentId;

    @Id
    @Column
    private int userId;

    @Column
    @CreationTimestamp
    private Instant created;

    @Column
    @UpdateTimestamp
    private Instant lastModified;

    public CommentLike() {

    }

    public CommentLike(final Builder builder) {
        setCommentId(builder.commentId);
        setUserId(builder.userId);

    }

    public int getCommentId() {
        return this.commentId;
    }

    public void setCommentId(final int commentId) {
        this.commentId = commentId;
    }

    public int getUserId() {
        return this.userId;
    }

    public void setUserId(final int userId) {
        this.userId = userId;
    }

    public Instant getCreated() {
        return this.created;
    }

    public void setCreated(final Instant created) {
        this.created = created;
    }

    public Instant getLastModified() {
        return this.lastModified;
    }

    public void setLastModified(final Instant lastModified) {
        this.lastModified = lastModified;
    }

    public static class CommentLikePK implements Serializable {
        private int commentId;
        private int userId;

        public CommentLikePK() {

        }

        public CommentLikePK(final int commentId, final int userId) {
            this.commentId = commentId;
            this.userId = userId;
        }
    }

    public static class Builder {
        private int commentId;
        private int userId;

        public Builder commentId(final int commentId) {
            this.commentId = commentId;
            return this;
        }

        public Builder userId(final int userId) {
            this.userId = userId;
            return this;
        }

        public CommentLike build() {
            return new CommentLike(this);
        }
    }
}
