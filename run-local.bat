@echo off
REM Starts VocabLearn backend with the "local" Spring profile active, so
REM application-local.yaml (the gitignored file holding the Groq API key)
REM gets merged in. Without this profile, the chatbot has no API key and
REM every /api/chat call fails.
cd /d "%~dp0"

echo [1/2] Starting Postgres container (docker compose)...
docker compose up -d
if errorlevel 1 (
    echo HATA: docker compose basarisiz oldu. Docker Desktop acik mi kontrol et.
    pause
    exit /b 1
)

echo [2/2] Starting backend with -Dspring-boot.run.profiles=local ...
call mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=local

pause
