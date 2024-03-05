package study.msa.gatewayservice.filter;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import study.msa.gatewayservice.service.JwtService;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
public class GlobalFilter extends AbstractGatewayFilterFactory<Config> {
    private final String filterName = GlobalFilter.class.getName();
    @Autowired
    private JwtService jwtService;

    public GlobalFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            log.info("{} Message : {}", filterName, config.getBaseMessage());
            System.out.println("----------------------------------------------------------------------------");
            if(config.isPreLogger()) {
                log.info("{} Start : {}", filterName, exchange.getRequest());
            }

            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();

            if (request.getHeaders().containsKey("Authorization")) {
                List<String> rawToken = request.getHeaders().get("Authorization");
                String rawTokenString = Objects.requireNonNull(rawToken).getFirst();
                String token = resolveToken(rawTokenString);

                int responseValue = 0;
                try {
                    responseValue = jwtService.isTokenValid(token);
                } catch (Exception e) {
                    responseValue = 904;
                }

                if (responseValue == 902) {
                    return onErrorResponse(exchange, responseValue, "ID NOT MATCH IN GATEWAY");
                } else if (responseValue == 903) {
                    return onErrorResponse(exchange, responseValue, "EXPIRED TOKEN IN GATEWAY");
                } else if (responseValue == 904) {
                    return onErrorResponse(exchange, responseValue, "NOT VALID TOKEN IN GATEWAY");
                } else if (responseValue == 905) {
                    return onErrorResponse(exchange, responseValue, "NOT VALID TOKEN IN GATEWAY");
                }
            }

            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                if(config.isPostLogger()) {
                    log.info("{} End : {}", filterName, exchange.getResponse());
                    log.info("{}", exchange.getResponse().getHeaders());
                }
            }));
        };
    }

    private String resolveToken(String rawToken) {
        if (StringUtils.hasText(rawToken) && rawToken.startsWith("Bearer ")) {
            return rawToken.substring(7);
        }
        return null;
    }

    public Mono<Void> onErrorResponse(ServerWebExchange exchange, int errorCode, String errorMessage) {
        String message = "{\"Status\": " + "\"FAIL\"" + ", " + "\"message\": \"" + errorMessage + "\", "+ "\"code\": \"" + errorCode +"\"}";
        byte[] bytes = message.getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
        return exchange.getResponse().writeWith(Flux.just(buffer));
    }
}
