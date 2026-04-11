package com.tarsystem.entity;

public class TA {
    private int id;
    private String name;
    private String studentId;
    private String email;
    private String major;
    private int year;  // 年级
    private double gpa;
    private String[] skills;
    private int currentWorkload;  // 当前工作量（小时/周）
    private String status;  // 状态：available, hired, busy
    
    // 构造函数
    public TA(int id, String name, String studentId, String email, 
              String major, int year, double gpa, String[] skills, 
              int currentWorkload, String status) {
        this.id = id;
        this.name = name;
        this.studentId = studentId;
        this.email = email;
        this.major = major;
        this.year = year;
        this.gpa = gpa;
        this.skills = skills;
        this.currentWorkload = currentWorkload;
        this.status = status;
    }
    
    // Getter 和 Setter 方法
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getMajor() { return major; }
    public void setMajor(String major) { this.major = major; }
    
    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }
    
    public double getGpa() { return gpa; }
    public void setGpa(double gpa) { this.gpa = gpa; }
    
    public String[] getSkills() { return skills; }
    public void setSkills(String[] skills) { this.skills = skills; }
    
    public int getCurrentWorkload() { return currentWorkload; }
    public void setCurrentWorkload(int currentWorkload) { 
        this.currentWorkload = currentWorkload; 
    }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    // 获取技能字符串
    public String getSkillsAsString() {
        return String.join(", ", skills);
    }
}