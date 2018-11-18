package com.buzz.model;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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
public class Poll {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buzzId")
    private Buzz buzz;

    @Column (nullable = false)
    private String text;

    @Column
    private int count = 0;

    @Column
    @CreationTimestamp
    private Instant created;

    @Column
    @UpdateTimestamp
    private Instant lastModified;

    public Poll() {

    }

    public Poll(final Builder builder) {
        setBuzz(builder.buzz);
        setText(builder.text);
        setCount(builder.count);
    }

    public Buzz getBuzz() {
        return this.buzz;
    }

    public void setBuzz(final Buzz buzz) {
        this.buzz = buzz;
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

    public int getCount() {
        return this.count;
    }

    public void setCount(final int count) {
        this.count = count;
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
        private Buzz buzz;
        private String text;
        private int count;

        public Builder buzz(final Buzz buzz) {
            this.buzz = buzz;
            return this;
        }

        public Builder text(final String text) {
            this.text = text;
            return this;
        }

        public Builder count(final int count) {
            this.count = count;
            return this;
        }

        public Poll build() {
            return new Poll(this);
        }
    }
}

