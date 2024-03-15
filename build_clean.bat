@echo off

rem .env 파일의 각 줄을 반복하면서 환경 변수로 설정
for /f "tokens=1* delims==" %%a in ('type ".env"') do (
    set "%%a=%%b"
)

echo Discovery-Service Build...
cd discovery-service
call gradlew clean build -x test
call docker build -t msa-study-discovery-service:%TAG% .
cd ..

echo Gateway-Service Build...
cd gateway-service
call gradlew clean build -x test
call docker build -t msa-study-gateway-service:%TAG% .
cd ..

echo Config-Service Build...
cd config-service
call gradlew clean build -x test
call docker build -t msa-study-config-service:%TAG% .
cd ..

echo User-Service Build...
cd user-service
call gradlew clean build -x test
call docker build -t msa-study-user-service:%TAG% .
cd ..

echo LLM-Service Build...
cd llm-service
call docker build -t msa-study-llm-service:%TAG% .
cd ..

echo All Applications Builded.

call docker-compose up -d
echo All Applications Docker Run.

for /f "tokens=*" %%i in ('docker images -f "dangling=true" -q') do docker rmi %%i
REM call docker rmi $(docker images -f "dangling=true" -q) || true

pause