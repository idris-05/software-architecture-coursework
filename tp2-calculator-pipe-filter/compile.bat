@echo off
setlocal EnableExtensions

set "SCRIPT_DIR=%~dp0"
set "APP_SRC_DIR=%SCRIPT_DIR%src"
set "OUT_DIR=%SCRIPT_DIR%out"
set "BUILD_DIR=%SCRIPT_DIR%build"
set "LIB_DIR=%SCRIPT_DIR%lib"
set "INTERP_JAR=%LIB_DIR%\interpreter.jar"
set "TMP_APP_SOURCES_FILE=%TEMP%\calculator_app_sources_%RANDOM%%RANDOM%.txt"

if not exist "%APP_SRC_DIR%" (
    echo Source directory not found: %APP_SRC_DIR%
    exit /b 1
)

if not exist "%INTERP_JAR%" (
    echo Missing interpreter JAR: %INTERP_JAR%
    exit /b 1
)

dir /s /b "%APP_SRC_DIR%\*.java" | sort > "%TMP_APP_SOURCES_FILE%"

for %%A in ("%TMP_APP_SOURCES_FILE%") do if %%~zA==0 (
    echo No application Java source files found in %APP_SRC_DIR%
    set "EXIT_CODE=1"
    goto cleanup
)

if exist "%OUT_DIR%" rmdir /s /q "%OUT_DIR%"
if exist "%BUILD_DIR%" rmdir /s /q "%BUILD_DIR%"

if not exist "%OUT_DIR%" mkdir "%OUT_DIR%"
if not exist "%LIB_DIR%" mkdir "%LIB_DIR%"

echo Compiling calculator application...
javac -cp "%INTERP_JAR%" -d "%OUT_DIR%" @"%TMP_APP_SOURCES_FILE%"
if errorlevel 1 (
    set "EXIT_CODE=1"
    goto cleanup
)

echo Launching calculator GUI...
java -cp "%OUT_DIR%;%INTERP_JAR%" calculatorApp.Main
set "EXIT_CODE=%ERRORLEVEL%"

:cleanup
del "%TMP_APP_SOURCES_FILE%" >nul 2>&1
if not defined EXIT_CODE set "EXIT_CODE=0"
exit /b %EXIT_CODE%