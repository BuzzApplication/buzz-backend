//package com.buzz.dao;
//
//import com.buzz.model.Job;
//import com.buzz.model.JobEntry;
//import com.buzz.requestBody.AuthenticationRequestBody;
//import com.buzz.view.JobEntryView;
//import org.hibernate.Criteria;
//import org.hibernate.criterion.Restrictions;
//
//import java.util.Collections;
//import java.util.List;
//import java.util.function.Function;
//import java.util.stream.Collectors;
//
///**
// * Created by toshikijahja on 11/3/17.
// */
//public class JobEntryDao extends BaseDao<JobEntry> {
//
//    private final JobDao jobDao;
//
//    public JobEntryDao(final SessionProvider sessionProvider) {
//        super(sessionProvider, JobEntry.class);
//        this.jobDao = new JobDao(sessionProvider);
//    }
//
//    public List<JobEntryView> getByJobId(final int jobId) {
//        return getByJobIds(Collections.singletonList(jobId));
//    }
//
//    public List<JobEntryView> getByJobIds(final List<Integer> jobIds) {
//        final Criteria criteria = getSessionProvider().getSession().createCriteria(JobEntry.class);
//        criteria.createAlias("job", "job");
//        criteria.add(Restrictions.in("job.id", jobIds));
//        @SuppressWarnings("unchecked")
//        final List<JobEntry> jobEntries = (List<JobEntry>) criteria.list();
//        return jobEntries.stream().map(JobEntryView::new).collect(Collectors.toList());
//    }
//
//    public JobEntryView addJobEntry(final int jobId, final int userId) {
//        getSessionProvider().startTransaction();
//        final Job job = jobDao.getById(jobId);
//        final JobEntry jobEntry = new JobEntry.Builder()
//                    .job(job)
//                    .userId(userId)
//                    .status(JobEntry.Status.APPLIED)
//                    .build();
//            getSessionProvider().getSession().save(jobEntry);
//        getSessionProvider().commitTransaction();
//
//        return new JobEntryView(jobEntry);
//    }
//
//}
