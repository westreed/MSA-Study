# MSA (MicroService Architecture) 공부용 레포

## 서비스 구성

* Discovery Service : Service Discovery를 위한 서비스. 일종의 전화번호부와 같은 역할로 각각의 마이크로 서비스가 어느 위치에 있는지를 관리한다.
* Gateway Service : 모든 클라이언트의 요청을 받아서 설정해놓은 라우팅으로 보내는 서비스.
* User Service : 마이크로 서비스, 간단한 테스트를 위해 만듬.
* LLM Service : 마이크로 서비스, Python Flask와 연동을 테스트해보기 위해 만듬.