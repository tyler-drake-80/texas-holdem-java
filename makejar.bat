@echo off
echo Creating JAR file...

jar cvfm holdem.jar MANIFEST.MF -C class . -C resources .

echo Done! Runnable JAR = holdem.jar