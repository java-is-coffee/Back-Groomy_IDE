package javaiscoffee.groomy.ide.websocket;
import javaiscoffee.groomy.ide.codeeditor.CodeEditorResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

/**
 * 웹소켓 연결이 끊어졌을 때 해당 프로젝트를 구독중인 다른 멤버들에게 구독 해제 메세지를 전달
 */
@RequiredArgsConstructor
public class WebSocketEventListener {
    private final SimpMessagingTemplate messagingTemplate;
    private final SubscriptionManager subscriptionManager;

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        // 세션 속성에서 memberId 가져오기
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String memberIdStr = (String) headerAccessor.getSessionAttributes().get("memberId");
        String projectIdStr = (String) headerAccessor.getSessionAttributes().get("projectId");
        if (memberIdStr == null || projectIdStr == null) {
            return; // memberId가 없는 경우는 처리하지 않음
        }
        Long memberId = Long.parseLong(memberIdStr);
        Long projectId = Long.parseLong(projectIdStr);
        String action = "disconnect";
        // 구독자들에게 메시지 전송
        CodeEditorResponseDto responseDto = new CodeEditorResponseDto();
        responseDto.setMemberId(memberId);
        responseDto.setAction(action);

        // 사용자 연결 해제 시 SubscriptionManager에서 구독 정보 제거
        subscriptionManager.disconnect(memberId);

        // 구독자들에게 사용자 연결 해제 메시지 전송
        messagingTemplate.convertAndSend(String.format("/projectws/%s/code", projectId), responseDto);
    }
}
