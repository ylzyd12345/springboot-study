@echo off
echo ========================================
echo Maven Cache Clean and Flatten Pom Regenerate
echo ========================================

echo.
echo [1/4] Cleaning build directories...
for /d %%d in (spring4demo-*) do (
    if exist "%%d\target" (
        echo Cleaning %%d\target
        rmdir /s /q "%%d\target" >nul 2>&1
    )
)

echo.
echo [2/4] Removing flattened pom files...
del /q /f .flattened-pom.xml >nul 2>&1
for /d %%d in (spring4demo-*) do (
    if exist "%%d\.flattened-pom.xml" (
        echo Removing %%d\.flattened-pom.xml
        del /q /f "%%d\.flattened-pom.xml" >nul 2>&1
    )
)

echo.
echo [3/4] Cleaning Maven local repository cache (MyBatis-Plus)...
if exist "%USERPROFILE%\.m2\repository\com\baomidou\mybatis-plus" (
    echo Cleaning MyBatis-Plus cache
    rmdir /s /q "%USERPROFILE%\.m2\repository\com\baomidou\mybatis-plus" >nul 2>&1
)

echo.
echo [4/4] Regenerating flatten pom and resolving dependencies...
call mvn clean
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Maven clean failed!
    pause
    exit /b 1
)

echo.
echo call mvn org.codehaus.mojo:flatten-maven-plugin:flatten
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Maven flatten failed!
    pause
    exit /b 1
)

echo.
echo call mvn dependency:resolve -U
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Dependency resolution failed!
    pause
    exit /b 1
)

echo.
echo ========================================
echo Cache clean and flatten pom regeneration completed successfully!
echo ========================================
pause