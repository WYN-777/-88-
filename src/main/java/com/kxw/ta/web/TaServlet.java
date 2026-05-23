package com.kxw.ta.web;

import com.kxw.ta.model.Job;
import com.kxw.ta.model.User;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Locale;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

public class TaServlet extends BaseServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!requireRole(request, response, User.ROLE_TA)) {
            return;
        }
        consumeMessages(request);
        String path = request.getPathInfo() == null ? "/profile" : request.getPathInfo();
        User user = currentUser(request);
        if ("/profile".equals(path)) {
            request.setAttribute("profile", service.store().findUser(user.getId()).orElse(user));
            view(request, response, "ta/profile.jsp");
        } else if ("/jobs".equals(path)) {
            request.setAttribute("jobs", filterJobs(request, service.store().openJobs()));
            request.setAttribute("applications", service.applicationsForTa(user.getId()));
            view(request, response, "ta/jobs.jsp");
        } else if ("/matching".equals(path)) {
            request.setAttribute("matches", service.jobMatchesForTa(user.getId()));
            view(request, response, "ta/matching.jsp");
        } else if ("/applications".equals(path)) {
            request.setAttribute("applications", service.applicationsForTa(user.getId()));
            view(request, response, "ta/applications.jsp");
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!requireRole(request, response, User.ROLE_TA)) {
            return;
        }
        String path = request.getPathInfo() == null ? "" : request.getPathInfo();
        User user = currentUser(request);
        try {
            verifyCsrf(request);
            if ("/profile".equals(path)) {
                service.updateTaProfile(user.getId(), request.getParameter("name"),
                        request.getParameter("major"), request.getParameter("email"),
                        request.getParameter("contact"), request.getParameter("profileSummary"),
                        request.getParameter("skills"));
                refreshSessionUser(request, user.getId());
                flash(request, "Profile saved successfully.");
                response.sendRedirect(request.getContextPath() + "/ta/profile");
            } else if ("/cv".equals(path)) {
                uploadCv(request, user.getId());
                refreshSessionUser(request, user.getId());
                flash(request, "CV uploaded successfully.");
                response.sendRedirect(request.getContextPath() + "/ta/profile");
            } else if ("/apply".equals(path)) {
                service.applyForJob(user.getId(), request.getParameter("jobId"));
                flash(request, "Application submitted successfully.");
                response.sendRedirect(request.getContextPath() + "/ta/applications");
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (IllegalArgumentException ex) {
            error(request, ex.getMessage());
            response.sendRedirect(request.getHeader("Referer") == null
                    ? request.getContextPath() + "/ta/jobs"
                    : request.getHeader("Referer"));
        }
    }

    private List<Job> filterJobs(HttpServletRequest request, List<Job> jobs) {
        String type = request.getParameter("type");
        String keyword = request.getParameter("keyword");
        return jobs.stream()
                .filter(job -> type == null || type.isBlank() || type.equals(job.getType()))
                .filter(job -> {
                    if (keyword == null || keyword.isBlank()) {
                        return true;
                    }
                    String lower = keyword.toLowerCase(Locale.ROOT);
                    return job.getTitle().toLowerCase(Locale.ROOT).contains(lower)
                            || job.getDescription().toLowerCase(Locale.ROOT).contains(lower)
                            || String.join(" ", job.getRequiredSkills()).toLowerCase(Locale.ROOT).contains(lower);
                })
                .toList();
    }

    private void uploadCv(HttpServletRequest request, String taId) throws IOException, ServletException {
        Part part = request.getPart("cv");
        if (part == null || part.getSize() == 0) {
            throw new IllegalArgumentException("Please choose a CV file.");
        }
        if (part.getSize() > 5L * 1024L * 1024L) {
            throw new IllegalArgumentException("CV file must be no larger than 5 MB.");
        }
        String submitted = Path.of(part.getSubmittedFileName()).getFileName().toString();
        String lower = submitted.toLowerCase(Locale.ROOT);
        if (!lower.endsWith(".pdf") && !lower.endsWith(".doc") && !lower.endsWith(".docx")) {
            throw new IllegalArgumentException("CV must be a PDF, DOC, or DOCX file.");
        }
        String storedName = taId + "-" + System.currentTimeMillis() + "-" + submitted.replaceAll("[^A-Za-z0-9._-]", "_");
        Path target = service.store().getUploadsDir().resolve(storedName);
        try (InputStream input = part.getInputStream()) {
            Files.copy(input, target, StandardCopyOption.REPLACE_EXISTING);
        }
        service.updateTaCv(taId, storedName, submitted);
    }

    private void refreshSessionUser(HttpServletRequest request, String id) {
        service.store().findUser(id).ifPresent(user -> request.getSession().setAttribute("user", user));
    }
}
