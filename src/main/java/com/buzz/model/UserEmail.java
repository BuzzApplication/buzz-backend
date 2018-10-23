package com.buzz.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;
import java.time.Instant;

/**
 * Created by toshikijahja on 10/31/17.
 */
@Entity
@IdClass(UserEmail.UserEmailPK.class)
public class UserEmail {

    @Id
    @Column(nullable = false)
    private String email;

    @Id
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "userId")
    private User user;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "companyId")
    private Company company;

    @Column
    private Instant created;

    @Column
    private Instant lastModified;

    public UserEmail() {

    }

    public UserEmail(final Builder builder) {
        setEmail(builder.email);
        setUser(builder.user);
        setCompany(builder.company);
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(final User user) {
        this.user = user;
    }

    public Company getCompany() {
        return this.company;
    }

    public void setCompany(final Company company) {
        this.company = company;
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

    public static class UserEmailPK implements Serializable {
        private String email;
        private User user;

        public UserEmailPK() {

        }

        public UserEmailPK(final String email, final User user) {
            this.email = email;
            this.user = user;
        }
    }

    public static class Builder {
        private String email;
        private Company company;
        private User user;

        public Builder email(final String email) {
            this.email = email;
            return this;
        }

        public Builder company(final Company company) {
            this.company = company;
            return this;
        }

        public Builder User(final User user) {
            this.user = user;
            return this;
        }

        public UserEmail build() {
            return new UserEmail(this);
        }
    }

}

