@echo off
rem Compiles the game and the JUnit 5 tests, then runs all tests.
cd /d "%~dp0"

call "%~dp0build.bat"
if errorlevel 1 exit /b 1

if exist out\test-classes rmdir /s /q out\test-classes
mkdir out\test-classes
dir /s /b src\test\java\*.java > out\test-sources.txt
javac -encoding UTF-8 -d out\test-classes -cp "out\classes;lib\junit-platform-console-standalone-1.10.3.jar" @out\test-sources.txt
if errorlevel 1 (
    echo Bien dich test LOI.
    exit /b 1
)

chcp 65001 > nul
java -Dstdout.encoding=UTF-8 -jar lib\junit-platform-console-standalone-1.10.3.jar execute ^
     -cp "out\test-classes;out\classes" --scan-classpath out\test-classes --disable-banner
