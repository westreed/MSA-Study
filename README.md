# MSA (MicroService Architecture) 공부용 레포

## 서비스 구성

* Discovery Service : Service Discovery를 위한 서비스. 일종의 전화번호부와 같은 역할로 각각의 마이크로 서비스가 어느 위치에 있는지를 관리한다.
* Gateway Service : 모든 클라이언트의 요청을 받아서 설정해놓은 라우팅으로 보내는 서비스.
* User Service : 마이크로 서비스, 간단한 테스트를 위해 만듬.
* LLM Service : 마이크로 서비스, Python Flask와 연동을 테스트해보기 위해 만듬.


## 오류 및 해결

1. Gateway Service 오류

    Gateway Service 구성 중, `https://start.spring.io/`에서 의존성으로 등록했던 `Spring Cloud Routing:Gateway`는 `org.springframework.cloud:spring-cloud-starter-gateway-mvc`로 만들어짐. 하지만, 이 의존성은 제대로 작동하지 않았고 mvc를 제거한 `org.springframework.cloud:spring-cloud-starter-gateway`으로 진행하면서 해결됨.

    [Velog:Spring에서 Eureka Gateway 작동 안될 때](https://velog.io/@westreed/Spring%EC%97%90%EC%84%9C-Eureka-Gateway-%EC%9E%91%EB%8F%99-%EC%95%88%EB%90%A0-%EB%95%8C)