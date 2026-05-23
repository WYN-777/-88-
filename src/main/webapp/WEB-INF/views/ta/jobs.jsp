<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="com.kxw.ta.model.Job" %>
<%@ page import="com.kxw.ta.service.PortalService.ApplicationView" %>
<%@ page import="com.kxw.ta.util.TextUtil" %>
<%
    request.setAttribute("pageTitle", "Available Jobs");
    List<Job> jobs = (List<Job>) request.getAttribute("jobs");
    List<ApplicationView> applications = (List<ApplicationView>) request.getAttribute("applications");
    Set<String> appliedJobIds = new HashSet<>();
    for (ApplicationView item : applications) {
        appliedJobIds.add(item.getRecord().getJobId());
    }
%>
<%@ include file="/WEB-INF/views/includes/top.jspf" %>

<section class="page-heading">
    <p class="eyebrow">Job Discovery</p>
    <h1>Available TA Jobs</h1>
    <p>Browse open positions and submit applications from the same workspace.</p>
</section>

<section class="panel">
    <form class="toolbar-form" action="<%= request.getContextPath() %>/ta/jobs" method="get">
        <label>Keyword
            <input name="keyword" value="<%= h(request.getParameter("keyword")) %>" placeholder="Skill, module, or title">
        </label>
        <label>Type
            <select name="type">
                <option value="">All types</option>
                <option value="Module Teaching" <%= "Module Teaching".equals(request.getParameter("type")) ? "selected" : "" %>>Module Teaching</option>
                <option value="Invigilation" <%= "Invigilation".equals(request.getParameter("type")) ? "selected" : "" %>>Invigilation</option>
            </select>
        </label>
        <button class="secondary-button" type="submit">Filter</button>
        <a class="ghost-button" href="<%= request.getContextPath() %>/ta/jobs">Reset</a>
    </form>
</section>

<section class="cards-grid">
    <% for (Job job : jobs) { %>
        <article class="job-card">
            <div class="card-topline">
                <span class="badge good"><%= h(job.getType()) %></span>
                <span><%= job.getWorkloadHours() %> h/week</span>
            </div>
            <h2><%= h(job.getTitle()) %></h2>
            <p><%= h(job.getDescription()) %></p>
            <dl class="meta-list">
                <div><dt>Deadline</dt><dd><%= h(job.getDeadline()) %></dd></div>
                <div><dt>Positions</dt><dd><%= job.getPositions() %></dd></div>
                <div><dt>Posted by</dt><dd><%= h(job.getOwnerMoName()) %></dd></div>
                <div><dt>Required Skills</dt><dd><%= h(TextUtil.joinList(job.getRequiredSkills())) %></dd></div>
            </dl>
            <% if (appliedJobIds.contains(job.getId())) { %>
                <button class="disabled-button" disabled>Already Applied</button>
            <% } else { %>
                <form action="<%= request.getContextPath() %>/ta/apply" method="post">
                    <input type="hidden" name="csrfToken" value="<%= h(csrfToken) %>">
                    <input type="hidden" name="jobId" value="<%= h(job.getId()) %>">
                    <button class="primary-button" type="submit">Apply for This Job</button>
                </form>
            <% } %>
        </article>
    <% } %>
    <% if (jobs.isEmpty()) { %>
        <div class="panel empty-panel">No jobs match the selected filters.</div>
    <% } %>
</section>

<%@ include file="/WEB-INF/views/includes/bottom.jspf" %>
