@echo off
setlocal enabledelayedexpansion

echo ========================================
echo   Building Texas Hold'em hwx.jar
echo ========================================

REM ==== CLEAN BIN DIRECTORY ====
echo Cleaning bin...
rmdir /s /q bin 2>nul
mkdir bin

REM ==== COMPILE ALL JAVA FILES ====
echo Compiling source files...

for /r src %%f in (*.java) do (
    echo Compiling %%f
    javac -cp bin -d bin "%%f"
)

if errorlevel 1 (
    echo Compilation failed!
    pause
    exit /b
)

REM ==== COPY RESOURCES (IMAGES, HTML, TXT, XML, ETC) ====
echo Copying resources...

for /r src %%f in (*.png *.jpg *.jpeg *.gif *.txt *.html *.htm *.xml) do (
    set "rel=%%f"
    set "rel=!rel:src\=!"
    set "target=bin\!rel!"
    echo Resource: %%f
    mkdir "bin\!rel!\.." 2>nul
    copy "%%f" "!target!" >nul
)

REM ==== COPY README ====
echo Copying README...
copy README.* bin >nul 2>&1

REM ==== CREATE MANIFEST ====
echo Creating manifest...
echo Main-Class: gui.app.GUIGame > manifest.txt

REM ==== BUILD JAR ====
echo Creating hwx.jar...
jar cfm hwx.jar manifest.txt -C bin .

echo ========================================
echo   JAR BUILD COMPLETE: hwx.jar
echo ========================================
pause
