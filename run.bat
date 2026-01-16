@echo off
echo ========================================
echo  Gestor de Ficheros - Compilacion
echo ========================================
echo.

echo Compilando proyecto...
call mvn clean package -DskipTests

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
pushd target
java -cp "ficheros.jar;lib/*" aed.elrincon.Launcher
popd

pause
