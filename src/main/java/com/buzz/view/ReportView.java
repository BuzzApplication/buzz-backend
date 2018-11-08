package com.buzz.view;

import com.buzz.model.Report;

/**
 * Created by toshikijahja on 11/7/18.
 */
public class ReportView extends BaseView {

    private final Report report;

    public ReportView(final Report report) {
        this.report = report;
    }

    public Report.Type getType() {
        return report.getType();
    }

    public int getItemId() {
        return report.getItemId();
    }

    public String getCategory() {
        return report.getReportCategory().getCategory();
    }

    public String getComments() {
        return report.getComments();
    }
}
