@echo off
setlocal
echo Este script borra la cache que IntelliJ guarda de este proyecto.
echo IntelliJ tiene que estar CERRADO antes de continuar.
echo.
pause

set BASE=%LOCALAPPDATA%\JetBrains
if not exist "%BASE%" set BASE=%APPDATA%\JetBrains

for /d %%D in ("%BASE%\IntelliJIdea*") do (
    for /d %%P in ("%%D\projects\Proyecto_Mini_Windows_ProgramacionII*") do (
        echo   borrando %%P
        rd /s /q "%%P"
    )
    for /d %%C in ("%%D\compile-server\Proyecto_Mini_Windows_ProgramacionII*") do (
        echo   borrando %%C
        rd /s /q "%%C"
    )
)

if exist "%~dp0out" rd /s /q "%~dp0out"
echo   borrada la carpeta out

echo.
echo Listo. Abre IntelliJ, espera a que termine de indexar y usa Build ^> Rebuild Project.
pause
