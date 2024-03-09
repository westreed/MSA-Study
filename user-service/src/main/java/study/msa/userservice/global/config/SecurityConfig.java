package study.msa.userservice.global.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import study.msa.userservice.domain.repository.UserRepository;
import study.msa.userservice.global.jwt.service.JwtService;
import study.msa.userservice.global.login.handler.CustomAuthenticationEntryPoint;
import study.msa.userservice.global.login.filter.AuthenticationProcessingFilter;
import study.msa.userservice.global.login.filter.CustomJsonUsernamePasswordAuthenticationFilter;
import study.msa.userservice.global.login.handler.CustomAccessDeniedHandler;
import study.msa.userservice.global.login.handler.LoginFailureHandler;
import study.msa.userservice.global.login.handler.LoginSuccessHandler;
import study.msa.userservice.global.login.service.PrincipalDetailsService;
import study.msa.userservice.global.oauth2.handler.OAuth2LoginFailureHandler;
import study.msa.userservice.global.oauth2.handler.OAuth2LoginSuccessHandler;
import study.msa.userservice.global.oauth2.service.CustomOAuth2UserService;
import study.msa.userservice.global.response.ErrorResponse;
import study.msa.userservice.global.response.SuccessResponse;

@Configuration
@EnableWebSecurity // Spring Security Filter(SecurityConfig를 의미함)가 Spring Filter Chain에 등록됩니다.
@RequiredArgsConstructor
public class SecurityConfig {

    private final ObjectMapper objectMapper;
    private final SuccessResponse successResponse;
    private final ErrorResponse errorResponse;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PrincipalDetailsService principalDetailsService;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
    private final OAuth2LoginFailureHandler oAuth2LoginFailureHandler;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable);
        http.httpBasic(AbstractHttpConfigurer::disable);
        http.headers(header -> header.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable));
        http
            .sessionManagement(manager -> manager.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/user/**").authenticated()
                .requestMatchers("/manager/**").hasAnyRole("ADMIN", "MANAGER")
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().permitAll()
        );
        // 승인되지 않은 접근(401)이나 접근 권한이 없는 경우(403)
        http.exceptionHandling(handler -> handler
                .accessDeniedHandler(customAccessDeniedHandler)
                .authenticationEntryPoint(customAuthenticationEntryPoint)
        );
        http.formLogin(AbstractHttpConfigurer::disable);
//        http.formLogin(form -> form
//                .loginPage("/loginForm")
//                .loginProcessingUrl("/login") // login 주소가 호출이 되면 시큐리티가 낚아채서 대신 로그인을 진행합니다.
//                .defaultSuccessUrl("/")
//        );
        http.oauth2Login(oauth2 -> oauth2
                .failureUrl("/")
                //.loginPage("/loginForm")
                .successHandler(oAuth2LoginSuccessHandler)
                .failureHandler(oAuth2LoginFailureHandler)
                .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
//                .defaultSuccessUrl("/")
        );
        // 순서 : LogoutFilter -> CustomJsonUsernamePasswordAuthenticationFilter
//        http.addFilterAfter(customJsonUsernamePasswordAuthenticationFilter(), LogoutFilter.class);
        // 순서 : LogoutFilter -> AuthenticationProcessingFilter -> CustomJsonUsernamePasswordAuthenticationFilter
        http.addFilterAfter(customJsonUsernamePasswordAuthenticationFilter(), LogoutFilter.class);
        http.addFilterBefore(authenticationProcessingFilter(), CustomJsonUsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public LoginSuccessHandler loginSuccessHandler() {
        return new LoginSuccessHandler(successResponse, jwtService, userRepository);
    }

    @Bean
    public LoginFailureHandler loginFailureHandler() {
        return new LoginFailureHandler(errorResponse);
    }


    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(bCryptPasswordEncoder());
        provider.setUserDetailsService(principalDetailsService);
        return new ProviderManager(provider);
    }

    @Bean
    public CustomJsonUsernamePasswordAuthenticationFilter customJsonUsernamePasswordAuthenticationFilter() {
        CustomJsonUsernamePasswordAuthenticationFilter authenticationFilter = new CustomJsonUsernamePasswordAuthenticationFilter(objectMapper);
        authenticationFilter.setAuthenticationManager(authenticationManager());
        authenticationFilter.setAuthenticationSuccessHandler(loginSuccessHandler());
        authenticationFilter.setAuthenticationFailureHandler(loginFailureHandler());
        return authenticationFilter;
    }

    @Bean
    public AuthenticationProcessingFilter authenticationProcessingFilter() {
        return new AuthenticationProcessingFilter(objectMapper);
    }
}
