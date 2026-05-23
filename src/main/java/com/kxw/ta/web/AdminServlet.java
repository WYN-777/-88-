package com.kxw.ta.web;

import com.kxw.ta.model.User;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AdminServlet extends BaseServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!requireRole(request, response, User.ROLE_ADMIN)) {
            return;
        }
        consumeMessages(request);
        String path = request.getPathInfo() == null ? "/overview" : request.getPathInfo();
        if ("/overview".equals(path)) {
            request.setAttribute("tas", service.usersByRole(User.ROLE_TA));
            request.setAttribute("mos", service.usersByRole(User.ROLE_MO));
            request.setAttribute("jobs", service.store().jobs());
            request.setAttribute("applications", service.allApplicationViews());
            request.setAttribute("workload", service.workloadByTa());
            view(request, response, "admin/overview.jsp");
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}
