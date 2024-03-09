package study.msa.userservice.global.login.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.transaction.annotation.Transactional;
import study.msa.userservice.domain.model.Role;
import study.msa.userservice.domain.model.User;
import study.msa.userservice.domain.repository.UserRepository;
import study.msa.userservice.global.jwt.service.JwtService;
import study.msa.userservice.global.response.SuccessResponse;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final SuccessResponse successResponse;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        String username = extractUsername(authentication); // 인증 정보에서 Username(email) 추출
        Optional<User> optUser = userRepository.findByUsername(username);

        if(optUser.isEmpty()) {
            log.error("로그인한 유저의 정보가 없습니다! username : {}", username);
            return;
        }
        User user = optUser.get();
        String accessToken = jwtService.createAccessToken(user); // JwtService의 createAccessToken을 사용하여 AccessToken 발급
        String refreshToken = jwtService.createRefreshToken(); // JwtService의 createRefreshToken을 사용하여 RefreshToken 발급

        jwtService.sendAccessAndRefreshToken(response, accessToken, refreshToken); // 응답 헤더에 AccessToken, RefreshToken 실어서 응답

        user.updateRefreshToken(refreshToken);
        userRepository.saveAndFlush(user);

        log.info("로그인에 성공하였습니다. Username : {}", username);
        log.info("로그인에 성공하였습니다. AccessToken : {}", accessToken);
        log.info("로그인에 성공하였습니다. RefreshToken : {}", refreshToken);
        Integer status = HttpServletResponse.SC_OK;
        String message = "로그인에 성공하였습니다.";
        successResponse.setResponse(response, status, message, request.getRequestURI());

    }

    private String extractUsername(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return userDetails.getUsername();
    }
}
