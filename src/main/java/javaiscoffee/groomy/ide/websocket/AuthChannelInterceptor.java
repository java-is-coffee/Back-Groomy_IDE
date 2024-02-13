package javaiscoffee.groomy.ide.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import javaiscoffee.groomy.ide.project.ProjectService;
import javaiscoffee.groomy.ide.response.ResponseStatus;
import javaiscoffee.groomy.ide.response.Status;
import javaiscoffee.groomy.ide.security.BaseException;
import javaiscoffee.groomy.ide.security.CustomUserDetails;
import javaiscoffee.groomy.ide.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AuthChannelInterceptor implements ChannelInterceptor {
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public AuthChannelInterceptor(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String authToken = accessor.getFirstNativeHeader("Authorization");
            log.info("인증 토큰 = {}",authToken);
            if (authToken != null && authToken.startsWith("Bearer ")) {
                if (jwtTokenProvider.validateToken(authToken)) {
                    String token = authToken.substring(7);
                    log.info("인증 토큰 = {}",token);
                    Authentication auth = jwtTokenProvider.getAuthentication(token);
                    SecurityContextHolder.getContext().setAuthentication(auth);
                } else {
                    // 토큰 검증 실패 처리 로직
                    log.error("웹소켓 토큰 검증 실패: {}", authToken);
                    // 인증 실패 시 처리 로직 필요 (예: 연결 종료 또는 에러 메시지 전송)
                    // 여기서는 에러 메시지를 클라이언트에게 보내는 것이 아니라, 단순히 로그를 남기고 메시지를 처리하지 않습니다.
                    throw new BaseException("웹소켓 연결 시 토큰 검증에 실패했습니다.");
                }
            } else {
                log.error("웹소켓 연결 시 인증 헤더가 누락됨");
                throw new BaseException("웹소켓 연결 시 인증 헤더가 누락되었습니다.");
            }
        }
        return message;
    }
}
