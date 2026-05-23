package com.kxw.ta.web;

import com.kxw.ta.model.User;
import com.kxw.ta.service.PortalService;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Base64;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class BaseServlet extends HttpServlet {
    protected final PortalService service = new PortalService();
    private static final SecureRandom RANDOM = new SecureRandom();

    protected User currentUser(HttpServletRequest request) {
        Object user = request.getSession().getAttribute("user");
        return user instanceof User ? (User) user : null;
    }

    protected boolean requireRole(HttpServletRequest request, HttpServletResponse response, String role)
            throws IOException {
        User user = currentUser(request);
        if (user == null || !role.equals(user.getRole())) {
            response.sendRedirect(request.getContextPath() + "/?message=Please sign in first.");
            return false;
        }
        return true;
    }

    protected void view(HttpServletRequest request, HttpServletResponse response, String path)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/" + path).forward(request, response);
    }

    protected void flash(HttpServletRequest request, String message) {
        request.getSession().setAttribute("flash", message);
    }

    protected void error(HttpServletRequest request, String message) {
        request.getSession().setAttribute("error", message);
    }

    protected void consumeMessages(HttpServletRequest request) {
        Object flash = request.getSession().getAttribute("flash");
        Object error = request.getSession().getAttribute("error");
        if (flash != null) {
            request.setAttribute("flash", flash);
            request.getSession().removeAttribute("flash");
        }
        if (error != null) {
            request.setAttribute("error", error);
            request.getSession().removeAttribute("error");
        }
    }

    protected void verifyCsrf(HttpServletRequest request) {
        String expected = (String) request.getSession().getAttribute("csrfToken");
        String actual = request.getParameter("csrfToken");
        if (expected == null || actual == null || !expected.equals(actual)) {
            throw new IllegalArgumentException("The form session expired. Please try again.");
        }
    }

    public static String ensureCsrfToken(HttpServletRequest request) {
        Object existing = request.getSession().getAttribute("csrfToken");
        if (existing instanceof String token && !token.isBlank()) {
            return token;
        }
        byte[] bytes = new byte[24];
        RANDOM.nextBytes(bytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        request.getSession().setAttribute("csrfToken", token);
        return token;
    }
}
