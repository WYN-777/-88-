<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="com.kxw.ta.model.*" %>
<%@ page import="com.kxw.ta.service.PortalService.ApplicantMatchView" %>
<%@ page import="com.kxw.ta.util.TextUtil" %>
<%
    request.setAttribute("pageTitle", "AI Matching");
    List<ApplicantMatchView> matches = (List<ApplicantMatchView>) request.getAttribute("matches");
    Map<String, Integer> workload = (Map<String, Integer>) request.getAttribute("workload");
%>
<%@ include file="/WEB-INF/views/includes/top.jspf" %>
<%@ include file="/WEB-INF/views/includes/status.jspf" %>

<section class="page-heading">
    <p class="eyebrow">AI Matching</p>
    <h1>Applicant Ranking</h1>
    <p>Rank applicants by the fit between their profile skills and the requirements of each posted job.</p>
</section>

<section class="panel">
    <div class="table-wrap">
        <table>
            <thead>
            <tr>
                <th>Score</th>
                <th>Applicant</th>
                <th>Job</th>
                <th>Matched Skills</th>
                <th>Skill Gaps</th>
                <th>Workload</th>
                <th>Status</th>
                <th>Decision</th>
            </tr>
            </thead>
            <tbody>
            <% for (ApplicantMatchView view : matches) {
                User ta = view.getTa();
                Job job = view.getJob();
                int hours = ta == null ? 0 : workload.getOrDefault(ta.getId(), 0);
            %>
                <tr>
                    <td>
                        <div class="mini-score">
                            <strong><%= view.getMatch().getScore() %>%</strong>
                            <span><%= h(view.getMatch().getLabel()) %></span>
                        </div>
                    </td>
                    <td>
                        <strong><%= h(ta == null ? "Unknown Applicant" : ta.getName()) %></strong><br>
                        <span class="muted"><%= h(ta == null ? "" : ta.getId() + " - " + ta.getMajor()) %></span>
                    </td>
                    <td><%= h(job == null ? "Archived Job" : job.getTitle()) %></td>
                    <td><%= h(view.getMatch().getMatchedSkills().isEmpty() ? "None" : TextUtil.joinList(view.getMatch().getMatchedSkills())) %></td>
                    <td><%= h(view.getMatch().getMissingSkills().isEmpty() ? "None" : TextUtil.joinList(view.getMatch().getMissingSkills())) %></td>
                    <td><span class="<%= hours >= 20 ? "badge danger" : "badge good" %>"><%= hours %> h/week</span></td>
                    <td><span class="<%= h(statusClass(view.getRecord().getStatus())) %>"><%= h(view.getRecord().getStatus()) %></span></td>
                    <td>
                        <form class="inline-review" action="<%= request.getContextPath() %>/mo/review" method="post">
                            <input type="hidden" name="csrfToken" value="<%= h(csrfToken) %>">
                            <input type="hidden" name="applicationId" value="<%= h(view.getRecord().getId()) %>">
                            <input type="hidden" name="reviewNote" value="Reviewed from AI Matching">
                            <button class="small-button" name="status" value="Accepted" type="submit">Accept</button>
                            <button class="danger-button" name="status" value="Rejected" type="submit">Reject</button>
                        </form>
                    </td>
                </tr>
            <% } %>
            <% if (matches.isEmpty()) { %>
                <tr><td colspan="8" class="empty">No applicant records are available for matching.</td></tr>
            <% } %>
            </tbody>
        </table>
    </div>
</section>

<%@ include file="/WEB-INF/views/includes/bottom.jspf" %>
