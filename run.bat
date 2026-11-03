@echo off
rem Compiles and starts Car Tycoon. Needs JDK 17 or newer (javac and java in PATH).
cd /d "%~dp0"

call "%~dp0build.bat"
if errorlevel 1 exit /b 1

chcp 65001 > nul
java -Dfile.encoding=UTF-8 -Dstdout.encoding=UTF-8 -cp out\classes com.cartycoon.app.Main
