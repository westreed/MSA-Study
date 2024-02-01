import requests
from fastapi import FastAPI
from py_eureka_client import eureka_client

eureka_server = "http://discovery-service:8761/eureka"
instance_port = 40010

app = FastAPI()


@app.get('/info')
async def test():
    service_instances = await eureka_client.get_application(eureka_server=eureka_server, app_name="user-service")
    if service_instances.instances:
        service = service_instances.instances[-1]
        target_url = f"http://{service.ipAddr}:{service.port.port}"

        # 예제로 GET 요청 보내기
        response = requests.get(f"{target_url}/info")

        # 응답 확인
        if response.status_code == 200:
            return f"요청 성공: {target_url} {response.text}"
        else:
            return f"요청 실패: {response.status_code} {response.text}"
    else:
        print("서비스를 찾을 수 없습니다.")


if __name__ == "__main__":
    import uvicorn
    eureka_client.init(
        eureka_server="http://discovery-service:8761/eureka",  # "http://localhost:8761/eureka",
        app_name="llm-service",
        # instance_host="127.0.0.1",
        instance_port=instance_port,
    )
    uvicorn.run("app:app", host="0.0.0.0", port=instance_port, reload=True)
