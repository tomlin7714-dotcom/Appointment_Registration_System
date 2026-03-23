@echo off
chcp 65001
cls
echo ==========================================
echo    医疗预约系统 - 启动脚本
echo ==========================================
echo.

echo [1/3] 检查Java环境...
java -version >nul 2>&1
if errorlevel 1 (
    echo [错误] 未检测到Java环境，请先安装JDK！
    pause
    exit /b 1
)
echo [✓] Java环境正常
echo.

echo [2/3] 检查Maven环境...
mvn -version >nul 2>&1
if errorlevel 1 (
    echo [错误] 未检测到Maven环境，请先安装Maven！
    pause
    exit /b 1
)
echo [✓] Maven环境正常
echo.

echo [3/3] 启动后端服务...
echo 服务启动中，请稍候...
echo 访问地址：
echo   - 管理员端：http://localhost:8083/admin/login.html
echo   - 医生端：http://localhost:8083/doctor/login.html
echo.
echo 默认账号：
echo   - 管理员：admin / admin123
echo   - 医生：doctor1 / 123456
echo.
echo 按Ctrl+C停止服务
echo ==========================================

mvn spring-boot:run

pause