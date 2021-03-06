package com.buzz.model;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.Instant;

import static com.buzz.model.User.Status.ACTIVE;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String guid;

    @Column(nullable = false)
    private String alias;

    @Column
    @Enumerated(EnumType.STRING)
    private Status status = ACTIVE;

    @Column
    @CreationTimestamp
    private Instant created;

    @Column
    @UpdateTimestamp
    private Instant lastModified;

    public enum Status {
        ACTIVE,
        SUSPENDED,
    }

    public User() {

    }

    public User(final Builder builder) {
        setAlias(builder.alias);
        setGuid(builder.guid);
        setStatus(builder.status);
    }

    public int getId() {
        return this.id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public String getGuid() {
        return this.guid;
    }

    public void setGuid(final String guid) {
        this.guid = guid;
    }

    public String getAlias() {
        return this.alias;
    }

    public void setAlias(final String alias) {
        this.alias = alias;
    }

    public Status getStatus() {
        return this.status;
    }

    public void setStatus(final Status status) {
        this.status = status;
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
        private String alias;
        private String guid;
        private Status status;

        public Builder alias(final String alias) {
            this.alias = alias;
            return this;
        }

        public Builder guid(final String guid) {
            this.guid = guid;
            return this;
        }

        public Builder status(final Status status) {
            this.status = status;
            return this;
        }

        public User build() {
            return new User(this);
        }
    }
}