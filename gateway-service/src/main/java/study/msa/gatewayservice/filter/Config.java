package study.msa.gatewayservice.filter;

import lombok.Getter;

@Getter
public class Config {
    private final String baseMessage;
    private final boolean preLogger;
    private final boolean postLogger;

    /*
     환경설정 파일(application.yml)에서 설정할 수 있다.
     baseMessage : 로그로 남길 메시지.
     preLogger : True인 경우, 해당 필터가 작동하기 전에 로그를 남긴다. (startpoint)
     postLogger : True인 경우, 해당 필터가 작동한 이후 로그를 남긴다. (endpoint)
     */
    public Config(String baseMessage, boolean preLogger, boolean postLogger) {
        this.baseMessage = baseMessage;
        this.preLogger = preLogger;
        this.postLogger = postLogger;
    }
}