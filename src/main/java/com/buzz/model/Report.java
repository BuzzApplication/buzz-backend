package com.buzz.model;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.time.Instant;

/**
 * Created by toshikijahja on 11/7/18.
 */
@Entity
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "userId")
    private User user;

    @Column
    @Enumerated(EnumType.STRING)
    private Type type;

    @Column
    private int itemId;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "reportCategoryId")
    private ReportCategory reportCategory;

    @Column
    @org.hibernate.annotations.Type(type="text")
    private String comments;

    @Column
    @CreationTimestamp
    private Instant created;

    @Column
    @UpdateTimestamp
    private Instant lastModified;

    public enum Type {
        BUZZ,
        COMMENT,
    }

    public Report() {

    }

    public Report(final Builder builder) {
        setUser(builder.user);
        setType(builder.type);
        setItemId(builder.itemId);
        setReportCategory(builder.reportCategory);
        setComments(builder.comments);
    }

    public int getId() {
        return this.id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(final User user) {
        this.user = user;
    }

    public Type getType() {
        return this.type;
    }

    public void setType(final Type type) {
        this.type = type;
    }

    public int getItemId() {
        return this.itemId;
    }

    public void setItemId(final int itemId) {
        this.itemId = itemId;
    }

    public ReportCategory getReportCategory() {
        return this.reportCategory;
    }

    public void setReportCategory(final ReportCategory reportCategory) {
        this.reportCategory = reportCategory;
    }

    public String getComments() {
        return this.comments;
    }

    public void setComments(final String comments) {
        this.comments = comments;
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
        private User user;
        private Type type;
        private int itemId;
        private ReportCategory reportCategory;
        private String comments;

        public Builder user(final User user) {
            this.user = user;
            return this;
        }

        public Builder type(final Type type) {
            this.type = type;
            return this;
        }

        public Builder itemId(final int itemId) {
            this.itemId = itemId;
            return this;
        }

        public Builder reportCategory(final ReportCategory reportCategory) {
            this.reportCategory = reportCategory;
            return this;
        }

        public Builder comments(final String comments) {
            this.comments = comments;
            return this;
        }

        public Report build() {
            return new Report(this);
        }
    }
}
