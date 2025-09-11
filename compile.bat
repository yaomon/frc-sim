@echo off
REM Clean previous build
if exist bin rmdir /s /q bin
mkdir bin

REM Set source path
set "SRC=."

REM Compile all Java files
javac -sourcepath %SRC% -d bin %SRC%\physics\*.java %SRC%\core\*.java %SRC%\objects\*.java %SRC%\ui\*.java Main.java

REM Run the program
java -cp bin Main
