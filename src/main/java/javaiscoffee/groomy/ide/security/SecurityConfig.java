package javaiscoffee.groomy.ide.security;


import javaiscoffee.groomy.ide.exception.CustomAuthenticationEntryPoint;
import javaiscoffee.groomy.ide.login.oauth.handler.OAuthLoginFailureHandler;
import javaiscoffee.groomy.ide.login.oauth.handler.OAuthLoginSuccessHandler;
import javaiscoffee.groomy.ide.login.oauth.service.CustomOAuthUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import static javax.management.Query.and;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {
    private final JwtTokenProvider jwtTokenProvider;
    private final OAuthLoginSuccessHandler oAuthLoginSuccessHandler;
    private final OAuthLoginFailureHandler oAuthLoginFailureHandler;
    private final CustomOAuthUserService customOAuthUserService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors()
                .and()
                .httpBasic().disable()
                //rest api이므로 basic auth 및 csrf 보안을 사용하지 않는다는 설정
                .csrf((csrf) -> csrf.disable())
                //JWT를 사용하기 때문에 세션을 사용하지 않는다는 설정
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                .and()
                .authorizeRequests()
                //로그인과 회원가입은 모든 요청을 허가
                .requestMatchers(new AntPathRequestMatcher("/api/member/login")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/api/member/login/reset-password")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/api/member/register")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/api/member/register/email-check")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/api/member/refresh")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/ws/**")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/YJS/**")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/auth/**")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/login/oauth2/**")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/api/email/**")).permitAll()
                //그 외 나머지 요청은 전부 인증이 필요
                .anyRequest().authenticated()
                .and()
                .oauth2Login()
                .successHandler(oAuthLoginSuccessHandler) // 동의하고 계속하기를 눌렀을 때 Handler 설정
                .failureHandler(oAuthLoginFailureHandler) // 소셜 로그인 실패 시 핸들러 설정
                .userInfoEndpoint().userService(customOAuthUserService); // customUserService 설정

                // /error 엔트리 포인트 진입했을 경우 에러 응답을 반환하도록 커스텀 핸들러 추가
                //JWT 인증을 위하여 직접 구현한 필터를 UsernamePasswordAuthenticationFilter 전에 실행하겠다는 설정
                http.exceptionHandling()
                        .authenticationEntryPoint(new CustomAuthenticationEntryPoint())
                        .and()
                        .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOriginPattern("*"); // 모든 출처 허용
        configuration.addAllowedMethod("*"); // 모든 HTTP 메서드 허용
        configuration.addAllowedHeader("*"); // 모든 헤더 허용
        configuration.setAllowCredentials(true); // 쿠키 허용, but allowedOriginPattern is used to allow all origins

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // 모든 경로에 대해 위 설정 적용
        return source;
    }
}
