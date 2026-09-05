@echo off
setlocal

set "SCRIPT_DIR=%~dp0"
set "APP_SRC_DIR=%SCRIPT_DIR%src"
set "INTERP_SRC_DIR=%SCRIPT_DIR%interp-code-for-demo\src"
set "OUT_DIR=%SCRIPT_DIR%out"
set "BUILD_DIR=%SCRIPT_DIR%build"
set "INTERP_BUILD_DIR=%BUILD_DIR%\interpreter"
set "LIB_DIR=%SCRIPT_DIR%lib"
set "INTERP_JAR=%LIB_DIR%\interpreter.jar"
set "TMP_APP_SOURCES_FILE=%TEMP%\calculator_app_sources.txt"
set "TMP_INTERP_SOURCES_FILE=%TEMP%\calculator_interpreter_sources.txt"

if not exist "%APP_SRC_DIR%" (
    echo Source directory not found: %APP_SRC_DIR%
    exit /b 1
)

if not exist "%INTERP_SRC_DIR%" (
    echo Interpreter source directory not found: %INTERP_SRC_DIR%
    exit /b 1
)

dir /s /b "%APP_SRC_DIR%\*.java" > "%TMP_APP_SOURCES_FILE%"
dir /s /b "%INTERP_SRC_DIR%\*.java" > "%TMP_INTERP_SOURCES_FILE%"

for %%A in ("%TMP_APP_SOURCES_FILE%") do if %%~zA==0 (
    echo No application Java source files found in %APP_SRC_DIR%
    del "%TMP_APP_SOURCES_FILE%" >nul 2>&1
    del "%TMP_INTERP_SOURCES_FILE%" >nul 2>&1
    exit /b 1
)

for %%A in ("%TMP_INTERP_SOURCES_FILE%") do if %%~zA==0 (
    echo No interpreter Java source files found in %INTERP_SRC_DIR%
    del "%TMP_APP_SOURCES_FILE%" >nul 2>&1
    del "%TMP_INTERP_SOURCES_FILE%" >nul 2>&1
    exit /b 1
)

if exist "%OUT_DIR%" rmdir /s /q "%OUT_DIR%"
if exist "%BUILD_DIR%" rmdir /s /q "%BUILD_DIR%"

if not exist "%OUT_DIR%" mkdir "%OUT_DIR%"
if not exist "%INTERP_BUILD_DIR%" mkdir "%INTERP_BUILD_DIR%"
if not exist "%LIB_DIR%" mkdir "%LIB_DIR%"

echo Compiling interpreter module...
javac -d "%INTERP_BUILD_DIR%" @"%TMP_INTERP_SOURCES_FILE%"
if errorlevel 1 (
    del "%TMP_APP_SOURCES_FILE%" >nul 2>&1
    del "%TMP_INTERP_SOURCES_FILE%" >nul 2>&1
    exit /b 1
)

echo Packaging interpreter JAR...
jar --create --file "%INTERP_JAR%" -C "%INTERP_BUILD_DIR%" .
if errorlevel 1 (
    del "%TMP_APP_SOURCES_FILE%" >nul 2>&1
    del "%TMP_INTERP_SOURCES_FILE%" >nul 2>&1
    exit /b 1
)

echo Compiling calculator application...
javac -cp "%INTERP_JAR%" -d "%OUT_DIR%" @"%TMP_APP_SOURCES_FILE%"
if errorlevel 1 (
    del "%TMP_APP_SOURCES_FILE%" >nul 2>&1
    del "%TMP_INTERP_SOURCES_FILE%" >nul 2>&1
    exit /b 1
)

del "%TMP_APP_SOURCES_FILE%" >nul 2>&1
del "%TMP_INTERP_SOURCES_FILE%" >nul 2>&1

echo Launching calculator GUI...
java -cp "%OUT_DIR%;%INTERP_JAR%" calculator.MainGUI
