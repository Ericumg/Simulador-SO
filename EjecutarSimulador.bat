@echo off
REM Script para compilar y ejecutar el simulador de algoritmos de reemplazo de páginas
cd simu
javac FIFO\*.java LRU\*.java RUEDA\*.java NRU\*.java MENU\*.java
java MENU.MainMenu
pause
