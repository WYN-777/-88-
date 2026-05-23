<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.kxw.ta.model.User" %>
<%
    User current = (User) session.getAttribute("user");
    if (current != null) {
        response.sendRedirect(request.getContextPath() + "/dashboard");
        return;
    }
    Object flash = session.getAttribute("flash");
    Object error = session.getAttribute("error");
    if (flash != null) {
        request.setAttribute("flash", flash);
        session.removeAttribute("flash");
    }
    if (error != null) {
        request.setAttribute("error", error);
        session.removeAttribute("error");
    }
    request.setAttribute("pageTitle", "Sign In - TA Recruitment Portal");
%>
<%@ include file="/WEB-INF/views/includes/top.jspf" %>

<section class="login-layout">
    <div class="intro-panel">
        <p class="eyebrow">Teaching Assistant Recruitment</p>
        <h1>Manage TA hiring from job posting to workload review.</h1>
        <p class="lead">A lightweight Java Servlet and JSP portal for applicants, module organisers, and administrators.</p>
        <div class="metric-row">
            <div><strong>TA</strong><span>Profiles, CVs, job search, applications</span></div>
            <div><strong>MO</strong><span>Job posting, applicant review, selection</span></div>
            <div><strong>Admin</strong><span>User oversight, recruitment statistics, workload balance</span></div>
        </div>
    </div>

    <section class="auth-panel">
        <p class="eyebrow">Welcome back</p>
        <h2>Sign In</h2>
        <p class="lead">Use your account credentials to access the portal.</p>
        <form class="form-stack" action="<%= request.getContextPath() %>/auth/login" method="post">
            <input type="hidden" name="csrfToken" value="<%= h(csrfToken) %>">
            <label>Role
                <select name="role" required>
                    <option value="TA">Teaching Assistant</option>
                    <option value="MO">Module Organiser</option>
                    <option value="ADMIN">Administrator</option>
                </select>
            </label>
            <label>Account ID
                <input name="id" autocomplete="username" required>
            </label>
            <label>Password
                <input type="password" name="password" autocomplete="current-password" required>
            </label>
            <button class="primary-button" type="submit">Sign In</button>
        </form>
        <div class="account-hints">
            <span>Demo TA: 20260001 / ta123456</span>
            <span>Demo MO: mo1001 / mo123456</span>
            <span>Admin: admin / admin123</span>
        </div>
        <p class="auth-switch">
            New here?<a href="<%= request.getContextPath() %>/register.jsp">Create a TA account</a>
        </p>
    </section>
</section>

<%@ include file="/WEB-INF/views/includes/bottom.jspf" %>
