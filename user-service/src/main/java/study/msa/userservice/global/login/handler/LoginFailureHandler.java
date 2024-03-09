package study.msa.userservice.global.login.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import study.msa.userservice.global.response.ErrorResponse;

import java.io.IOException;

/**
 * JWT 로그인 실패 시 처리하는 핸들러
 * SimpleUrlAuthenticationFailureHandler를 상속받아서 구현
 */
@Slf4j
@RequiredArgsConstructor
public class LoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private final ErrorResponse errorResponse;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {

        Integer status = HttpServletResponse.SC_BAD_REQUEST;
        String error = "로그인 실패! 이메일이나 비밀번호를 확인해주세요.";
        String path = request.getRequestURI();
        errorResponse.setResponse(response, status, error, path);
        log.info("로그인에 실패했습니다. 메시지 : {}", exception.getMessage());
    }
}
