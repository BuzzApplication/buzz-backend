package com.buzz.view;

import com.buzz.model.Company;


/**
 * Created by toshikijahja on 10/18/17.
 */
public class CompanyView {

    private final Company company;

    public CompanyView(final Company company) {
        this.company = company;
    }

    public String getName() {
        return company.getName();
    }

    public int getId() {
        return company.getId();
    }

}
