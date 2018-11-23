package com.buzz.model;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.Instant;

/**
 * Created by toshikijahja on 11/7/18.
 */
@Entity
public class ReportCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    private String category;

    @Column
    @CreationTimestamp
    private Instant created;

    @Column
    @UpdateTimestamp
    private Instant lastModified;

    public ReportCategory() {

    }

    public ReportCategory(final Builder builder) {
        setCategory(builder.category);
    }

    public int getId() {
        return this.id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public String getCategory() {
        return this.category;
    }

    public void setCategory(final String category) {
        this.category = category;
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
        private String category;

        public Builder category(final String category) {
            this.category = category;
            return this;
        }

        public ReportCategory build() {
            return new ReportCategory(this);
        }
    }
}

