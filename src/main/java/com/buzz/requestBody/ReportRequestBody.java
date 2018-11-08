package com.buzz.requestBody;

import com.buzz.model.Report;

import java.io.Serializable;

/**
 * Created by toshikijahja on 11/5/17.
 */
public class ReportRequestBody implements Serializable {

    private int itemId;
    private Report.Type type;
    private int reportCategoryId;
    private String comments;

    public ReportRequestBody() {

    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(final int itemId) {
        this.itemId = itemId;
    }

    public Report.Type getType() {
        return this.type;
    }

    public void setType(final Report.Type type) {
        this.type = type;
    }

    public int getReportCategoryId() {
        return this.reportCategoryId;
    }

    public void setReportCategoryId(final int reportCategoryId) {
        this.reportCategoryId = reportCategoryId;
    }

    public String getComments() {
        return this.comments;
    }

    public void setComments(final String comments) {
        this.comments = comments;
    }

}
