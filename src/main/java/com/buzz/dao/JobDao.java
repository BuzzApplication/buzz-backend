//package com.buzz.dao;
//
//import com.buzz.model.Job;
//import com.buzz.model.Company;
//import com.buzz.requestBody.CreateJobRequestBody;
//import com.buzz.view.JobView;
//import org.hibernate.Criteria;
//import org.hibernate.criterion.Restrictions;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
///**
// * Created by toshikijahja on 10/18/17.
// */
//public class JobDao extends BaseDao<Job> {
//
//    private final CompanyDao companyDao;
//
//    public JobDao(final SessionProvider sessionProvider) {
//        super(sessionProvider, Job.class);
//        this.companyDao = new CompanyDao(getSessionProvider());
//    }
//
//    public JobView createJob(final CreateJobRequestBody createJobRequestBody) {
//        getSessionProvider().startTransaction();
//        final Company company = companyDao.getById(createJobRequestBody.getCompanyId());
//        final Job job = new Job.Builder()
//                .title(createJobRequestBody.getTitle())
//                .company(company)
//                .type(createJobRequestBody.getType())
//                .paid(createJobRequestBody.isPaid())
//                .city(createJobRequestBody.getCity())
//                .province(createJobRequestBody.getProvince())
//                .requirement(createJobRequestBody.getRequirement())
//                .description(createJobRequestBody.getDescription())
//                .deadline(createJobRequestBody.getDeadline())
//                .build();
//        getSessionProvider().getSession().save(job);
//        getSessionProvider().commitTransaction();
//        return new JobView(job);
//    }
//
//    public List<JobView> getByCompanyId(final int companyId) {
//        final Criteria criteria = getSessionProvider().getSession().createCriteria(Job.class);
//        criteria.createAlias("company", "company");
//        criteria.add(Restrictions.eq("company.id", companyId));
//        @SuppressWarnings("unchecked")
//        final List<Job> items = (List<Job>) criteria.list();
//        return items.stream().map(JobView::new).collect(Collectors.toList());
//    }
//}
