package study.msa.gatewayservice.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

public class UserFilter extends AbstractGatewayFilterFactory<Config> {
    private final String filterName = UserFilter.class.getName();

    public UserFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();

            String pathString = request.getURI().getPath();
            String[] extractPath = pathString.split("/");
            // "user", "목적지", "<userid>", "auth"

            boolean authFlag = false;
            int authIndex = -1;
            for (int idx=0; idx<extractPath.length; idx++){
                if (extractPath[idx].equals("auth")) {
                    authFlag = true;
                    authIndex = idx;
                }
            }

            if (!authFlag) {
                return chain.filter(exchange);
            }

            return chain.filter(exchange);
        };
    }

    public Mono<Void> onErrorResponse(ServerWebExchange exchange, int errorCode, String errorMessage) {
        String message = "{\"Status\": " + "FAIL" + "," + "\"message\": \"" + errorMessage + "\", "+ "\"code\": " + errorCode +"}";
        byte[] bytes = message.getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
        return exchange.getResponse().writeWith(Flux.just(buffer));
    }
}
