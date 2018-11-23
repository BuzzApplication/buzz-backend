package com.buzz.dao;

import com.buzz.model.Company;

/**
 * Created by toshikijahja on 6/7/17.
 */
public class CompanyDao extends BaseDao<Company> {

    public CompanyDao(final SessionProvider sessionProvider) {
        super(sessionProvider, Company.class);
    }

    /**
     *
     * @return Everyone. Will always have id=1
     */
    public Company getEveryone() {
        return getById(1);
    }


}
