package com.tarsystem.dao;

import com.tarsystem.entity.TA;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import javax.servlet.ServletContext;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class TAFileDAO {
    
    // 获取所有助教
    public static List<TA> getAllTAs(ServletContext context) {
        List<TA> taList = new ArrayList<>();
        String dataPath = context.getRealPath("/data/tas.json");
        
        try (FileReader reader = new FileReader(dataPath)) {
            JSONTokener tokener = new JSONTokener(reader);
            JSONArray jsonArray = new JSONArray(tokener);
            
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObj = jsonArray.getJSONObject(i);
                
                // 从JSON数组中提取skills数组
                JSONArray skillsArray = jsonObj.getJSONArray("skills");
                String[] skills = new String[skillsArray.length()];
                for (int j = 0; j < skillsArray.length(); j++) {
                    skills[j] = skillsArray.getString(j);
                }
                
                // 创建TA对象
                TA ta = new TA(
                    jsonObj.getInt("id"),
                    jsonObj.getString("name"),
                    jsonObj.getString("studentId"),
                    jsonObj.getString("email"),
                    jsonObj.getString("major"),
                    jsonObj.getInt("year"),
                    jsonObj.getDouble("gpa"),
                    skills,
                    jsonObj.getInt("currentWorkload"),
                    jsonObj.getString("status")
                );
                
                taList.add(ta);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return taList;
    }
    
    // 获取活跃的助教
    public static List<TA> getAvailableTAs(ServletContext context) {
        List<TA> allTAs = getAllTAs(context);
        List<TA> availableTAs = new ArrayList<>();
        
        for (TA ta : allTAs) {
            if ("available".equals(ta.getStatus()) && ta.getCurrentWorkload() < 20) {
                availableTAs.add(ta);
            }
        }
        
        return availableTAs;
    }
    
    // 保存助教列表到文件
    public static void saveTAs(ServletContext context, List<TA> taList) {
        String dataPath = context.getRealPath("/data/tas.json");
        JSONArray jsonArray = new JSONArray();
        
        for (TA ta : taList) {
            JSONObject jsonObj = new JSONObject();
            jsonObj.put("id", ta.getId());
            jsonObj.put("name", ta.getName());
            jsonObj.put("studentId", ta.getStudentId());
            jsonObj.put("email", ta.getEmail());
            jsonObj.put("major", ta.getMajor());
            jsonObj.put("year", ta.getYear());
            jsonObj.put("gpa", ta.getGpa());
            
            // 将skills数组转换为JSONArray
            JSONArray skillsArray = new JSONArray(ta.getSkills());
            jsonObj.put("skills", skillsArray);
            
            jsonObj.put("currentWorkload", ta.getCurrentWorkload());
            jsonObj.put("status", ta.getStatus());
            
            jsonArray.put(jsonObj);
        }
        
        try (FileWriter writer = new FileWriter(dataPath)) {
            writer.write(jsonArray.toString(4));  // 缩进4个空格，美化输出
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}