package com.buzz.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.Instant;
import java.util.UUID;

@Entity
public class Authentication {

    @Id
    private String guid = String.valueOf(UUID.randomUUID());

    @Column
    private String email;

    @Column
    private String password;

    @Column
    private Instant created;

    @Column
    private Instant lastModified;

    public Authentication() {

    }

    public Authentication(final Builder builder) {
        setGuid(builder.guid);
        setEmail(builder.email);
        setPassword(builder.password);
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

        public Builder email(final String email) {
            this.email = email;
            return this;
        }

        public Builder password(final String password) {
            this.password = password;
            return this;
        }

        public Authentication build() {
            return new Authentication(this);
        }
    }
}