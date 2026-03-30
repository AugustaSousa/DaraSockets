@echo off
echo DARA GAME
echo.

echo [1/4] Compilando com UTF-8...
javac -encoding UTF-8 -d . src/main/Main.java src/controller/*.java src/model/*.java src/view/*.java src/network/*.java
if %errorlevel% neq 0 (
    echo Erro na compilacao!
    pause
    exit /b 1
)

echo [2/4] Criando MANIFEST.MF...
echo Manifest-Version: 1.0 > MANIFEST.MF
echo Main-Class: src.main.Main >> MANIFEST.MF
echo Class-Path: . >> MANIFEST.MF

echo [3/4] Criando arquivo JAR...
jar cfm DaraGame.jar MANIFEST.MF src/main/*.class src/controller/*.class src/model/*.class src/view/*.class src/network/*.class

echo [4/4] Iniciando Servidor e Clientes...
echo.

start "Servidor Dara" cmd /k "echo Servidor Dara Iniciado... & java -cp . src.network.Servidor"

timeout /t 2 /nobreak > nul

start "Cliente 1 - Jogador 1" cmd /k "echo Cliente 1 - Jogador 1 & java -jar DaraGame.jar"

timeout /t 1 /nobreak > nul

start "Cliente 2 - Jogador 2" cmd /k "echo Cliente 2 - Jogador 2 & java -jar DaraGame.jar"

pause