package com.buzz.model;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.time.Instant;

/**
 * Created by toshikijahja on 10/23/18.
 */
@Entity
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    private int buzzId;

    @Column(nullable = false)
    @Type(type="text")
    private String text;

    @Column(nullable = false)
    private String alias;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "userEmailId")
    private UserEmail userEmail;

    @Column
    private int likesCount = 0;

    @Column
    @CreationTimestamp
    private Instant created;

    @Column
    @UpdateTimestamp
    private Instant lastModified;

    public Comment() {

    }

    public Comment(final Builder builder) {
        setBuzzId(builder.buzzId);
        setText(builder.text);
        setAlias(builder.alias);
        setUserEmail(builder.userEmail);
        setLikesCount(builder.likesCount);
    }

    public int getId() {
        return this.id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public int getBuzzId() {
        return this.buzzId;
    }

    public void setBuzzId(final int buzzId) {
        this.buzzId = buzzId;
    }

    public String getText() {
        return this.text;
    }

    public void setText(final String text) {
        this.text = text;
    }

    public String getAlias() {
        return this.alias;
    }

    public void setAlias(final String alias) {
        this.alias = alias;
    }

    public UserEmail getUserEmail() {
        return this.userEmail;
    }

    public void setUserEmail(final UserEmail userEmail) {
        this.userEmail = userEmail;
    }

    public int getLikesCount() {
        return this.likesCount;
    }

    public void setLikesCount(final int likesCount) {
        this.likesCount = likesCount;
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

    public static class Builder {
        private int buzzId;
        private String text;
        private String alias;
        private UserEmail userEmail;
        private int likesCount;

        public Builder buzzId(final int buzzId) {
            this.buzzId = buzzId;
            return this;
        }

        public Builder text(final String text) {
            this.text = text;
            return this;
        }

        public Builder alias(final String alias) {
            this.alias = alias;
            return this;
        }

        public Builder userEmail(final UserEmail userEmail) {
            this.userEmail = userEmail;
            return this;
        }

        public Builder likesCount(final int likesCount) {
            this.likesCount = likesCount;
            return this;
        }

        public Comment build() {
            return new Comment(this);
        }
    }
}
