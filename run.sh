#!/bin/sh
# Compiles and starts Car Tycoon on Linux / macOS. Needs JDK 17 or newer.
cd "$(dirname "$0")" || exit 1
rm -rf out/classes && mkdir -p out/classes
find src/main/java -name "*.java" > out/sources.txt
javac -encoding UTF-8 -d out/classes @out/sources.txt || exit 1
java -Dfile.encoding=UTF-8 -cp out/classes com.cartycoon.app.Main
