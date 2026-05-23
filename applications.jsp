<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="com.kxw.ta.model.*" %>
<%@ page import="com.kxw.ta.service.PortalService.ApplicationView" %>
<%@ page import="com.kxw.ta.util.TextUtil" %>
<%
    request.setAttribute("pageTitle", "Applicant Review");
    List<ApplicationView> applications = (List<ApplicationView>) request.getAttribute("applications");
    Map<String, Integer> workload = (Map<String, Integer>) request.getAttribute("workload");
%>
<%@ include file="/WEB-INF/views/includes/top.jspf" %>
<%@ include file="/WEB-INF/views/includes/status.jspf" %>

<section class="page-heading">
    <p class="eyebrow">Selection Workflow</p>
    <h1>Applicant Review</h1>
    <p>Review applicant profiles, open CV files, and record selection decisions.</p>
</section>

<section class="applicant-stack">
    <% for (ApplicationView view : applications) {
        User ta = view.getTa();
        Job job = view.getJob();
        int hours = ta == null ? 0 : workload.getOrDefault(ta.getId(), 0);
        boolean heavy = hours >= 20;
    %>
        <article class="panel applicant-card">
            <div class="applicant-head">
                <div>
                    <h2><%= h(ta == null ? "Unknown Applicant" : ta.getName()) %></h2>
                    <p><%= h(job == null ? "Archived Job" : job.getTitle()) %> - Applied <%= h(view.getRecord().getAppliedAt()) %></p>
                </div>
                <span class="<%= h(statusClass(view.getRecord().getStatus())) %>"><%= h(view.getRecord().getStatus()) %></span>
            </div>
            <div class="detail-grid">
                <div><span>Student ID</span><strong><%= h(ta == null ? "-" : ta.getId()) %></strong></div>
                <div><span>Major</span><strong><%= h(ta == null ? "-" : ta.getMajor()) %></strong></div>
                <div><span>Email</span><strong><%= h(ta == null ? "-" : ta.getEmail()) %></strong></div>
                <div><span>Current Workload</span><strong class="<%= heavy ? "risk-text" : "" %>"><%= hours %> h/week</strong></div>
                <div><span>Skills</span><strong><%= h(ta == null ? "-" : TextUtil.joinList(ta.getSkills())) %></strong></div>
                <div><span>CV</span>
                    <% if (ta != null && ta.getCvPath() != null && !ta.getCvPath().isBlank()) { %>
                        <a class="text-link" href="<%= request.getContextPath() %>/files/<%= h(ta.getCvPath()) %>" target="_blank">Open CV</a>
                    <% } else { %>
                        <strong>Not uploaded</strong>
                    <% } %>
                </div>
            </div>
            <% if (heavy) { %>
                <div class="notice compact error">This applicant is at or above the workload threshold.</div>
            <% } %>
            <p class="profile-text"><%= h(ta == null ? "" : ta.getProfileSummary()) %></p>
            <form class="review-form" action="<%= request.getContextPath() %>/mo/review" method="post">
                <input type="hidden" name="csrfToken" value="<%= h(csrfToken) %>">
                <input type="hidden" name="applicationId" value="<%= h(view.getRecord().getId()) %>">
                <label>Decision
                    <select name="status" required>
                        <option value="Pending" <%= "Pending".equals(view.getRecord().getStatus()) ? "selected" : "" %>>Pending</option>
                        <option value="Accepted" <%= "Accepted".equals(view.getRecord().getStatus()) ? "selected" : "" %>>Accepted</option>
                        <option value="Rejected" <%= "Rejected".equals(view.getRecord().getStatus()) ? "selected" : "" %>>Rejected</option>
                    </select>
                </label>
                <label>Review Note
                    <input name="reviewNote" value="<%= h(view.getRecord().getReviewNote()) %>">
                </label>
                <button class="primary-button" type="submit">Save Decision</button>
            </form>
        </article>
    <% } %>
    <% if (applications.isEmpty()) { %>
        <div class="panel empty-panel">No applications have been received yet.</div>
    <% } %>
</section>

<%@ include file="/WEB-INF/views/includes/bottom.jspf" %>
