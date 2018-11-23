package com.buzz.requestBody;

import java.io.Serializable;

/**
 * Created by toshikijahja on 11/5/17.
 */
public class FeedbackRequestBody implements Serializable {

    private String text;
    private Integer version;
    private String userAgent;
    private String systemName;
    private String systemVersion;
    private String carrier;
    private String ipAddr;
    private String locale;

    public FeedbackRequestBody() {

    }

    public String getText() {
        return this.text;
    }

    public void setText(final String text) {
        this.text = text;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(final Integer version) {
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

}
