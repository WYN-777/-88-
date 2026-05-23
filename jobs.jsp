<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="com.kxw.ta.model.Job" %>
<%@ page import="com.kxw.ta.util.TextUtil" %>
<%
    request.setAttribute("pageTitle", "Manage Jobs");
    List<Job> jobs = (List<Job>) request.getAttribute("jobs");
    Job editing = (Job) request.getAttribute("editing");
    Map<String, Integer> acceptedCounts = (Map<String, Integer>) request.getAttribute("acceptedCounts");
%>
<%@ include file="/WEB-INF/views/includes/top.jspf" %>
<%@ include file="/WEB-INF/views/includes/status.jspf" %>

<section class="page-heading">
    <p class="eyebrow">Job Management</p>
    <h1>Post and Manage Jobs</h1>
    <p>Create teaching or invigilation opportunities and keep the vacancy status current.</p>
</section>

<section class="section-grid wide-left">
    <div class="panel">
        <h2><%= editing == null ? "Post New Job" : "Edit Job" %></h2>
        <form class="form-stack" action="<%= request.getContextPath() %>/mo/job/save" method="post">
            <input type="hidden" name="csrfToken" value="<%= h(csrfToken) %>">
            <input type="hidden" name="jobId" value="<%= h(editing == null ? "" : editing.getId()) %>">
            <label>Job Title
                <input name="title" value="<%= h(editing == null ? "" : editing.getTitle()) %>" required>
            </label>
            <div class="two-cols">
                <label>Type
                    <select name="type" required>
                        <option value="Module Teaching" <%= editing != null && "Module Teaching".equals(editing.getType()) ? "selected" : "" %>>Module Teaching</option>
                        <option value="Invigilation" <%= editing != null && "Invigilation".equals(editing.getType()) ? "selected" : "" %>>Invigilation</option>
                    </select>
                </label>
                <label>Status
                    <select name="status" required>
                        <option value="Open" <%= editing == null || "Open".equals(editing.getStatus()) ? "selected" : "" %>>Open</option>
                        <option value="Closed" <%= editing != null && "Closed".equals(editing.getStatus()) ? "selected" : "" %>>Closed</option>
                    </select>
                </label>
            </div>
            <div class="two-cols">
                <label>Workload Hours per Week
                    <input type="number" min="1" name="workloadHours" value="<%= editing == null ? "4" : editing.getWorkloadHours() %>" required>
                </label>
                <label>Available Positions
                    <input type="number" min="1" name="positions" value="<%= editing == null ? "1" : editing.getPositions() %>" required>
                </label>
            </div>
            <label>Application Deadline
                <input type="date" name="deadline" value="<%= h(editing == null ? "" : editing.getDeadline()) %>" required>
            </label>
            <label>Required Skills
                <input name="skills" value="<%= h(editing == null ? "" : TextUtil.joinList(editing.getRequiredSkills())) %>" placeholder="Java, Testing, Communication">
            </label>
            <label>Description
                <textarea name="description" rows="6" required><%= h(editing == null ? "" : editing.getDescription()) %></textarea>
            </label>
            <div class="button-row">
                <button class="primary-button" type="submit"><%= editing == null ? "Post Job" : "Save Changes" %></button>
                <% if (editing != null) { %>
                    <a class="ghost-button" href="<%= request.getContextPath() %>/mo/jobs">Cancel Editing</a>
                <% } %>
            </div>
        </form>
    </div>

    <div class="panel">
        <h2>Posted Jobs</h2>
        <div class="item-list">
            <% for (Job job : jobs) { %>
                <article class="list-item stacked">
                    <div class="list-main">
                        <div>
                            <h3><%= h(job.getTitle()) %></h3>
                            <p><%= h(job.getType()) %> - <%= job.getWorkloadHours() %> h/week - Deadline <%= h(job.getDeadline()) %></p>
                        </div>
                        <span class="<%= h(statusClass(job.getStatus())) %>"><%= h(job.getStatus()) %></span>
                    </div>
                    <div class="inline-meta">
                        <span><%= acceptedCounts.getOrDefault(job.getId(), 0) %>/<%= job.getPositions() %> selected</span>
                        <span><%= h(TextUtil.joinList(job.getRequiredSkills())) %></span>
                    </div>
                    <div class="button-row">
                        <a class="small-button" href="<%= request.getContextPath() %>/mo/jobs?editId=<%= h(job.getId()) %>">Edit</a>
                        <% if ("Open".equals(job.getStatus())) { %>
                            <form action="<%= request.getContextPath() %>/mo/job/close" method="post">
                                <input type="hidden" name="csrfToken" value="<%= h(csrfToken) %>">
                                <input type="hidden" name="jobId" value="<%= h(job.getId()) %>">
                                <button class="danger-button" type="submit">Close</button>
                            </form>
                        <% } %>
                    </div>
                </article>
            <% } %>
            <% if (jobs.isEmpty()) { %><p class="empty">No jobs have been posted.</p><% } %>
        </div>
    </div>
</section>

<%@ include file="/WEB-INF/views/includes/bottom.jspf" %>
