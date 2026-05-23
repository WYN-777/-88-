<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="com.kxw.ta.service.PortalService.JobMatchView" %>
<%@ page import="com.kxw.ta.model.Job" %>
<%@ page import="com.kxw.ta.util.TextUtil" %>
<%
    request.setAttribute("pageTitle", "AI Matching");
    List<JobMatchView> matches = (List<JobMatchView>) request.getAttribute("matches");
%>
<%@ include file="/WEB-INF/views/includes/top.jspf" %>

<section class="page-heading">
    <p class="eyebrow">AI Matching</p>
    <h1>Recommended Jobs</h1>
    <p>Compare your profile skills with current job requirements and focus on the strongest opportunities.</p>
</section>

<section class="match-board">
    <% for (JobMatchView match : matches) {
        Job job = match.getJob();
    %>
        <article class="panel match-card">
            <div class="match-score">
                <div class="score-ring" style="--score:<%= match.getScore() %>%">
                    <strong><%= match.getScore() %>%</strong>
                    <span>Match</span>
                </div>
                <span class="match-label"><%= h(match.getLabel()) %></span>
            </div>
            <div class="match-content">
                <div class="card-topline">
                    <span class="badge good"><%= h(job.getType()) %></span>
                    <span><%= job.getWorkloadHours() %> h/week</span>
                </div>
                <h2><%= h(job.getTitle()) %></h2>
                <p><%= h(job.getDescription()) %></p>
                <div class="skill-columns">
                    <div>
                        <h3>Matched Skills</h3>
                        <p><%= h(match.getMatchedSkills().isEmpty() ? "No direct matches yet" : TextUtil.joinList(match.getMatchedSkills())) %></p>
                    </div>
                    <div>
                        <h3>Skill Gaps</h3>
                        <p><%= h(match.getMissingSkills().isEmpty() ? "No major gaps" : TextUtil.joinList(match.getMissingSkills())) %></p>
                    </div>
                </div>
                <dl class="meta-list compact-meta">
                    <div><dt>Deadline</dt><dd><%= h(job.getDeadline()) %></dd></div>
                    <div><dt>Positions</dt><dd><%= job.getPositions() %></dd></div>
                    <div><dt>Organiser</dt><dd><%= h(job.getOwnerMoName()) %></dd></div>
                </dl>
                <form action="<%= request.getContextPath() %>/ta/apply" method="post">
                    <input type="hidden" name="csrfToken" value="<%= h(csrfToken) %>">
                    <input type="hidden" name="jobId" value="<%= h(job.getId()) %>">
                    <button class="primary-button" type="submit">Apply from Recommendation</button>
                </form>
            </div>
        </article>
    <% } %>
    <% if (matches.isEmpty()) { %>
        <div class="panel empty-panel">No open jobs are available for matching.</div>
    <% } %>
</section>

<%@ include file="/WEB-INF/views/includes/bottom.jspf" %>
