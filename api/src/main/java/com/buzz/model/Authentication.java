package com.buzz.model;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import java.time.Instant;
import java.util.UUID;

import static com.buzz.model.Authentication.Status.UNVERIFIED;

@Entity
public class Authentication {

    @Id
    private String guid = String.valueOf(UUID.randomUUID());

    @Column
    private String email;

    @Column
    private String password;

    @Column
    private String verificationCode;

    @Column
    @Enumerated(EnumType.STRING)
    private Status status = UNVERIFIED;

    @Column
    @CreationTimestamp
    private Instant created;

    @Column
    @UpdateTimestamp
    private Instant lastModified;

    public enum Status {
        VERIFIED,
        UNVERIFIED,
    }

    public Authentication() {

    }

    public Authentication(final Builder builder) {
        setGuid(builder.guid);
        setEmail(builder.email);
        setPassword(builder.password);
        setVerificationCode(builder.verificationCode);
        setStatus(builder.status);
    }

    public String getGuid() {
        return this.guid;
    }

    public void setGuid(final String guid) {
        this.guid = guid;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public String getVerificationCode() {
        return this.verificationCode;
    }

    public void setVerificationCode(final String verificationCode) {
        this.verificationCode = verificationCode;
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
        private String guid = String.valueOf(UUID.randomUUID());
        private String email;
        private String password;
        private String verificationCode;
        private Status status;

        public Builder email(final String email) {
            this.email = email;
            return this;
        }

        public Builder password(final String password) {
            this.password = password;
            return this;
        }

        public Builder verificationCode(final String verificationCode) {
            this.verificationCode = verificationCode;
            return this;
        }

        public Builder status(final Status status) {
            this.status = status;
            return this;
        }

        public Authentication build() {
            return new Authentication(this);
        }
    }
}