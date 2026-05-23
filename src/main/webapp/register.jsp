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
    request.setAttribute("pageTitle", "Create Account - TA Recruitment Portal");
%>
<%@ include file="/WEB-INF/views/includes/top.jspf" %>

<section class="login-layout">
    <div class="intro-panel">
        <p class="eyebrow">Join the Portal</p>
        <h1>Create your Teaching Assistant account.</h1>
        <p class="lead">Set up your profile once and apply for any open TA position with a single click.</p>
        <div class="metric-row">
            <div><strong>Step 1</strong><span>Fill in your basic information</span></div>
            <div><strong>Step 2</strong><span>List your skills and summary</span></div>
            <div><strong>Step 3</strong><span>Upload CV and start applying</span></div>
        </div>
    </div>

    <section class="auth-panel">
        <p class="eyebrow">New Account</p>
        <h2>Create TA Account</h2>
        <p class="lead">Only teaching assistants can self-register. Module organisers are provisioned by the administrator.</p>
        <form class="form-stack" action="<%= request.getContextPath() %>/auth/register" method="post">
            <input type="hidden" name="csrfToken" value="<%= h(csrfToken) %>">
            <div class="two-cols">
                <label>Student ID
                    <input name="id" required>
                </label>
                <label>Password
                    <input type="password" name="password" required>
                </label>
            </div>
            <div class="two-cols">
                <label>Full Name
                    <input name="name" required>
                </label>
                <label>Major
                    <input name="major" required>
                </label>
            </div>
            <div class="two-cols">
                <label>Email
                    <input type="email" name="email" required>
                </label>
                <label>Contact
                    <input name="contact">
                </label>
            </div>
            <label>Skills
                <input name="skills" placeholder="Java, Testing, Communication">
            </label>
            <label>Profile Summary
                <textarea name="profileSummary" rows="4" placeholder="Brief description of your background and strengths"></textarea>
            </label>
            <button class="primary-button" type="submit">Create Account</button>
        </form>
        <p class="auth-switch">
            Already have an account?<a href="<%= request.getContextPath() %>/">Sign in instead</a>
        </p>
    </section>
</section>

<%@ include file="/WEB-INF/views/includes/bottom.jspf" %>
