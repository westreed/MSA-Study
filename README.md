# MSA (MicroService Architecture) 공부용 레포

## 서비스 구성

* Discovery Service : Service Discovery를 위한 서비스. 일종의 전화번호부와 같은 역할로 각각의 마이크로 서비스가 어느 위치에 있는지를 관리한다.
* Gateway Service : 모든 클라이언트의 요청을 받아서 설정해놓은 라우팅으로 보내는 서비스.
* Config Service : 공통된 설정정보를 Private GitHub Repository에 업로드해두고, 필요한 서비스에게 제공하는 설정 서비스.
* User Service : 마이크로 서비스, 간단한 테스트를 위해 만듬.
* LLM Service : 마이크로 서비스, Python Flask와 연동을 테스트해보기 위해 만듬.
* Frontend Service : 사용자에게 보여줄 프론트엔드.

<img src="https://github.com/westreed/MSA-Study/blob/main/_src/docker_container.png"/>
<img src="https://github.com/westreed/MSA-Study/blob/main/_src/internal_request.png"/>

## 마이크로서비스 간 통신 시 기본 메시징 패턴

마이크로서비스 간 통신은 크게 두가지 방법이 있다. `1. 동기 통신`, `2. 비동기 통신`이다.
각 패턴은 장단점이 있어 로직에 따라 필요한 패턴을 적용한다.

### 동기 통신

동기 통신은 대표적으로 `HTTP`, `gRPC` 를 이용해 구현한다.
동기 통신은 A->B로 통신 시 B에 대한 응답을 기다리고 그동안 A는 블로킹된다. 동기 통신을 사용하면 사용할수록 마이크로서비스 간 결합도는 증가한다.

### 비동기 통신

비동기 통신은 A->B로 통신 시 B에 대한 응답을 기다리지 않는다. 해당 요청을 비동기적으로 처리한다.
비동기 통신을 하고자 한다면 중간 매개체가 필요하다. 주로 메시지 브로커 역할을 수행하는 `Apache Kafka`나 `aws SQS`, `RabbitMQ` 등의 솔루션을 사용할 수 있다.

#### 비동기 통신의 장점

1. 결합 감소<br/>
    동기 통신은 소비자에 대한 정보를 알아야하고 그 과정에서 마이크로서비스 간 결합도가 증가한다. 하지만 비동기 통신은 메시지 발신자가 소비자에 대해 알 필요가 없어 마이크로서비스 간 결합도가 감소한다.

2. 오류 격리<br/>
    소비자가 실패하더라도 발신자는 여전히 메시지를 보낼 수 있다. 소비자가 복구되면 메시지를 수신하여 처리한다. 서비스는 언제든 사용할 수 없게 되거나 새 버전으로 교체될 수 있다. 비동기 메시징은 이 과정에서 일시적 가동 중지 시간을 처리할 수 있다. 반면에 동기 통신은 이 사이에 무조건 실패한다.

3. 응답<br/>
    업스트림 서비스는 다운스트림 서비스를 기다리지 않으면 더 빨리 응답할 수 있다. 서비스 종속성 체인(서비스 A가 B를 호출하고 B가 C를 호출하는)이 있는 경우 동기 호출에 대기하는 시간만큼 응답시간이 늘어날 수 있다.

4. 부하 평준화<br/>
    메시지 수신자는 자체 속도에 맞게 메시지를 처리할 수 있다.

#### 비동기 통신의 단점

1. 메시징 인프라와 결합
    특정 메시징 인프라를 사용하면 해당 인프라와 밀접하게 결합될 수 있다. 그렇게 되면 나중에 다른 메시징 인프라로 전환하는 것이 어렵다.

2. 대기 시간
    메시지 큐에 메시지가 쌓이면 쌓일수록 앞선 메시지를 처리하느라 늦게 들어온 메시지는 엔드투엔드 대기 시간이 그만큼 길어질 수 있다.

3. 비용
    처리량이 높으면 메시징 인프라에 대한 금전적인 비용이 늘어날 수 있다.

4. 복잡성
    비동기 메시징 처리는 구현이 매우 어렵다. 메시지를 중복 처리될 수 있는 여지에 대해 어떻게 문제점을 해결할 것인가도 고민이다. 또한 비동기 메시징을 사용하여 요청-응답 의미 체계를 구현하는 것도 어렵다. 응답을 보내려면 다른 큐가 필요하고 요청과 응답 메시지를 상호 연결하는 방법이 필요하다.

#### 용어 정리

1. RESTful API : RESTful API는 HTTP 프로토콜을 기반으로 하며, 자원을 URI로 표현하고 HTTP 메서드(GET, POST, PUT, DELETE 등)를 사용하여 자원에 대한 액세스를 정의합니다.
2. gRPC : Google에서 개발한 고성능 RPC(Remote Procedure Call) 프레임워크로, Protocol Buffers를 사용하여 직렬화하고 HTTP/2를 기반으로 하는 효율적인 통신을 제공합니다. gRPC는 강력한 타입 시스템을 갖추고 있으며, 다양한 언어로 지원됩니다.
3. Message Queue: 서비스 간 비동기 통신을 위해 메시지 큐 시스템을 사용할 수 있습니다. 대표적으로 RabbitMQ, Apache Kafka, ActiveMQ 등이 있으며, 이를 통해 이벤트 기반 아키텍처를 구현할 수 있습니다.

