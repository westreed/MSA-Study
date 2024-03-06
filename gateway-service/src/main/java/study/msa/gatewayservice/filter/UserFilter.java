package study.msa.gatewayservice.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import study.msa.gatewayservice.common.ErrorResponse;

@Slf4j
@Component
public class UserFilter extends AbstractGatewayFilterFactory<Config> {
    private final String filterName = UserFilter.class.getName();
    private final String[] noAuthPath = {"login", "join"};

    public UserFilter() {
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

            if (authFlag) {
                if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                    return new ErrorResponse(901, "No Authorization Header").setResponse(exchange);
                }
            }

            if(config.isPostLogger()) {
                log.info("{} End : {}", filterName, exchange.getResponse());
            }

            if (!authFlag) {
                return chain.filter(exchange);
            }

            return chain.filter(exchange);
        };
    }
}
