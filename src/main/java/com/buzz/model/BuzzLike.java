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
@IdClass(BuzzLike.BuzzLikePK.class)
public class BuzzLike {

    @Id
    @Column
    private int buzzId;

    @Id
    @Column
    private int userId;

    @Column
    @CreationTimestamp
    private Instant created;

    @Column
    @UpdateTimestamp
    private Instant lastModified;

    public BuzzLike() {

    }

    public BuzzLike(final Builder builder) {
        setBuzzId(builder.buzzId);
        setUserId(builder.userId);

    }

    public int getBuzzId() {
        return this.buzzId;
    }

    public void setBuzzId(final int buzzId) {
        this.buzzId = buzzId;
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

    public static class BuzzLikePK implements Serializable {
        private int buzzId;
        private int userId;

        public BuzzLikePK() {

        }

        public BuzzLikePK(final int buzzId, final int userId) {
            this.buzzId = buzzId;
            this.userId = userId;
        }
    }

    public static class Builder {
        private int buzzId;
        private int userId;

        public Builder buzzId(final int buzzId) {
            this.buzzId = buzzId;
            return this;
        }

        public Builder userId(final int userId) {
            this.userId = userId;
            return this;
        }

        public BuzzLike build() {
            return new BuzzLike(this);
        }
    }
}