## 오류 및 해결

1. Gateway Service 오류

    Gateway Service 구성 중, `https://start.spring.io/`에서 의존성으로 등록했던 `Spring Cloud Routing:Gateway`는 `org.springframework.cloud:spring-cloud-starter-gateway-mvc`로 만들어짐. 하지만, 이 의존성은 제대로 작동하지 않았고 mvc를 제거한 `org.springframework.cloud:spring-cloud-starter-gateway`으로 진행하면서 해결됨.

    [Velog:Spring에서 Eureka Gateway 작동 안될 때](https://velog.io/@westreed/Spring%EC%97%90%EC%84%9C-Eureka-Gateway-%EC%9E%91%EB%8F%99-%EC%95%88%EB%90%A0-%EB%95%8C)

2. Docker Compose로 컨테이너화했을 때 서비스 등록 안됨

    Docker Network 안에 묶인 형태이므로, Docker 내부에서 사용하는 Eureka Server 주소를 건내줘야 한다.
    보통은 docker-compose.yml의 서비스 이름으로 네트워크 위치를 알려줄 수 있음.

3. 인증/인가 처리에서 중복코드는 어떻게 해결해야 하는가

    API Gateway에서 Header에 포함된 토큰의 유효성을 검사하고 Payload에 담긴 사용자정보를 토대로 Request 요청을 다시 재구성 한 후,
    다시 목적지로 보냄. 그런데 Gateway로부터 전달 받는 모든 마이크로 서비스에서는 접근 제어를 위해, Security를 사용하고 있고
    사용자정보가 있을 경우 인증된 사용자로 처리해야 하는데 이 과정에서 중복코드가 발생함.
    [Spring MSA로 구성된 다른 서비스 참고해보기](https://github.com/bithumb-talk)

    현재 이 프로젝트는 MVC 패턴에서 View는 React.js로 제작할 예정으로, 인가 과정에서의 페이지에 대한 접근 제어는 서버측에서 할 필요가 없다!
    즉, Spring에서는 공개된 API에 대한 접근 제어만 잘 처리하면 되는 것! 위의 레포를 참고한 결과, URL의 경로상에 `auth`가 있으면 Gateway에서
    JWT에 포함된 데이터를 기반으로 처리했음.

4. 권한이 없을 때 OAuth2 로그인 페이지로 이동하는 문제 발생

    토큰이 없거나 토큰은 있지만 권한이 없을 경우, 401이나 403 에러가 발생해야 하지만, OAuth2 로그인 페이지로 리다이렉트 되는 문제 발생.
    이게 문제되는 이유는 View를 Spring에서 관리하지 않기 때문에 Redirect 자체가 의미가 없음(?).
    
    SecurityConfig에서 이와 관련된 설정이 있음.
    AccessDeniedHandler -> 권한 없음 (403 FORBIDDEN)
    AuthenticationEntry -> 로그인 정보 없음 (401 UNAUTHORIZED)

5. Config Server를 따로 만들면 Local Test에서 Config 정보가 없어서 문제가 됨.

    Local Test를 고려해서 Local 환경에만 있는 환경파일을 따로 가지고 있거나 Config Serve를 항상 따로 실행시켜두고 불러올 수 있게 해야할 것 같음.

## 다음 목표

1. Docker로 묶어서 서비스를 실행하기. (✔️)
2. 간단하게 서비스를 구현하여 마이크로 서비스끼리 내부통신 해보기. (✔️)
3. Spring에서 FastAPI 쪽에 요청해서 가져오기. (✔️)
4. FastAPI에서 다른 서비스에게 요청할 때 Spring의 Feign Client처럼 간편하게 할 수 없는지 찾아보기. (❌)
    * 4-1. 대신 간단하게 사용할 수 있는 클래스 구현. (✔️)
5. Spring Feign Client 사용해보기. (✔️)
6. OAuth를 활용한 인증/인가 서비스 공부.
    * 6-1. Spring Security 학습. (✔️)
    * 6-2. React + Security + JWT + OAuth2.0 학습. [참고자료](https://github.com/chunghee-hwang/spring-security-and-react-integration/blob/master/README.md) (✔️)
    * 6-3. MSA에서 인증/인가를 어떻게 처리하는지 학습. [참고자료](https://medium.com/spoontech/%EB%A7%88%EC%9D%B4%ED%81%AC%EB%A1%9C%EC%84%9C%EB%B9%84%EC%8A%A4-%EA%B5%AC%EC%A1%B0-msa-%EC%9D%98-%EC%9D%B8%EC%A6%9D-%EB%B0%8F-%EC%9D%B8%EA%B0%80-authorization-authentication-a595179ab88e) (✔️)
7. 인증/인가 서비스를 통한 인가 관리.
8. 실제 서비스에 적용하기.
