<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.kxw.ta.model.User" %>
<%
    request.setAttribute("pageTitle", "MO Profile");
    User profile = (User) request.getAttribute("profile");
%>
<%@ include file="/WEB-INF/views/includes/top.jspf" %>

<section class="page-heading">
    <p class="eyebrow">Module Organiser</p>
    <h1>MO Profile</h1>
    <p>Maintain the organiser information shown across job posts and applicant review.</p>
</section>

<section class="panel narrow-panel">
    <form class="form-stack" action="<%= request.getContextPath() %>/mo/profile" method="post">
        <input type="hidden" name="csrfToken" value="<%= h(csrfToken) %>">
        <label>Full Name
            <input name="name" value="<%= h(profile.getName()) %>" required>
        </label>
        <label>Email
            <input type="email" name="email" value="<%= h(profile.getEmail()) %>" required>
        </label>
        <label>Department
            <input name="department" value="<%= h(profile.getDepartment()) %>">
        </label>
        <label>Primary Module
            <input name="moduleName" value="<%= h(profile.getModuleName()) %>">
        </label>
        <button class="primary-button" type="submit">Save Profile</button>
    </form>
</section>

<%@ include file="/WEB-INF/views/includes/bottom.jspf" %>
