package com.kxw.ta.web;

import com.kxw.ta.model.User;
import java.io.IOException;
import java.util.Optional;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AuthServlet extends BaseServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getPathInfo() == null ? "" : request.getPathInfo();
        try {
            verifyCsrf(request);
            if ("/login".equals(path)) {
                login(request, response);
            } else if ("/register".equals(path)) {
                register(request, response);
            } else if ("/logout".equals(path)) {
                request.getSession().invalidate();
                response.sendRedirect(request.getContextPath() + "/");
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (IllegalArgumentException ex) {
            error(request, ex.getMessage());
            String back = "/register".equals(path)
                    ? "/register.jsp"
                    : "/";
            response.sendRedirect(request.getContextPath() + back);
        }
    }

    private void login(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String id = request.getParameter("id");
        String password = request.getParameter("password");
        String role = request.getParameter("role");

        Optional<User> user = service.authenticate(id, password, role);
        if (user.isPresent()) {
            request.changeSessionId();
            request.getSession().setAttribute("user", user.get());
            flash(request, "Welcome back, " + user.get().getName() + ".");
            response.sendRedirect(request.getContextPath() + "/dashboard");
        } else {
            error(request, "Invalid account, password, or role.");
            response.sendRedirect(request.getContextPath() + "/");
        }
    }

    private void register(HttpServletRequest request, HttpServletResponse response) throws IOException {
        User user = service.registerTa(
                required(request, "id", "Student ID is required."),
                required(request, "password", "Password is required."),
                required(request, "name", "Full name is required."),
                required(request, "major", "Major is required."),
                required(request, "email", "Email is required."),
                request.getParameter("contact"),
                request.getParameter("profileSummary"),
                request.getParameter("skills"));
        request.changeSessionId();
        request.getSession().setAttribute("user", user);
        flash(request, "Your TA account has been created.");
        response.sendRedirect(request.getContextPath() + "/ta/profile");
    }

    private String required(HttpServletRequest request, String name, String message) {
        String value = request.getParameter(name);
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
        return value.trim();
    }
}
