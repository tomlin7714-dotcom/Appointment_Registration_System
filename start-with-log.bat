@echo off
chcp 65001
cd /d "%~dp0"
echo Starting application...
java -jar target\Appointment_Registration_System-1.0-SNAPSHOT.jar > app.log 2>&1
echo Application exited. Check app.log for details.
pause
