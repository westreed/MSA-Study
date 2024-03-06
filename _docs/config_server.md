# Spring Cloud Config

Spring Cloud Config는 중앙 집중식 마이크로서비스 구성을 지원한다.

<img src="https://github.com/westreed/MSA-Study/blob/main/_src/config_server.png"/>

- Spring Cloud Config Server : 버전 관리 레포지토리로 백업된 중앙 집중식 구성 노출을 지원한다.
- Spring Cloud Config Client : 애플리케이션이 Spring Cloud Config Server에 연결하도록 지원한다.

# Spring Cloud Config의 장단점

Spring Cloud Config 는 여러 서비스 들의 설정 파일을 외부로 분리해 하나의 중앙 설정 저장소 처럼 관리 할 수 있도록 해주며 특정 설정 값이 변경시 각각의 서비스를 재기동 할 필요없이 적용이 가능하도록 도와준다.

- 여러 서버의 설정 파일을 중앙 서버에서 관리할 수 있다. 
- 서버를 재배포 하지 않고 설정 파일의 변경사항을 반영할 수 있다.

하지만 이것이 모든 문제를 해결해주지는 않는다. Spring Cloud Config를 이용하면 다음의 문제들을 겪을 수 있으므로 주의해야 한다.

- Git 서버 또는 설정 서버에 의한 장애가 전파될 수 있다.
- 우선 순위에 의해 설정 정보가 덮어씌워질 수 있다.

이미 서비스가 실행중이라면 메모리에서 설정 정보를 관리해 문제가 없지만, 서비스가 시작될 때 만약 GIT 서버나 설정 서버에 문제가 있다면 서비스들까지 문제가 전파될 수 있다. 또한 설정 서버에 의해 장애 지점이 될 수 있으므로 설정 정보를 관리하기 위한 별도의 서비스 운영이 필요할 수도 있다.

출처: https://mangkyu.tistory.com/253 [MangKyu's Diary:티스토리]

# GitHub Repository 연결

GitHub Repositroy에는 `서비스명-프로필.yml` 형식으로 설정파일을 생성합니다.

```bash
- microservice-local.yml
- microservice-dev.yml
```

안에는 확인을 위해서, `test.msg: "Hello World!"`라고 작성하겠습니다.

# Config Server

```gradle
dependencies {
    implementation 'org.springframework.cloud:spring-cloud-config-server'
    implementation 'org.springframework.boot:spring-boot-starter-actuator'
}
```

Config Server의 `build.gradle`에 의존성을 추가합니다. actuator는 편의를 위해 추가합니다.

```yml
server:
  port: 8888

spring:
  application:
    name: config-server

  cloud:
    config:
      server:
        git:
          uri: "https://github.com/..."
          username: username
          password: password
```
GitHub Repository는 Public이냐 Private이냐에 따라 설정이 달라집니다. Public이면 공개되어 있으므로, URI만 작성하면 되고 Private인 경우 Password 방식 혹은 SSH Key 인증 방식으로 Repository에 접근해야 합니다.

SSH Key 방식의 경우에는 아래와 같이 작성합니다.
```yml
spring:
  cloud:
    config:
      server:
        git:
          uri: git@github.com:username/repository.git
          ignoreLocalSshSettings: true
          private-key: |
            -----BEGIN RSA PRIVATE KEY-----
            ....
            -----END RSA PRIVATE KEY-----
            # RSA 키를 생성해서 Repository에 공개키를 등록하고, 설정에 비공개키를 넣습니다.
```

```java
@SpringBootApplication
@EnableConfigServer
public class ConfigServerApplication {

  public static void main(String[] args) {
    SpringApplication.run(ConfigServerApplication.class, args);
  }

}
```

`@EnableConfigServer` 어노테이션을 추가하면 별다른 설정없이 Config Server가 설정됩니다.

URL 형식은 `/{appliation-name}/{profile}/{label}`입니다. 위에서 등록한 microservice-local.yml을 확인해보기 위해서 `http://localhost:8888/microservice/local`을 호출합니다.

```json
{
  "name": "microservice",
  "profiles": [
    "local"
  ],
  "label": null,
  "version": "c03eecc5d8eabefc4b2a8f085789f42bd5317366",
  "state": null,
  "propertySources": [
    {
      "name": "https://github.com/.../{repositroy}/microservice-local.yml",
      "source": {
        "test.msg": "Hello World!"
      }
    }
  ]
}
```

응답을 확인하면 해당 yml을 잘 가져온 것을 확인할 수 있습니다.

# Config Client

이제, 설정파일을 제공받고자 하는 마이크로서비스에 설정을 추가해야 합니다.

```gradle
dependencies {
    implementation 'org.springframework.cloud:spring-cloud-config-client'
    implementation 'org.springframework.boot:spring-boot-starter-actuator'
}
```

```yml
spring:
  config:
    import:
      - optional:configserver:{Config서버주소}
  
  cloud:
    config:
      name: {name}
      profile: {profile}, {profile2}, ...
      # github repo -> {name}-{profile}.yml
```