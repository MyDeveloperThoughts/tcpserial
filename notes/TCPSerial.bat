@echo off
where /Q java.exe
if "%ERRORLEVEL%" == "0" goto startTCPSerial
java -jar TCPSerial.jar
echo Java does not appear to be installed on this machine.
echo Java Version 21 or later can be downloaded from https://adoptium.net/ or https://www.oracle.com/java/technologies/downloads/
exit /B
:startTCPSerial
echo
java -jar TCPSerial.jar
