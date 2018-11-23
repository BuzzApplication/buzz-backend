package com.buzz.view;

import com.buzz.model.ReportCategory;

/**
 * Created by toshikijahja on 11/7/18.
 */
public class ReportCategoryView extends BaseView {

    private final ReportCategory reportCategory;

    public ReportCategoryView(final ReportCategory reportCategory) {
        this.reportCategory = reportCategory;
    }

    public int getId() {
        return reportCategory.getId();
    }

    public String getCategory() {
        return reportCategory.getCategory();
    }
}
