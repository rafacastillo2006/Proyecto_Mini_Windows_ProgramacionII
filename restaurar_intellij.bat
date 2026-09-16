@echo off
setlocal
set RAIZ=%~dp0
echo Dejando el proyecto como estaba antes de Maven...
echo.

if not exist "%RAIZ%.idea" mkdir "%RAIZ%.idea"
copy /y "%RAIZ%config_restaurada\modules.xml" "%RAIZ%.idea\modules.xml" >nul
copy /y "%RAIZ%config_restaurada\misc.xml" "%RAIZ%.idea\misc.xml" >nul
copy /y "%RAIZ%config_restaurada\compiler.xml" "%RAIZ%.idea\compiler.xml" >nul
copy /y "%RAIZ%config_restaurada\encodings.xml" "%RAIZ%.idea\encodings.xml" >nul
echo   [ok] configuracion del modulo restaurada en .idea

if exist "%RAIZ%.idea\jarRepositories.xml" del /q "%RAIZ%.idea\jarRepositories.xml"
if exist "%RAIZ%pom.xml" del /q "%RAIZ%pom.xml"
if exist "%RAIZ%target" rd /s /q "%RAIZ%target"
echo   [ok] fuera pom.xml, target y jarRepositories.xml

if not exist "%RAIZ%Z\sistema\anteriores" mkdir "%RAIZ%Z\sistema\anteriores"
if exist "%RAIZ%INSTA_RAIZ" move "%RAIZ%INSTA_RAIZ" "%RAIZ%Z\sistema\anteriores\" >nul
if exist "%RAIZ%Z_Drive" move "%RAIZ%Z_Drive" "%RAIZ%Z\sistema\anteriores\" >nul
echo   [ok] INSTA_RAIZ y Z_Drive movidos a Z\sistema\anteriores

echo.
echo Listo. Abre IntelliJ, compila con Ctrl+F9 y ejecuta la configuracion MiniWindows.
pause
