@echo off
echo 正在编译项目...

set WEBINF=web\WEB-INF
set LIB=%WEBINF%\lib
set SRC=src
set BIN=%WEBINF%\classes

if not exist "%BIN%" mkdir "%BIN%"

echo 编译Java文件...
javac -cp "%LIB%\json-20250107.jar;%LIB%\javax.servlet-api-4.0.1.jar" -d "%BIN%" ^
    "%SRC%\com\tarsystem\dao\*.java" ^
    "%SRC%\com\tarsystem\entity\*.java" ^
    "%SRC%\com\tarsystem\service\*.java" ^
    "%SRC%\com\tarsystem\servlet\*.java"

if %ERRORLEVEL% EQU 0 (
    echo 编译成功！
) else (
    echo 编译失败！
    pause
    exit /b 1
)

echo 检查编译结果...
dir "%BIN%" /s

pause