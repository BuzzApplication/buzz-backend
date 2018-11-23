package com.buzz.source.user;

import com.buzz.auth.UserAuth;
import com.buzz.dao.ReportCategoryDao;
import com.buzz.dao.ReportDao;
import com.buzz.dao.SessionProvider;
import com.buzz.dao.UserDao;
import com.buzz.exception.BuzzException;
import com.buzz.model.Report;
import com.buzz.model.ReportCategory;
import com.buzz.model.User;
import com.buzz.requestBody.ReportRequestBody;
import com.buzz.view.ReportCategoryView;
import com.buzz.view.ReportView;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;
import java.util.List;
import java.util.Optional;

import static com.buzz.exception.BadRequest.REPORT_CATEGORY_NOT_EXIST;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

/**
 * Created by toshikijahja on 11/7/18.
 */
@Path("/user")
@UserAuth
public class ReportSource {

    @POST
    @Path("/report")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public ReportView submitReport(final ReportRequestBody reportRequestBody,
                                   @Context final SecurityContext securityContext) {
        requireNonNull(reportRequestBody);
        try (final SessionProvider sessionProvider = new SessionProvider()) {
            final UserDao userDao = new UserDao(sessionProvider);
            final ReportCategoryDao reportCategoryDao = new ReportCategoryDao(sessionProvider);
            final ReportDao reportDao = new ReportDao(sessionProvider);

            final Optional<User> user = userDao.getByGuid(securityContext.getUserPrincipal().getName());

            final Optional<ReportCategory> reportCategory = reportCategoryDao.getByIdOptional(reportRequestBody.getReportCategoryId());
            if (!reportCategory.isPresent()) {
                throw new BuzzException(REPORT_CATEGORY_NOT_EXIST);
            }

            final Report report = reportDao.submitReport(reportRequestBody, reportCategory.get(), user.get());

            return new ReportView(report);
        }
    }

    @GET
    @Path("/report/category")
    @Produces(MediaType.APPLICATION_JSON)
    public List<ReportCategoryView> getReportCategories() {

        try (final SessionProvider sessionProvider = new SessionProvider()) {
            final ReportCategoryDao reportCategoryDao = new ReportCategoryDao(sessionProvider);
            final List<ReportCategory> reportCategories = reportCategoryDao.getAll();
            return reportCategories.stream().map(ReportCategoryView::new).collect(toList());
        }
    }
}
