
@echo off
cd /d "%~dp0"

call "%USERPROFILE%\Downloads\apache-maven-3.10.0-bin\apache-maven-3.10.0\bin\mvn.cmd" compile exec:java "-Dexec.mainClass=com.spendsphere.SpendSphereApp"

pause
