@echo off
rem Compiles src\main\java into out\classes.
cd /d "%~dp0"

if exist out\classes rmdir /s /q out\classes
mkdir out\classes

dir /s /b src\main\java\*.java > out\sources.txt
echo Dang bien dich...
javac -encoding UTF-8 -d out\classes @out\sources.txt
if errorlevel 1 (
    echo Bien dich LOI.
    exit /b 1
)
echo Bien dich xong.
exit /b 0
