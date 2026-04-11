package com.tarsystem.dao;

import com.tarsystem.entity.Course;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import javax.servlet.ServletContext;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class CourseFileDAO {
    
    // 获取所有课程
    public static List<Course> getAllCourses(ServletContext context) {
        List<Course> courseList = new ArrayList<>();
        String dataPath = context.getRealPath("/data/courses.json");
        
        try (FileReader reader = new FileReader(dataPath)) {
            JSONTokener tokener = new JSONTokener(reader);
            JSONArray jsonArray = new JSONArray(tokener);
            
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObj = jsonArray.getJSONObject(i);
                
                // 提取所需技能数组
                JSONArray skillsArray = jsonObj.getJSONArray("requiredSkills");
                String[] requiredSkills = new String[skillsArray.length()];
                for (int j = 0; j < skillsArray.length(); j++) {
                    requiredSkills[j] = skillsArray.getString(j);
                }
                
                // 提取当前助教数组
                JSONArray tasArray = jsonObj.getJSONArray("currentTAs");
                String[] currentTAs = new String[tasArray.length()];
                for (int j = 0; j < tasArray.length(); j++) {
                    currentTAs[j] = tasArray.getString(j);
                }
                
                // 创建Course对象
                Course course = new Course(
                    jsonObj.getInt("courseId"),
                    jsonObj.getString("courseCode"),
                    jsonObj.getString("courseName"),
                    jsonObj.getString("moduleOrganiser"),
                    jsonObj.getInt("maxTAs"),
                    jsonObj.getInt("hoursPerWeek"),
                    requiredSkills,
                    currentTAs,
                    jsonObj.getString("status")
                );
                
                courseList.add(course);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return courseList;
    }
    
    // 获取正在招聘的课程
    public static List<Course> getOpenCourses(ServletContext context) {
        List<Course> allCourses = getAllCourses(context);
        List<Course> openCourses = new ArrayList<>();
        
        for (Course course : allCourses) {
            if ("open".equals(course.getStatus()) && course.getRemainingSlots() > 0) {
                openCourses.add(course);
            }
        }
        
        return openCourses;
    }
    
    // 保存课程列表到文件
    public static void saveCourses(ServletContext context, List<Course> courseList) {
        String dataPath = context.getRealPath("/data/courses.json");
        JSONArray jsonArray = new JSONArray();
        
        for (Course course : courseList) {
            JSONObject jsonObj = new JSONObject();
            jsonObj.put("courseId", course.getCourseId());
            jsonObj.put("courseCode", course.getCourseCode());
            jsonObj.put("courseName", course.getCourseName());
            jsonObj.put("moduleOrganiser", course.getModuleOrganiser());
            jsonObj.put("maxTAs", course.getMaxTAs());
            jsonObj.put("hoursPerWeek", course.getHoursPerWeek());
            
            // 将所需技能数组转换为JSONArray
            JSONArray skillsArray = new JSONArray(course.getRequiredSkills());
            jsonObj.put("requiredSkills", skillsArray);
            
            // 将当前助教数组转换为JSONArray
            JSONArray tasArray = new JSONArray(course.getCurrentTAs());
            jsonObj.put("currentTAs", tasArray);
            
            jsonObj.put("status", course.getStatus());
            
            jsonArray.put(jsonObj);
        }
        
        try (FileWriter writer = new FileWriter(dataPath)) {
            writer.write(jsonArray.toString(4));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}