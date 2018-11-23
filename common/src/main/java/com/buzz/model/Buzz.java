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
import javax.persistence.OneToMany;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by toshikijahja on 7/29/17.
 */
@Entity
public class Buzz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    @Type(type="text")
    private String text;

    @Column
    private int companyId;

    @Column
    private String alias;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "userEmailId")
    private UserEmail userEmail;

    @Column
    private int likesCount = 0;

    @Column
    private int commentsCount = 0;

    @OneToMany(mappedBy = "buzz")
    private List<Poll> polls = new ArrayList<>();

    @Column
    @CreationTimestamp
    private Instant created;

    @Column
    @UpdateTimestamp
    private Instant lastModified;

    public Buzz() {

    }

    public Buzz(final Builder builder) {
        setText(builder.text);
        setCompanyId(builder.companyId);
        setAlias(builder.alias);
        setUserEmail(builder.userEmail);
        setLikesCount(builder.likesCount);
        setCommentsCount(builder.commentsCount);
        setPolls(builder.polls);
    }

    public int getId() {
        return this.id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public String getText() {
        return this.text;
    }

    public void setText(final String text) {
        this.text = text;
    }

    public int getCompanyId() {
        return this.companyId;
    }

    public void setCompanyId(final int companyId) {
        this.companyId = companyId;
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

    public int getCommentsCount() {
        return this.commentsCount;
    }

    public void setCommentsCount(final int commentsCount) {
        this.commentsCount = commentsCount;
    }

    public List<Poll> getPolls() {
        return this.polls;
    }

    public void setPolls(final List<Poll> polls) {
        this.polls = polls;
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
        private String text;
        private int companyId;
        private String alias;
        private UserEmail userEmail;
        private int likesCount;
        private int commentsCount;
        private List<Poll> polls;

        public Builder text(final String text) {
            this.text = text;
            return this;
        }

        public Builder companyId(final int companyId) {
            this.companyId = companyId;
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

        public Builder commentsCount(final int commentsCount) {
            this.commentsCount = commentsCount;
            return this;
        }

        public Builder polls(final List<Poll> polls) {
            this.polls = polls;
            return this;
        }

        public Buzz build() {
            return new Buzz(this);
        }
    }
}
