package com.kxw.ta.web;

import com.kxw.ta.model.User;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DashboardServlet extends BaseServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = currentUser(request);
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }
        consumeMessages(request);
        if (User.ROLE_TA.equals(user.getRole())) {
            request.setAttribute("openJobs", service.store().openJobs());
            request.setAttribute("applications", service.applicationsForTa(user.getId()));
        } else if (User.ROLE_MO.equals(user.getRole())) {
            request.setAttribute("jobs", service.jobsForMo(user.getId()));
            request.setAttribute("applications", service.applicationsForMo(user.getId()));
        } else if (User.ROLE_ADMIN.equals(user.getRole())) {
            request.setAttribute("tas", service.usersByRole(User.ROLE_TA));
            request.setAttribute("mos", service.usersByRole(User.ROLE_MO));
            request.setAttribute("workload", service.workloadByTa());
            request.setAttribute("jobs", service.store().jobs());
            request.setAttribute("applications", service.allApplicationViews());
        }
        view(request, response, "dashboard.jsp");
    }
}
