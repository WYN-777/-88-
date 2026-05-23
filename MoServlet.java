package com.kxw.ta.web;

import com.kxw.ta.model.Job;
import com.kxw.ta.model.User;
import com.kxw.ta.util.TextUtil;
import java.io.IOException;
import java.util.Optional;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MoServlet extends BaseServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!requireRole(request, response, User.ROLE_MO)) {
            return;
        }
        consumeMessages(request);
        String path = request.getPathInfo() == null ? "/jobs" : request.getPathInfo();
        User user = currentUser(request);
        if ("/profile".equals(path)) {
            request.setAttribute("profile", service.store().findUser(user.getId()).orElse(user));
            view(request, response, "mo/profile.jsp");
        } else if ("/jobs".equals(path)) {
            String editId = request.getParameter("editId");
            Optional<Job> editing = service.store().findJob(editId)
                    .filter(job -> user.getId().equals(job.getOwnerMoId()));
            request.setAttribute("editing", editing.orElse(null));
            request.setAttribute("jobs", service.jobsForMo(user.getId()));
            request.setAttribute("acceptedCounts", service.store().jobs().stream()
                    .collect(java.util.stream.Collectors.toMap(Job::getId, job -> service.acceptedCount(job.getId()))));
            view(request, response, "mo/jobs.jsp");
        } else if ("/applications".equals(path)) {
            request.setAttribute("applications", service.applicationsForMo(user.getId()));
            request.setAttribute("workload", service.workloadByTa());
            view(request, response, "mo/applications.jsp");
        } else if ("/matching".equals(path)) {
            request.setAttribute("matches", service.applicantMatchesForMo(user.getId()));
            request.setAttribute("workload", service.workloadByTa());
            view(request, response, "mo/matching.jsp");
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!requireRole(request, response, User.ROLE_MO)) {
            return;
        }
        String path = request.getPathInfo() == null ? "" : request.getPathInfo();
        User user = currentUser(request);
        try {
            verifyCsrf(request);
            if ("/profile".equals(path)) {
                service.updateMoProfile(user.getId(), request.getParameter("name"), request.getParameter("email"),
                        request.getParameter("department"), request.getParameter("moduleName"));
                refreshSessionUser(request, user.getId());
                flash(request, "Profile saved successfully.");
                response.sendRedirect(request.getContextPath() + "/mo/profile");
            } else if ("/job/save".equals(path)) {
                service.saveJob(user, request.getParameter("jobId"), request.getParameter("title"),
                        request.getParameter("type"), request.getParameter("description"),
                        TextUtil.requiredInt(request.getParameter("workloadHours"), "Workload must be a whole number."),
                        TextUtil.requiredInt(request.getParameter("positions"), "Positions must be a whole number."),
                        request.getParameter("deadline"), request.getParameter("skills"),
                        request.getParameter("status"));
                flash(request, "Job saved successfully.");
                response.sendRedirect(request.getContextPath() + "/mo/jobs");
            } else if ("/job/close".equals(path)) {
                service.closeJob(user.getId(), request.getParameter("jobId"));
                flash(request, "Job closed successfully.");
                response.sendRedirect(request.getContextPath() + "/mo/jobs");
            } else if ("/review".equals(path)) {
                service.reviewApplication(user.getId(), request.getParameter("applicationId"),
                        request.getParameter("status"), request.getParameter("reviewNote"));
                flash(request, "Application reviewed successfully.");
                response.sendRedirect(request.getContextPath() + "/mo/applications");
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (IllegalArgumentException ex) {
            error(request, ex.getMessage());
            response.sendRedirect(request.getHeader("Referer") == null
                    ? request.getContextPath() + "/mo/jobs"
                    : request.getHeader("Referer"));
        }
    }

    private void refreshSessionUser(HttpServletRequest request, String id) {
        service.store().findUser(id).ifPresent(user -> request.getSession().setAttribute("user", user));
    }
}
