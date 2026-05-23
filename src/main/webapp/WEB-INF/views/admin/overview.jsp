<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="com.kxw.ta.model.*" %>
<%@ page import="com.kxw.ta.service.PortalService.ApplicationView" %>
<%
    request.setAttribute("pageTitle", "Admin Overview");
    List<User> tas = (List<User>) request.getAttribute("tas");
    List<User> mos = (List<User>) request.getAttribute("mos");
    List<Job> jobs = (List<Job>) request.getAttribute("jobs");
    List<ApplicationView> applications = (List<ApplicationView>) request.getAttribute("applications");
    Map<String, Integer> workload = (Map<String, Integer>) request.getAttribute("workload");
%>
<%@ include file="/WEB-INF/views/includes/top.jspf" %>
<%@ include file="/WEB-INF/views/includes/status.jspf" %>

<section class="page-heading">
    <p class="eyebrow">Administrative Control</p>
    <h1>Recruitment Overview</h1>
    <p>Monitor users, posted jobs, applicant outcomes, and TA workload across the school.</p>
</section>

<section class="kpi-grid">
    <div class="kpi"><span>Teaching Assistants</span><strong><%= tas.size() %></strong></div>
    <div class="kpi"><span>Module Organisers</span><strong><%= mos.size() %></strong></div>
    <div class="kpi"><span>Open Jobs</span><strong><%= jobs.stream().filter(j -> "Open".equals(j.getStatus())).count() %></strong></div>
    <div class="kpi"><span>Accepted Applications</span><strong><%= applications.stream().filter(a -> "Accepted".equals(a.getRecord().getStatus())).count() %></strong></div>
</section>

<section class="panel">
    <h2>TA Workload</h2>
    <div class="table-wrap">
        <table>
            <thead><tr><th>TA</th><th>Student ID</th><th>Major</th><th>Accepted Workload</th><th>Status</th></tr></thead>
            <tbody>
            <% for (User ta : tas) {
                int hours = workload.getOrDefault(ta.getId(), 0);
            %>
                <tr>
                    <td><%= h(ta.getName()) %></td>
                    <td><%= h(ta.getId()) %></td>
                    <td><%= h(ta.getMajor()) %></td>
                    <td><%= hours %> h/week</td>
                    <td><span class="<%= hours >= 20 ? "badge danger" : "badge good" %>"><%= hours >= 20 ? "Overloaded" : "Balanced" %></span></td>
                </tr>
            <% } %>
            </tbody>
        </table>
    </div>
</section>

<section class="section-grid">
    <div class="panel">
        <h2>Module Organisers</h2>
        <div class="table-wrap">
            <table>
                <thead><tr><th>Name</th><th>Department</th><th>Module</th><th>Email</th></tr></thead>
                <tbody>
                <% for (User mo : mos) { %>
                    <tr>
                        <td><%= h(mo.getName()) %></td>
                        <td><%= h(mo.getDepartment()) %></td>
                        <td><%= h(mo.getModuleName()) %></td>
                        <td><%= h(mo.getEmail()) %></td>
                    </tr>
                <% } %>
                </tbody>
            </table>
        </div>
    </div>
    <div class="panel">
        <h2>Teaching Assistants</h2>
        <div class="table-wrap">
            <table>
                <thead><tr><th>Name</th><th>Student ID</th><th>Major</th><th>CV</th></tr></thead>
                <tbody>
                <% for (User ta : tas) { %>
                    <tr>
                        <td><%= h(ta.getName()) %></td>
                        <td><%= h(ta.getId()) %></td>
                        <td><%= h(ta.getMajor()) %></td>
                        <td>
                            <% if (ta.getCvPath() != null && !ta.getCvPath().isBlank()) { %>
                                <a class="text-link" href="<%= request.getContextPath() %>/files/<%= h(ta.getCvPath()) %>" target="_blank">Open</a>
                            <% } else { %>
                                Missing
                            <% } %>
                        </td>
                    </tr>
                <% } %>
                </tbody>
            </table>
        </div>
    </div>
</section>

<section class="panel">
    <h2>All Jobs and Applications</h2>
    <div class="table-wrap">
        <table>
            <thead><tr><th>Applicant</th><th>Job</th><th>MO</th><th>Applied At</th><th>Status</th></tr></thead>
            <tbody>
            <% for (ApplicationView view : applications) { %>
                <tr>
                    <td><%= h(view.getTa() == null ? "Unknown Applicant" : view.getTa().getName()) %></td>
                    <td><%= h(view.getJob() == null ? "Archived Job" : view.getJob().getTitle()) %></td>
                    <td><%= h(view.getJob() == null ? "-" : view.getJob().getOwnerMoName()) %></td>
                    <td><%= h(view.getRecord().getAppliedAt()) %></td>
                    <td><span class="<%= h(statusClass(view.getRecord().getStatus())) %>"><%= h(view.getRecord().getStatus()) %></span></td>
                </tr>
            <% } %>
            <% if (applications.isEmpty()) { %>
                <tr><td colspan="5" class="empty">No application records are available.</td></tr>
            <% } %>
            </tbody>
        </table>
    </div>
</section>

<%@ include file="/WEB-INF/views/includes/bottom.jspf" %>
