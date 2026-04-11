<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>助教招聘系统</title>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body { 
            font-family: Arial, sans-serif; 
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            height: 100vh;
            display: flex;
            justify-content: center;
            align-items: center;
        }
        .container { 
            text-align: center; 
            background: white; 
            padding: 50px; 
            border-radius: 20px;
            box-shadow: 0 20px 40px rgba(0,0,0,0.2);
        }
        h1 { 
            color: #333; 
            margin-bottom: 20px; 
            font-size: 36px;
        }
        p { 
            color: #666; 
            margin-bottom: 30px; 
            font-size: 18px;
        }
        a { 
            display: inline-block; 
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white; 
            padding: 15px 40px; 
            border-radius: 50px; 
            text-decoration: none; 
            font-size: 18px;
            font-weight: bold;
            transition: transform 0.3s ease, box-shadow 0.3s ease;
        }
        a:hover { 
            transform: translateY(-3px); 
            box-shadow: 0 10px 20px rgba(0,0,0,0.2);
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>🎓 助教招聘系统</h1>
        <p>北京邮电大学国际学院助教招聘系统<br>管理员专用平台</p>
        <a href="${pageContext.request.contextPath}/admin/dashboard">进入管理员面板</a>
    </div>
</body>
</html>