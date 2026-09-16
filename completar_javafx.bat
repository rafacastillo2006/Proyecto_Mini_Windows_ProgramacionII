@echo off
setlocal
set RAIZ=%~dp0
set DESTINO=%RAIZ%javafx-sdk-26.0.2\bin

if exist "%DESTINO%\jfxwebkit.dll" (
    echo jfxwebkit.dll ya esta instalada, no hay nada que hacer.
    pause
    exit /b
)

echo Falta jfxwebkit.dll, la libreria que usa el editor de texto.
echo Se va a descargar desde repo1.maven.org (unos 36 MB).
echo.

curl -L -o "%TEMP%\javafx-web.zip" https://repo1.maven.org/maven2/org/openjfx/javafx-web/26.0.2/javafx-web-26.0.2-win.jar
if not exist "%TEMP%\javafx-web.zip" goto error

echo Extrayendo...
powershell -NoProfile -Command "Expand-Archive -LiteralPath '%TEMP%\javafx-web.zip' -DestinationPath '%TEMP%\javafx-web' -Force"
if not exist "%TEMP%\javafx-web\jfxwebkit.dll" goto error

copy /y "%TEMP%\javafx-web\jfxwebkit.dll" "%DESTINO%\" >nul
del /q "%TEMP%\javafx-web.zip"
rd /s /q "%TEMP%\javafx-web"

if not exist "%DESTINO%\jfxwebkit.dll" goto error
echo   [ok] jfxwebkit.dll instalada en javafx-sdk-26.0.2\bin
echo.
echo Listo, ya puedes abrir el editor de texto de MiniWindows.
pause
exit /b

:error
echo.
echo No se pudo completar la descarga. Revisa tu conexion y vuelve a intentarlo.
pause
