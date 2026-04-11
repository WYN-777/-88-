package com.tarsystem.servlet;

import com.tarsystem.service.AdminService;
import com.tarsystem.dao.TAFileDAO;
import com.tarsystem.dao.CourseFileDAO;
import com.tarsystem.entity.TA;
import com.tarsystem.entity.Course;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet(name = "AdminDashboardServlet", urlPatterns = {"/admin/dashboard"})
public class AdminDashboardServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // 获取所有助教和课程数据
        List<TA> taList = TAFileDAO.getAllTAs(getServletContext());
        List<Course> courseList = CourseFileDAO.getAllCourses(getServletContext());
        
        // 获取系统统计
        Map<String, Object> stats = AdminService.getSystemStatistics(getServletContext());
        
        // 设置请求属性
        request.setAttribute("taList", taList);
        request.setAttribute("courseList", courseList);
        request.setAttribute("stats", stats);
        
        // 检查是否有搜索参数
        String searchTa = request.getParameter("searchTa");
        String searchCourse = request.getParameter("searchCourse");
        
        if (searchTa != null && !searchTa.trim().isEmpty()) {
            List<TA> searchResults = AdminService.searchTAs(getServletContext(), searchTa);
            request.setAttribute("taList", searchResults);
            request.setAttribute("taSearchKeyword", searchTa);
        }
        
        if (searchCourse != null && !searchCourse.trim().isEmpty()) {
            List<Course> searchResults = AdminService.searchCourses(getServletContext(), searchCourse);
            request.setAttribute("courseList", searchResults);
            request.setAttribute("courseSearchKeyword", searchCourse);
        }
        
        // 转发到JSP页面
        RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/admin/dashboard.jsp");
        dispatcher.forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        doGet(request, response);
    }
}