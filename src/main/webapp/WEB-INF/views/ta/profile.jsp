<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.kxw.ta.model.User" %>
<%@ page import="com.kxw.ta.util.TextUtil" %>
<%
    request.setAttribute("pageTitle", "TA Profile");
    User profile = (User) request.getAttribute("profile");
%>
<%@ include file="/WEB-INF/views/includes/top.jspf" %>

<section class="page-heading">
    <p class="eyebrow">Applicant Profile</p>
    <h1>TA Profile</h1>
    <p>Keep your contact details, skills, and CV ready for module organisers.</p>
</section>

<section class="section-grid">
    <div class="panel">
        <h2>Profile Details</h2>
        <form class="form-stack" action="<%= request.getContextPath() %>/ta/profile" method="post">
            <input type="hidden" name="csrfToken" value="<%= h(csrfToken) %>">
            <div class="two-cols">
                <label>Full Name
                    <input name="name" value="<%= h(profile.getName()) %>" required>
                </label>
                <label>Major
                    <input name="major" value="<%= h(profile.getMajor()) %>" required>
                </label>
            </div>
            <div class="two-cols">
                <label>Email
                    <input type="email" name="email" value="<%= h(profile.getEmail()) %>" required>
                </label>
                <label>Contact
                    <input name="contact" value="<%= h(profile.getContact()) %>">
                </label>
            </div>
            <label>Skills
                <input name="skills" value="<%= h(TextUtil.joinList(profile.getSkills())) %>">
            </label>
            <label>Profile Summary
                <textarea name="profileSummary" rows="6"><%= h(profile.getProfileSummary()) %></textarea>
            </label>
            <button class="primary-button" type="submit">Save Profile</button>
        </form>
    </div>

    <div class="panel">
        <h2>CV Upload</h2>
        <p class="muted">Accepted formats: PDF, DOC, DOCX. Maximum file size: 5 MB.</p>
        <form class="form-stack" action="<%= request.getContextPath() %>/ta/cv" method="post" enctype="multipart/form-data">
            <input type="hidden" name="csrfToken" value="<%= h(csrfToken) %>">
            <label>Choose CV File
                <input type="file" name="cv" accept=".pdf,.doc,.docx" required>
            </label>
            <button class="secondary-button" type="submit">Upload CV</button>
        </form>
        <div class="summary-box">
            <span>Current CV</span>
            <% if (profile.getCvPath() == null || profile.getCvPath().isBlank()) { %>
                <strong>Not uploaded</strong>
            <% } else { %>
                <a class="text-link" href="<%= request.getContextPath() %>/files/<%= h(profile.getCvPath()) %>" target="_blank"><%= h(profile.getCvFileName()) %></a>
            <% } %>
        </div>
    </div>
</section>

<%@ include file="/WEB-INF/views/includes/bottom.jspf" %>
