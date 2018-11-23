package com.buzz.view;

import java.util.List;

/**
 * Created by toshikijahja on 10/27/18.
 */
public class BuzzListWithCompanyView extends BuzzListView {

    private int companyId;

    public BuzzListWithCompanyView(final int companyId,
                                   final List<BuzzView> buzzViews) {
        super(buzzViews);
        this.companyId = companyId;
    }

    public int getCompanyId() {
        return companyId;
    }
}
