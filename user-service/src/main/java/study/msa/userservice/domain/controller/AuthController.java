package study.msa.userservice.domain.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import study.msa.userservice.domain.dto.UserDto;
import study.msa.userservice.domain.model.User;
import study.msa.userservice.domain.repository.UserRepository;
import study.msa.userservice.global.response.SuccessResponse;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@RestController
public class AuthController {

    private final UserRepository userRepository;
    private final SuccessResponse successResponse;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @PostMapping("/join")
    public void join(HttpServletRequest request, HttpServletResponse response, @Valid @RequestBody UserDto userDto) throws IOException {
        log.info("POST /join UserDto : {}", userDto);

        Optional<User> userEntity = userRepository.findByUsername(userDto.getUsername());
        if (userEntity.isPresent()) {
            throw new BadRequestException("이미 가입된 유저입니다.");
        }

        User user = User.builder()
                .username(userDto.getUsername())
                .password(userDto.getPassword())
                .email(userDto.getEmail())
                .build();
        user.authorizeUser();
        user.passwordEncode(bCryptPasswordEncoder);
        userRepository.save(user);
        Integer status = HttpServletResponse.SC_OK;
        String message = "가입 성공";
        successResponse.setResponse(response, status, message, request.getRequestURI());
    }
}
