@echo off
echo ========================================
echo Docker镜像构建脚本
echo ========================================
echo.

echo 1. 检查Docker是否运行...
docker version > nul 2>&1
if errorlevel 1 (
    echo 错误：Docker未运行！
    echo 请启动Docker Desktop并等待Docker引擎就绪
    pause
    exit /b 1
)

echo 2. 停止并删除旧容器（如果存在）...
docker stop admin-ta-system 2>nul
docker rm admin-ta-system 2>nul

echo 3. 删除旧镜像（如果存在）...
docker rmi admin-ta-system:latest 2>nul

echo 4. 构建Docker镜像...
echo 正在构建，请稍候...
docker build -t admin-ta-system:latest .

if errorlevel 1 (
    echo 错误：镜像构建失败！
    pause
    exit /b 1
)

echo 5. 查看构建的镜像...
docker images admin-ta-system

echo.
echo ========================================
echo 镜像构建完成！
echo 镜像名称：admin-ta-system:latest
echo.
echo 运行容器命令：
echo   docker run -d -p 8080:8080 --name admin-ta-system admin-ta-system:latest
echo.
echo 一键运行脚本：run-docker.bat
echo ========================================
pause