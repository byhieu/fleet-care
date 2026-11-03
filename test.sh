#!/bin/sh
# Compiles the game and the JUnit 5 tests, then runs all tests (Linux / macOS).
cd "$(dirname "$0")" || exit 1
rm -rf out/classes out/test-classes && mkdir -p out/classes out/test-classes
find src/main/java -name "*.java" > out/sources.txt
javac -encoding UTF-8 -d out/classes @out/sources.txt || exit 1
find src/test/java -name "*.java" > out/test-sources.txt
JUNIT=lib/junit-platform-console-standalone-1.10.3.jar
javac -encoding UTF-8 -d out/test-classes -cp "out/classes:$JUNIT" @out/test-sources.txt || exit 1
java -jar $JUNIT execute -cp "out/test-classes:out/classes" --scan-classpath out/test-classes --disable-banner
