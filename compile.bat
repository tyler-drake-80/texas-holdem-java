@echo off
set SRC=src
set OUT=class

echo Cleaning output directory...
if exist %OUT% rmdir /s /q %OUT%
mkdir %OUT%

echo Building sources list...
del sources.txt 2>nul
for /r %SRC% %%f in (*.java) do echo %%f >> sources.txt

echo Compiling...
javac -d %OUT% -cp %SRC% @sources.txt

if %errorlevel% neq 0 (
    echo.
    echo ======================
    echo   COMPILATION FAILED
    echo ======================
    exit /b 1
)

echo.
echo ======================
echo   COMPILATION SUCCESS
echo ======================
