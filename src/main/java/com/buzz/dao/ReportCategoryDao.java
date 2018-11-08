package com.buzz.dao;

import com.buzz.model.ReportCategory;

/**
 * Created by toshikijahja on 11/7/18.
 */
public class ReportCategoryDao extends BaseDao<ReportCategory> {

    public ReportCategoryDao(final SessionProvider sessionProvider) {
        super(sessionProvider, ReportCategory.class);
    }
}
