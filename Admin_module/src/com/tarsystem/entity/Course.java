package com.tarsystem.entity;

public class Course {
    private int courseId;
    private String courseCode;
    private String courseName;
    private String moduleOrganiser;  // 模块负责人
    private int maxTAs;  // 最多可招聘助教数量
    private int hoursPerWeek;  // 每周需要的工作小时
    private String[] requiredSkills;
    private String[] currentTAs;  // 当前被分配的助教姓名
    private String status;  // 状态：open, closed, filled
    
    // 构造函数
    public Course(int courseId, String courseCode, String courseName, 
                  String moduleOrganiser, int maxTAs, int hoursPerWeek, 
                  String[] requiredSkills, String[] currentTAs, String status) {
        this.courseId = courseId;
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.moduleOrganiser = moduleOrganiser;
        this.maxTAs = maxTAs;
        this.hoursPerWeek = hoursPerWeek;
        this.requiredSkills = requiredSkills;
        this.currentTAs = currentTAs;
        this.status = status;
    }
    
    // Getter 和 Setter 方法
    public int getCourseId() { return courseId; }
    public void setCourseId(int courseId) { this.courseId = courseId; }
    
    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }
    
    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }
    
    public String getModuleOrganiser() { return moduleOrganiser; }
    public void setModuleOrganiser(String moduleOrganiser) { 
        this.moduleOrganiser = moduleOrganiser; 
    }
    
    public int getMaxTAs() { return maxTAs; }
    public void setMaxTAs(int maxTAs) { this.maxTAs = maxTAs; }
    
    public int getHoursPerWeek() { return hoursPerWeek; }
    public void setHoursPerWeek(int hoursPerWeek) { 
        this.hoursPerWeek = hoursPerWeek; 
    }
    
    public String[] getRequiredSkills() { return requiredSkills; }
    public void setRequiredSkills(String[] requiredSkills) { 
        this.requiredSkills = requiredSkills; 
    }
    
    public String[] getCurrentTAs() { return currentTAs; }
    public void setCurrentTAs(String[] currentTAs) { 
        this.currentTAs = currentTAs; 
    }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    // 获取所需技能字符串
    public String getRequiredSkillsAsString() {
        return String.join(", ", requiredSkills);
    }
    
    // 获取当前助教字符串
    public String getCurrentTAsAsString() {
        if (currentTAs == null || currentTAs.length == 0) {
            return "暂无";
        }
        return String.join(", ", currentTAs);
    }
    
    // 获取剩余名额
    public int getRemainingSlots() {
        int currentCount = (currentTAs == null) ? 0 : currentTAs.length;
        return maxTAs - currentCount;
    }
}