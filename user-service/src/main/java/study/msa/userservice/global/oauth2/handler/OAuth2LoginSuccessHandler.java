package study.msa.userservice.global.oauth2.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import study.msa.userservice.domain.model.User;
import study.msa.userservice.domain.repository.UserRepository;
import study.msa.userservice.global.jwt.service.JwtService;
import study.msa.userservice.global.login.dto.PrincipalDetails;
import study.msa.userservice.global.response.SuccessResponse;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final SuccessResponse successResponse;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    @Override
    @Transactional
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("OAuth2 Login 성공!");
        try {
            PrincipalDetails oAuth2User = (PrincipalDetails) authentication.getPrincipal();
            loginSuccess(response, oAuth2User); // 로그인에 성공한 경우 access, refresh 토큰 생성
        } catch (Exception e) {
            throw e;
        }

        Integer status = HttpServletResponse.SC_OK;
        String message = "소셜 로그인에 성공하였습니다.";
        successResponse.setResponse(response, status, message, request.getRequestURI());
    }

    // TODO : 소셜 로그인 시에도 무조건 토큰 생성하지 말고 JWT 인증 필터처럼 RefreshToken 유/무에 따라 다르게 처리해보기
    private void loginSuccess(HttpServletResponse response, PrincipalDetails oAuth2User) throws IOException {
        String username = oAuth2User.getUsername();
        Optional<User> optUser = userRepository.findByUsername(username);

        if(optUser.isEmpty()) {
            log.error("로그인한 OAuth 유저의 정보가 없습니다! username : {}", username);
            return;
        }
        User user = optUser.get();
        String accessToken = jwtService.createAccessToken(user);
        String refreshToken = jwtService.createRefreshToken();
        response.addHeader(jwtService.getAccessHeader(), jwtService.getBEARER() + accessToken);
        response.addHeader(jwtService.getRefreshHeader(), jwtService.getBEARER() + refreshToken);

        jwtService.sendAccessAndRefreshToken(response, accessToken, refreshToken);
        user.updateRefreshToken(refreshToken);
        userRepository.saveAndFlush(user);

        response.sendRedirect("/");
    }
}
