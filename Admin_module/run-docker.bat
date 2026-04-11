@echo off
echo ========================================
echo 助教招聘系统 - Docker容器运行脚本
echo ========================================
echo.

echo 1. 检查Docker是否运行...
docker ps > nul 2>&1
if errorlevel 1 (
    echo 错误：Docker未运行！
    echo 请启动Docker Desktop并等待Docker引擎就绪
    pause
    exit /b 1
)

echo 2. 检查镜像是否存在...
docker images admin-ta-system:latest > nul 2>&1
if errorlevel 1 (
    echo 错误：镜像不存在！
    echo 请先运行 build-docker.bat 构建镜像
    pause
    exit /b 1
)

echo 3. 停止并删除旧容器（如果存在）...
docker stop admin-ta-system 2>nul
docker rm admin-ta-system 2>nul

echo 4. 启动新容器...
echo 正在启动容器...
docker run -d -p 8080:8080 --name admin-ta-system admin-ta-system:latest

if errorlevel 1 (
    echo 错误：容器启动失败！
    pause
    exit /b 1
)

echo 5. 等待容器启动...
echo 等待10秒让Tomcat完全启动...
timeout /t 10 /nobreak >nul

echo 6. 检查容器状态...
docker ps --filter "name=admin-ta-system"

echo 7. 查看启动日志...
docker logs --tail 20 admin-ta-system

echo.
echo ========================================
echo 容器启动完成！
echo.
echo 访问地址：
echo   主页面：http://localhost:8080/
echo   管理员面板：http://localhost:8080/admin/dashboard
echo.
echo 管理命令：
echo   查看实时日志：docker logs -f admin-ta-system
echo   停止容器：docker stop admin-ta-system
echo   重启容器：docker restart admin-ta-system
echo   删除容器：docker rm admin-ta-system
echo ========================================
echo.
echo 按任意键打开浏览器...
pause >nul
start http://localhost:8080/