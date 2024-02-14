package javaiscoffee.groomy.ide.websocket;


import javaiscoffee.groomy.ide.project.ProjectService;
import javaiscoffee.groomy.ide.security.BaseException;
import javaiscoffee.groomy.ide.security.CustomUserDetails;
import javaiscoffee.groomy.ide.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * 웹소켓 전용 검증 처리하는 인터셉터
 * 연결할 때 토큰 유효성 검사 => 성공하면 세션 속성에 memberId 저장
 * 구독할 때 해당 프로젝트 참가 여부 검사 => 성공하면 세션 속성에 memberId 저장
 */

@Slf4j
@Component
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
public class AuthChannelInterceptor implements ChannelInterceptor {
    private final JwtTokenProvider jwtTokenProvider;
    private final ProjectService projectService;
    private final SubscriptionManager subscriptionManager;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        //연결 시 토큰값 확인
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String authToken = accessor.getFirstNativeHeader("Authorization");
            log.debug("웹소켓 인터셉터 인증 토큰 = {}",authToken);
            if (authToken != null) {
                if (jwtTokenProvider.validateToken(authToken)) {
                    log.debug("인증 토큰 검증 성공 = {}",authToken);
                    Authentication auth = jwtTokenProvider.getAuthentication(authToken);
                    SecurityContextHolder.getContext().setAuthentication(auth);

                    // Authentication 객체로부터 UserDetails를 추출해서 memberId를 저장
                    if (auth.getPrincipal() instanceof CustomUserDetails) {
                        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
                        Long memberId = userDetails.getMemberId();
                        // 여기서 세션 속성에 memberId 저장 필요
                        accessor.getSessionAttributes().put("memberId", memberId.toString());
                    }
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
        //구독 시 프로젝트에 참가하고 있는지 확인
        else if(StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
            log.info("웹소켓 구독 요청 시작");
            // 세션 속성에서 memberId 추출
            String memberIdStr = (String) accessor.getSessionAttributes().get("memberId");
            Long memberId = Long.parseLong(memberIdStr);
            log.info("웹소켓 구독 정보 =>> 멤버ID = {}", memberId);
            // destination에서 프로젝트 ID 추출
            String destination = accessor.getDestination();
            Long projectId = extractProjectIdFromDestination(destination);
            log.info("웹소켓 구독 정보 =>> 프로젝트ID = {}",projectId);

            //프로젝트 참여 여부 확인
            if(!projectService.isParticipated(memberId, projectId)) {
                throw new BaseException("프로젝트 구독 권한이 없습니다.");
            }

            // SubscriptionManager를 사용하여 구독 처리
            subscriptionManager.subscribe(memberId, projectId);

            //세션 속성에 프로젝트 ID 저장
            accessor.getSessionAttributes().put("projectId",projectId.toString());
        }
        return message;
    }

    /**
     * 웹소켓 destination 문자열에서 프로젝트ID를 추출해서 반환
     * 박상현 2024-02-14
     */
    private Long extractProjectIdFromDestination(String destination) {
        try {
            // destination 예시: /app/project-chat/123/send
            String[] parts = destination.split("/");
            // 프로젝트 ID가 항상 동일한 위치에 있다고 가정
            return Long.parseLong(parts[2]);
        } catch (Exception e) {
            log.error("Destination에서 프로젝트 ID 추출 실패: {}", destination, e);
            throw new BaseException("유효하지 않은 Destination 형식입니다.");
        }
    }
}
