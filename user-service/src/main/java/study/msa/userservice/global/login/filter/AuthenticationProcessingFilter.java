package study.msa.userservice.global.login.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class AuthenticationProcessingFilter extends OncePerRequestFilter {

    private final String BEARER = "Bearer ";
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("AuthenticationProcessingFilter 호출됨");
        if(Optional.ofNullable(request.getHeader("Authorization")).isEmpty()) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = extractToken(request)
                .orElse(null);

        log.info("token : {}", token);


        if (token == null) {
            throw new BadRequestException("잘못된 요청입니다.");
        }

        String encPayload = token.split("\\.")[1];

        byte[] decodedPayloads = Base64.getUrlDecoder().decode(encPayload);
        log.info("decodedPayloads : {}", decodedPayloads);

        String decodedPayload = new String(decodedPayloads);
        log.info("decodedPayload : {}", decodedPayload);

        saveAuthentication(decodedPayload);
        filterChain.doFilter(request, response);
    }

    private Optional<String> extractToken(HttpServletRequest request) {
        log.info("extractToken() 호출");
        return Optional.ofNullable(request.getHeader("Authorization"))
                .filter(token -> token.startsWith(BEARER))
                .map(token -> token.replace(BEARER, ""));
    }

    private void saveAuthentication(String decodedPayload) throws IOException {
        log.info("saveAuthentication() 호출");
        Map<String, String> payloadMap = objectMapper.readValue(decodedPayload, Map.class);
        log.info("payloadMap : {}", payloadMap);
        UserDetails userDetails = User.builder()
                .username(payloadMap.get("name"))
                .password("")
                .roles(payloadMap.get("role"))
                .build();

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
