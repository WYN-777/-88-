<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="com.kxw.ta.service.PortalService.ApplicationView" %>
<%
    request.setAttribute("pageTitle", "My Applications");
    List<ApplicationView> applications = (List<ApplicationView>) request.getAttribute("applications");
%>
<%@ include file="/WEB-INF/views/includes/top.jspf" %>
<%@ include file="/WEB-INF/views/includes/status.jspf" %>

<section class="page-heading">
    <p class="eyebrow">Application Tracking</p>
    <h1>My Applications</h1>
    <p>Follow the review status of every position you have applied for.</p>
</section>

<section class="panel">
    <div class="table-wrap">
        <table>
            <thead>
            <tr><th>Job</th><th>Type</th><th>Applied At</th><th>Status</th><th>Review Note</th></tr>
            </thead>
            <tbody>
            <% for (ApplicationView view : applications) { %>
                <tr>
                    <td><%= h(view.getJob() == null ? "Archived Job" : view.getJob().getTitle()) %></td>
                    <td><%= h(view.getJob() == null ? "-" : view.getJob().getType()) %></td>
                    <td><%= h(view.getRecord().getAppliedAt()) %></td>
                    <td><span class="<%= h(statusClass(view.getRecord().getStatus())) %>"><%= h(view.getRecord().getStatus()) %></span></td>
                    <td><%= h(view.getRecord().getReviewNote()) %></td>
                </tr>
            <% } %>
            <% if (applications.isEmpty()) { %>
                <tr><td colspan="5" class="empty">No applications have been submitted yet.</td></tr>
            <% } %>
            </tbody>
        </table>
    </div>
</section>

<%@ include file="/WEB-INF/views/includes/bottom.jspf" %>
