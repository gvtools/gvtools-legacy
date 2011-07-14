@echo off
SET PATH=..\..\binaries\w32;%PATH%

SET GVSIG_JARS=.
for /f "delims=" %%a in ('dir lib\ /s /b') do call :process %%a
rem echo %GVSIG_JARS%

java -cp "%GVSIG_JARS%" -Xmx500M com.iver.andami.Launcher gvSIG gvSIG/extensiones %1

:process
set GVSIG_JARS=%GVSIG_JARS%;%1
  
