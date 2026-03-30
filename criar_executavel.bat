@echo off
echo DARA GAME - Criar Executavel
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

echo [4/4] Criando executavel BAT...
echo @echo off > DaraGame.bat
echo echo Iniciando Dara Game... >> DaraGame.bat
echo java -jar DaraGame.jar >> DaraGame.bat
echo pause >> DaraGame.bat

echo.
echo EXECUTAVEL CRIADO COM SUCESSO
echo.
echo Para iniciar o servidor primeiro:
echo   java src.network.Servidor
echo.
echo Para executar o jogo:
echo   1. Duplo clique em DaraGame.bat
echo   ou
echo   2. java -jar DaraGame.jar
echo.
pause