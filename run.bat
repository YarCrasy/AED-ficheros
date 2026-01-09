@echo off
echo ========================================
echo  Gestor de Estudiantes - Compilacion
echo ========================================
echo.

echo Compilando proyecto...
call mvn clean compile

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo ERROR: La compilacion fallo.
    pause
    exit /b %ERRORLEVEL%
)

echo.
echo ========================================
echo  Compilacion exitosa!
echo ========================================
echo.
echo Ejecutando aplicacion...
call mvn javafx:run

pause
