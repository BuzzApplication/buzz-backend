//package com.buzz.dao;
//
//import com.buzz.model.Company;
//import com.buzz.requestBody.CreateCompanyRequestBody;
//import com.buzz.view.CompanyView;
//
///**
// * Created by toshikijahja on 10/18/17.
// */
//public class CompanyDao extends BaseDao<Company> {
//
//    public CompanyDao(final SessionProvider sessionProvider) {
//        super(sessionProvider, Company.class);
//    }
//
//    public CompanyView createCompany(final CreateCompanyRequestBody createCompanyRequestBody) {
//        getSessionProvider().startTransaction();
//        final Company company = new Company.Builder()
//                .name(createCompanyRequestBody.getName())
//                .logoUrl(createCompanyRequestBody.getLogoUrl())
//                .city(createCompanyRequestBody.getCity())
//                .province(createCompanyRequestBody.getProvince())
//                .companySize(createCompanyRequestBody.getCompanySize())
//                .build();
//        getSessionProvider().getSession().save(company);
//        getSessionProvider().commitTransaction();
//        return new CompanyView(company);
//    }
//}
