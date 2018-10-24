package com.buzz.view;

import java.util.List;

/**
 * Created by toshikijahja on 10/18/17.
 */
public class BuzzListView {

    private final List<BuzzView> buzzViews;

    public BuzzListView(final List<BuzzView> buzzViews) {
        this.buzzViews = buzzViews;
    }

    public List<BuzzView> getBuzzList() {
        return buzzViews;
    }
}
