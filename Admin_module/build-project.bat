@echo off
echo ========================================
echo 助教招聘系统 - 项目编译脚本 (修正版)
echo ========================================
echo.

set WEBINF=web\WEB-INF
set LIB=%WEBINF%\lib
set SRC=src
set CLASSES=%WEBINF%\classes

echo 1. 创建classes目录...
if not exist "%CLASSES%" mkdir "%CLASSES%"

echo 2. 编译所有Java源文件...
echo 编译实体类...
javac -cp "%LIB%\json-20250107.jar;%LIB%\javax.servlet-api-4.0.1.jar" -d "%CLASSES%" "%SRC%\com\tarsystem\entity\*.java"

echo 编译DAO类...
javac -cp "%CLASSES%;%LIB%\json-20250107.jar;%LIB%\javax.servlet-api-4.0.1.jar" -d "%CLASSES%" "%SRC%\com\tarsystem\dao\*.java"

echo 编译Service类...
javac -cp "%CLASSES%;%LIB%\json-20250107.jar;%LIB%\javax.servlet-api-4.0.1.jar" -d "%CLASSES%" "%SRC%\com\tarsystem\service\*.java"

echo 编译Filter类...
javac -cp "%CLASSES%;%LIB%\json-20250107.jar;%LIB%\javax.servlet-api-4.0.1.jar" -d "%CLASSES%" "%SRC%\com\tarsystem\filter\*.java"

echo 编译Servlet类...
javac -cp "%CLASSES%;%LIB%\json-20250107.jar;%LIB%\javax.servlet-api-4.0.1.jar" -d "%CLASSES%" "%SRC%\com\tarsystem\servlet\*.java"

echo 3. 检查编译结果...
if exist "%CLASSES%\com\tarsystem\filter\CharacterEncodingFilter.class" (
    echo ✓ Filter类编译成功
) else (
    echo ✗ Filter类编译失败
)

if exist "%CLASSES%\com\tarsystem\servlet\AdminDashboardServlet.class" (
    echo ✓ Servlet类编译成功
) else (
    echo ✗ Servlet类编译失败
)

echo 4. 查看编译的文件列表...
dir "%CLASSES%\com\tarsystem" /s

pause