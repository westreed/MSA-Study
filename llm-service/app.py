from fastapi import FastAPI
from py_eureka_client import eureka_client

from feign_provider import FeignProvider

eureka_server = "http://discovery-service:8761/eureka"
instance_port = 40010

app = FastAPI()


@app.get('/info/{test_value}')
async def info(test_value):
    user_service = FeignProvider(eureka_server=eureka_server, app_name="user-service")
    response = await user_service.a_get(f"/test/{test_value}")

    # 응답 확인
    if response.status_code == 200:
        return f"요청 성공: user-service {response.text}"
    else:
        return f"요청 실패: {response.status_code} {response.text}"


@app.get('/test')
async def test():
    return f"요청 성공: llm-service /python/test"


if __name__ == "__main__":
    import uvicorn
    eureka_client.init(
        eureka_server=eureka_server,  # "http://localhost:8761/eureka",
        app_name="llm-service",
        # instance_host="127.0.0.1",
        instance_port=instance_port,
    )
    uvicorn.run("app:app", host="0.0.0.0", port=instance_port, reload=True)
