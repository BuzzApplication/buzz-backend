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
@IdClass(UserPoll.PollLikePK.class)
public class UserPoll {

    @Id
    @Column
    private int pollId;

    @Id
    @Column
    private int userId;

    @Column
    private int buzzId;

    @Column
    @CreationTimestamp
    private Instant created;

    @Column
    @UpdateTimestamp
    private Instant lastModified;

    public UserPoll() {

    }

    public UserPoll(final Builder builder) {
        setPollId(builder.pollId);
        setUserId(builder.userId);
        setBuzzId(builder.buzzId);
    }

    public int getPollId() {
        return this.pollId;
    }

    public void setPollId(final int pollId) {
        this.pollId = pollId;
    }

    public int getUserId() {
        return this.userId;
    }

    public void setUserId(final int userId) {
        this.userId = userId;
    }

    public int getBuzzId() {
        return this.buzzId;
    }

    public void setBuzzId(final int buzzId) {
        this.buzzId = buzzId;
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

    public static class PollLikePK implements Serializable {
        private int pollId;
        private int userId;

        public PollLikePK() {

        }

        public PollLikePK(final int pollId, final int userId) {
            this.pollId = pollId;
            this.userId = userId;
        }
    }

    public static class Builder {
        private int pollId;
        private int userId;
        private int buzzId;

        public Builder pollId(final int pollId) {
            this.pollId = pollId;
            return this;
        }

        public Builder userId(final int userId) {
            this.userId = userId;
            return this;
        }

        public Builder buzzId(final int buzzId) {
            this.buzzId = buzzId;
            return this;
        }

        public UserPoll build() {
            return new UserPoll(this);
        }
    }
}
