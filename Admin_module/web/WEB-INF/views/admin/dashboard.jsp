<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.tarsystem.entity.TA, com.tarsystem.entity.Course, java.util.List, java.util.Map" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>管理员仪表板 - 助教招聘系统</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        /* 基础样式 */
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background-color: #f5f7fa;
            color: #333;
            line-height: 1.6;
        }
        
        .container {
            max-width: 1200px;
            margin: 0 auto;
            padding: 20px;
        }
        
        /* 头部样式 */
        header {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 20px 0;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        }
        
        .header-content {
            display: flex;
            justify-content: space-between;
            align-items: center;
        }
        
        h1 {
            font-size: 28px;
            font-weight: 600;
        }
        
        /* 统计卡片样式 */
        .stats-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
            gap: 20px;
            margin: 30px 0;
        }
        
        .stat-card {
            background: white;
            border-radius: 10px;
            padding: 25px;
            box-shadow: 0 5px 15px rgba(0,0,0,0.08);
            transition: transform 0.3s ease, box-shadow 0.3s ease;
        }
        
        .stat-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 8px 25px rgba(0,0,0,0.12);
        }
        
        .stat-value {
            font-size: 36px;
            font-weight: bold;
            color: #667eea;
            margin: 10px 0;
        }
        
        .stat-label {
            color: #666;
            font-size: 14px;
            text-transform: uppercase;
            letter-spacing: 1px;
        }
        
        /* 搜索框样式 */
        .search-section {
            background: white;
            padding: 20px;
            border-radius: 10px;
            margin-bottom: 20px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.05);
        }
        
        .search-form {
            display: flex;
            gap: 15px;
            flex-wrap: wrap;
        }
        
        .search-group {
            flex: 1;
            min-width: 300px;
        }
        
        .search-group label {
            display: block;
            margin-bottom: 8px;
            font-weight: 600;
            color: #444;
        }
        
        .search-input {
            width: 100%;
            padding: 12px 20px;
            border: 2px solid #e0e0e0;
            border-radius: 8px;
            font-size: 16px;
            transition: border-color 0.3s ease;
        }
        
        .search-input:focus {
            outline: none;
            border-color: #667eea;
        }
        
        .search-btn {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            border: none;
            padding: 12px 30px;
            border-radius: 8px;
            font-size: 16px;
            font-weight: 600;
            cursor: pointer;
            transition: transform 0.2s ease;
        }
        
        .search-btn:hover {
            transform: translateY(-2px);
        }
        
        /* 表格容器 */
        .section-container {
            background: white;
            border-radius: 10px;
            padding: 25px;
            margin-bottom: 30px;
            box-shadow: 0 5px 15px rgba(0,0,0,0.08);
        }
        
        .section-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 20px;
            padding-bottom: 15px;
            border-bottom: 2px solid #f0f0f0;
        }
        
        h2 {
            color: #333;
            font-size: 22px;
        }
        
        .record-count {
            background: #f0f7ff;
            color: #667eea;
            padding: 6px 12px;
            border-radius: 20px;
            font-size: 14px;
            font-weight: 600;
        }
        
        /* 表格样式 */
        .data-table {
            width: 100%;
            border-collapse: collapse;
            font-size: 14px;
        }
        
        .data-table th {
            background: linear-gradient(135deg, #f0f7ff 0%, #e6f3ff 100%);
            color: #444;
            font-weight: 600;
            text-align: left;
            padding: 15px;
            border-bottom: 2px solid #ddd;
        }
        
        .data-table td {
            padding: 15px;
            border-bottom: 1px solid #eee;
        }
        
        .data-table tr:hover {
            background-color: #f9f9f9;
        }
        
        /* 状态标签 */
        .status {
            padding: 6px 12px;
            border-radius: 20px;
            font-size: 12px;
            font-weight: 600;
            text-transform: uppercase;
        }
        
        .status-available {
            background: #d4edda;
            color: #155724;
        }
        
        .status-busy {
            background: #f8d7da;
            color: #721c24;
        }
        
        .status-open {
            background: #d4edda;
            color: #155724;
        }
        
        .status-closed {
            background: #f8d7da;
            color: #721c24;
        }
        
        /* 技能标签 */
        .skill-tag {
            display: inline-block;
            background: #e0f7ff;
            color: #0066cc;
            padding: 4px 8px;
            border-radius: 4px;
            font-size: 12px;
            margin: 2px;
        }
        
        /* 响应式设计 */
        @media (max-width: 768px) {
            .header-content {
                flex-direction: column;
                text-align: center;
                gap: 15px;
            }
            
            .search-group {
                min-width: 100%;
            }
            
            .data-table {
                display: block;
                overflow-x: auto;
            }
        }
        
        /* 高亮搜索结果 */
        .highlight {
            background-color: #fffacd;
            padding: 2px 4px;
            border-radius: 4px;
        }
    </style>
</head>
<body>
    <header>
        <div class="container">
            <div class="header-content">
                <h1>助教招聘系统 - 管理员仪表板</h1>
                <div class="user-info">
                    欢迎，管理员 | 
                    <a href="#" style="color: white; text-decoration: underline;">退出登录</a>
                </div>
            </div>
        </div>
    </header>

    <main class="container">
        <!-- 统计信息 -->
        <div class="stats-grid">
            <div class="stat-card">
                <div class="stat-value">${stats.totalTAs}</div>
                <div class="stat-label">总助教数</div>
            </div>
            <div class="stat-card">
                <div class="stat-value">${stats.availableTAs}</div>
                <div class="stat-label">可分配助教</div>
            </div>
            <div class="stat-card">
                <div class="stat-value">${stats.avgWorkload}</div>
                <div class="stat-label">平均工作量(小时/周)</div>
            </div>
            <div class="stat-card">
                <div class="stat-value">${stats.totalCourses}</div>
                <div class="stat-label">总课程数</div>
            </div>
            <div class="stat-card">
                <div class="stat-value">${stats.openCourses}</div>
                <div class="stat-label">招聘中课程</div>
            </div>
            <div class="stat-card">
                <div class="stat-value">${stats.remainingSlots}</div>
                <div class="stat-label">剩余名额</div>
            </div>
        </div>

        <!-- 搜索区域 -->
        <div class="search-section">
            <form method="get" action="${pageContext.request.contextPath}/admin/dashboard" class="search-form">
                <div class="search-group">
                    <label for="searchTa">搜索助教:</label>
                    <input type="text" id="searchTa" name="searchTa" 
                           value="${taSearchKeyword != null ? taSearchKeyword : ''}"
                           class="search-input" 
                           placeholder="输入姓名、学号、专业或技能...">
                </div>
                <div class="search-group">
                    <label for="searchCourse">搜索课程:</label>
                    <input type="text" id="searchCourse" name="searchCourse" 
                           value="${courseSearchKeyword != null ? courseSearchKeyword : ''}"
                           class="search-input" 
                           placeholder="输入课程名称、代码或负责人...">
                </div>
                <div style="align-self: flex-end;">
                    <button type="submit" class="search-btn">搜索</button>
                </div>
            </form>
        </div>

        <!-- 助教信息表格 -->
        <div class="section-container">
            <div class="section-header">
                <h2>助教信息</h2>
                <div class="record-count">共 ${taList.size()} 位助教</div>
            </div>
            
            <table class="data-table">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>姓名</th>
                        <th>学号</th>
                        <th>专业</th>
                        <th>年级</th>
                        <th>GPA</th>
                        <th>技能</th>
                        <th>当前工作量</th>
                        <th>状态</th>
                    </tr>
                </thead>
                <tbody>
                    <%
                        List<TA> taList = (List<TA>) request.getAttribute("taList");
                        String taKeyword = (String) request.getAttribute("taSearchKeyword");
                        
                        if (taList != null && !taList.isEmpty()) {
                            for (TA ta : taList) {
                                String name = ta.getName();
                                String studentId = ta.getStudentId();
                                String major = ta.getMajor();
                                String skills = ta.getSkillsAsString();
                                String statusClass = "status-" + ta.getStatus();
                                
                                // 高亮搜索关键词
                                if (taKeyword != null && !taKeyword.trim().isEmpty()) {
                                    String keyword = taKeyword.toLowerCase();
                                    if (name.toLowerCase().contains(keyword)) {
                                        name = highlightText(name, taKeyword);
                                    }
                                    if (studentId.toLowerCase().contains(keyword)) {
                                        studentId = highlightText(studentId, taKeyword);
                                    }
                                    if (major.toLowerCase().contains(keyword)) {
                                        major = highlightText(major, taKeyword);
                                    }
                                    if (skills.toLowerCase().contains(keyword)) {
                                        skills = highlightSkills(skills, taKeyword);
                                    }
                                }
                    %>
                    <tr>
                        <td><%= ta.getId() %></td>
                        <td><%= name %></td>
                        <td><%= studentId %></td>
                        <td><%= major %></td>
                        <td>Year <%= ta.getYear() %></td>
                        <td><%= String.format("%.2f", ta.getGpa()) %></td>
                        <td><%= skills %></td>
                        <td><%= ta.getCurrentWorkload() %> 小时/周</td>
                        <td><span class="status <%= statusClass %>"><%= ta.getStatus() %></span></td>
                    </tr>
                    <%
                            }
                        } else {
                    %>
                    <tr>
                        <td colspan="9" style="text-align: center; padding: 40px; color: #666;">
                            暂无助教信息
                        </td>
                    </tr>
                    <%
                        }
                    %>
                </tbody>
            </table>
        </div>

        <!-- 课程信息表格 -->
        <div class="section-container">
            <div class="section-header">
                <h2>课程信息</h2>
                <div class="record-count">共 ${courseList.size()} 门课程</div>
            </div>
            
            <table class="data-table">
                <thead>
                    <tr>
                        <th>课程ID</th>
                        <th>课程代码</th>
                        <th>课程名称</th>
                        <th>模块负责人</th>
                        <th>所需助教数</th>
                        <th>每周时长</th>
                        <th>所需技能</th>
                        <th>当前助教</th>
                        <th>剩余名额</th>
                        <th>状态</th>
                    </tr>
                </thead>
                <tbody>
                    <%
                        List<Course> courseList = (List<Course>) request.getAttribute("courseList");
                        String courseKeyword = (String) request.getAttribute("courseSearchKeyword");
                        
                        if (courseList != null && !courseList.isEmpty()) {
                            for (Course course : courseList) {
                                String courseName = course.getCourseName();
                                String courseCode = course.getCourseCode();
                                String moduleOrganiser = course.getModuleOrganiser();
                                String requiredSkills = course.getRequiredSkillsAsString();
                                String currentTAs = course.getCurrentTAsAsString();
                                String statusClass = "status-" + course.getStatus();
                                
                                // 高亮搜索关键词
                                if (courseKeyword != null && !courseKeyword.trim().isEmpty()) {
                                    String keyword = courseKeyword.toLowerCase();
                                    if (courseName.toLowerCase().contains(keyword)) {
                                        courseName = highlightText(courseName, courseKeyword);
                                    }
                                    if (courseCode.toLowerCase().contains(keyword)) {
                                        courseCode = highlightText(courseCode, courseKeyword);
                                    }
                                    if (moduleOrganiser.toLowerCase().contains(keyword)) {
                                        moduleOrganiser = highlightText(moduleOrganiser, courseKeyword);
                                    }
                                    if (requiredSkills.toLowerCase().contains(keyword)) {
                                        requiredSkills = highlightSkills(requiredSkills, courseKeyword);
                                    }
                                }
                    %>
                    <tr>
                        <td><%= course.getCourseId() %></td>
                        <td><%= courseCode %></td>
                        <td><%= courseName %></td>
                        <td><%= moduleOrganiser %></td>
                        <td><%= course.getMaxTAs() %> 人</td>
                        <td><%= course.getHoursPerWeek() %> 小时/周</td>
                        <td><%= requiredSkills %></td>
                        <td><%= currentTAs %></td>
                        <td>
                            <strong><%= course.getRemainingSlots() %></strong> 人
                        </td>
                        <td><span class="status <%= statusClass %>"><%= course.getStatus() %></span></td>
                    </tr>
                    <%
                            }
                        } else {
                    %>
                    <tr>
                        <td colspan="10" style="text-align: center; padding: 40px; color: #666;">
                            暂无课程信息
                        </td>
                    </tr>
                    <%
                        }
                    %>
                </tbody>
            </table>
        </div>
    </main>

    <footer style="text-align: center; margin: 40px 0; color: #666;">
        <p>北京邮电大学国际学院助教招聘系统 © 2026</p>
    </footer>

    <!-- 高亮搜索关键词的方法 -->
    <%!
        // 高亮文本
        public String highlightText(String text, String keyword) {
            if (text == null || keyword == null) return text;
            return text.replaceAll("(?i)(" + keyword + ")", "<span class='highlight'>$1</span>");
        }
        
        // 高亮技能
        public String highlightSkills(String skills, String keyword) {
            if (skills == null || keyword == null) return skills;
            String[] skillArray = skills.split(", ");
            StringBuilder result = new StringBuilder();
            for (String skill : skillArray) {
                if (skill.toLowerCase().contains(keyword.toLowerCase())) {
                    result.append("<span class='highlight'>").append(skill).append("</span>, ");
                } else {
                    result.append(skill).append(", ");
                }
            }
            if (result.length() > 0) {
                result.setLength(result.length() - 2); // 移除最后的", "
            }
            return result.toString();
        }
    %>
</body>
</html>