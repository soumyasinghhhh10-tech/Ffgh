@echo off
REM Run this from the project folder (the one that has the "src" folder).
REM Needs: JDK 17+ and WiX Toolset 3.x (for jpackage .exe). Both must be in PATH.

echo [1/4] Making logo and icon...
if not exist build mkdir build
javac -d build src\*.java || goto :error
java -cp build AppIcon || goto :error

echo [2/4] Making the jar...
if not exist dist mkdir dist
jar --create --file dist\NeonSnake.jar --main-class Main -C build . || goto :error

echo [3/4] Making the Windows installer...
jpackage --type exe --name "NeonSnake LAN" --app-version 1.0 --vendor "Your Name" ^
  --input dist --main-jar NeonSnake.jar --main-class Main ^
  --icon icon.ico --win-shortcut --win-menu --win-dir-chooser --dest installer || goto :error

echo [4/4] Done! Your installer is inside the "installer" folder.
goto :eof

:error
echo Something failed, read the message above.
exit /b 1
