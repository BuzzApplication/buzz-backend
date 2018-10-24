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
 * Created by toshikijahja on 10/31/17.
 */
@Entity
@IdClass(CompanyEmail.CompanyEmailPK.class)
public class CompanyEmail {

    @Id
    @Column(nullable = false)
    private String email;

    @Id
    @Column
    private int companyId;

    @Column
    @CreationTimestamp
    private Instant created;

    @Column
    @UpdateTimestamp
    private Instant lastModified;

    public CompanyEmail() {

    }

    public CompanyEmail(final Builder builder) {
        setEmail(builder.email);
        setCompanyId(builder.companyId);
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    public int getCompanyId() {
        return this.companyId;
    }

    public void setCompanyId(final int companyId) {
        this.companyId = companyId;
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

    public static class CompanyEmailPK implements Serializable {
        private String email;
        private int companyId;

        public CompanyEmailPK() {

        }

        public CompanyEmailPK(final String email, final int companyId) {
            this.email = email;
            this.companyId = companyId;
        }
    }

    public static class Builder {
        private String email;
        private int companyId;

        public Builder email(final String email) {
            this.email = email;
            return this;
        }

        public Builder companyId(final int companyId) {
            this.companyId = companyId;
            return this;
        }

        public CompanyEmail build() {
            return new CompanyEmail(this);
        }
    }

}

