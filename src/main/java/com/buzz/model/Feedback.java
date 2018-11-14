package com.buzz.model;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
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
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    private int userId;

    @Column(nullable = false)
    @Type(type="text")
    private String text;

    @Column
    private int version;

    @Column
    private String userAgent;

    @Column
    private String systemName;

    @Column
    private String systemVersion;

    @Column
    private String carrier;

    @Column
    private String ipAddr;

    @Column
    private String locale;

    @Column
    @CreationTimestamp
    private Instant created;

    @Column
    @UpdateTimestamp
    private Instant lastModified;

    public Feedback() {

    }

    public Feedback(final Builder builder) {
        setUserId(builder.userId);
        setText(builder.text);
        setVersion(builder.version);
        setUserAgent(builder.userAgent);
        setSystemName(builder.systemName);
        setSystemVersion(builder.systemVersion);
        setCarrier(builder.carrier);
        setIpAddr(builder.ipAddr);
        setLocale(builder.locale);
    }

    public int getId() {
        return this.id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(final int userId) {
        this.userId = userId;
    }

    public String getText() {
        return text;
    }

    public void setText(final String text) {
        this.text = text;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(final String userAgent) {
        this.userAgent = userAgent;
    }

    public String getSystemName() {
        return systemName;
    }

    public void setSystemName(final String systemName) {
        this.systemName = systemName;
    }

    public String getSystemVersion() {
        return systemVersion;
    }

    public void setSystemVersion(final String systemVersion) {
        this.systemVersion = systemVersion;
    }

    public String getCarrier() {
        return carrier;
    }

    public void setCarrier(final String carrier) {
        this.carrier = carrier;
    }

    public String getIpAddr() {
        return ipAddr;
    }

    public void setIpAddr(final String ipAddr) {
        this.ipAddr = ipAddr;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(final String locale) {
        this.locale = locale;
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
        private int userId;
        private String text;
        private int version;
        private String userAgent;
        private String systemName;
        private String systemVersion;
        private String carrier;
        private String ipAddr;
        private String locale;

        public Builder userId(final int userId) {
            this.userId = userId;
            return this;
        }

        public Builder text(final String text) {
            this.text = text;
            return this;
        }

        public Builder version(final int version) {
            this.version = version;
            return this;
        }

        public Builder userAgent(final String userAgent) {
            this.userAgent = userAgent;
            return this;
        }

        public Builder systemName(final String systemName) {
            this.systemName = systemName;
            return this;
        }

        public Builder systemVersion(final String systemVersion) {
            this.systemVersion = systemVersion;
            return this;
        }

        public Builder carrier(final String carrier) {
            this.carrier = carrier;
            return this;
        }

        public Builder ipAddr(final String ipAddr) {
            this.ipAddr = ipAddr;
            return this;
        }

        public Builder locale(final String locale) {
            this.locale = locale;
            return this;
        }

        public Feedback build() {
            return new Feedback(this);
        }
    }
}
