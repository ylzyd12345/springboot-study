@echo off
cd /d "F:\codes\roadmap\github-project\springboot-study\spring4demo"
mvn clean compile > compile_output.txt 2>&1
echo Compilation complete. Check compile_output.txt for details.