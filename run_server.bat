@echo off
title IRIS - QpiAI Opt Server
chcp 65001 >nul
cls
echo ===============================================================================
echo                IRIS / MolFunnel - QpiAI OPT LIVE API SERVER
echo ===============================================================================
echo Listening on http://0.0.0.0:8000...
echo Ready to accept UI requests and log QpiAI Opt dispatch executions.
echo.
cd /d "%~dp0"
python -m uvicorn backend.main:app --host 0.0.0.0 --port 8000
pause
