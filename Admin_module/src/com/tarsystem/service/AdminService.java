package com.tarsystem.service;

import com.tarsystem.dao.TAFileDAO;
import com.tarsystem.dao.CourseFileDAO;
import com.tarsystem.entity.TA;
import com.tarsystem.entity.Course;

import javax.servlet.ServletContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class AdminService {
    
    // 获取系统统计信息
    public static Map<String, Object> getSystemStatistics(ServletContext context) {
        Map<String, Object> stats = new HashMap<>();
        
        // 获取所有助教和课程
        List<TA> allTAs = TAFileDAO.getAllTAs(context);
        List<Course> allCourses = CourseFileDAO.getAllCourses(context);
        
        // 统计助教
        int totalTAs = allTAs.size();
        int availableTAs = 0;
        int busyTAs = 0;
        int totalWorkload = 0;
        
        for (TA ta : allTAs) {
            if ("available".equals(ta.getStatus())) {
                availableTAs++;
            } else if ("busy".equals(ta.getStatus())) {
                busyTAs++;
            }
            totalWorkload += ta.getCurrentWorkload();
        }
        
        // 统计课程
        int totalCourses = allCourses.size();
        int openCourses = 0;
        int totalTASlots = 0;
        int filledSlots = 0;
        
        for (Course course : allCourses) {
            if ("open".equals(course.getStatus())) {
                openCourses++;
            }
            totalTASlots += course.getMaxTAs();
            filledSlots += course.getCurrentTAs().length;
        }
        
        // 计算平均工作量和剩余名额
        double avgWorkload = totalTAs > 0 ? (double) totalWorkload / totalTAs : 0;
        int remainingSlots = totalTASlots - filledSlots;
        
        // 放入统计结果
        stats.put("totalTAs", totalTAs);
        stats.put("availableTAs", availableTAs);
        stats.put("busyTAs", busyTAs);
        stats.put("avgWorkload", String.format("%.1f", avgWorkload));
        stats.put("totalCourses", totalCourses);
        stats.put("openCourses", openCourses);
        stats.put("totalTASlots", totalTASlots);
        stats.put("filledSlots", filledSlots);
        stats.put("remainingSlots", remainingSlots);
        
        return stats;
    }
    
    // 搜索助教
    public static List<TA> searchTAs(ServletContext context, String keyword) {
        List<TA> allTAs = TAFileDAO.getAllTAs(context);
        List<TA> result = new ArrayList<>();
        
        if (keyword == null || keyword.trim().isEmpty()) {
            return allTAs;
        }
        
        keyword = keyword.toLowerCase();
        for (TA ta : allTAs) {
            if (ta.getName().toLowerCase().contains(keyword) ||
                ta.getStudentId().toLowerCase().contains(keyword) ||
                ta.getMajor().toLowerCase().contains(keyword) ||
                String.join(" ", ta.getSkills()).toLowerCase().contains(keyword)) {
                result.add(ta);
            }
        }
        
        return result;
    }
    
    // 搜索课程
    public static List<Course> searchCourses(ServletContext context, String keyword) {
        List<Course> allCourses = CourseFileDAO.getAllCourses(context);
        List<Course> result = new ArrayList<>();
        
        if (keyword == null || keyword.trim().isEmpty()) {
            return allCourses;
        }
        
        keyword = keyword.toLowerCase();
        for (Course course : allCourses) {
            if (course.getCourseName().toLowerCase().contains(keyword) ||
                course.getCourseCode().toLowerCase().contains(keyword) ||
                course.getModuleOrganiser().toLowerCase().contains(keyword) ||
                String.join(" ", course.getRequiredSkills()).toLowerCase().contains(keyword)) {
                result.add(course);
            }
        }
        
        return result;
    }
}