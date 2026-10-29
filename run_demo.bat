@echo off
title IRIS - QpiAI Live Judge Demonstration
chcp 65001 >nul
cls
echo ===============================================================================
echo                IRIS / MolFunnel (QML-04) - LIVE JUDGE DEMONSTRATION
echo ===============================================================================
echo Starting live benchmarking and QpiAI Opt platform execution...
echo.
cd /d "%~dp0"
python scripts\run_judge_demonstration.py
echo.
echo ===============================================================================
echo Demonstration complete. Press any key to exit.
echo ===============================================================================
pause >nul
