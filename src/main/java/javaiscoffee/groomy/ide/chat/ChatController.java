package javaiscoffee.groomy.ide.chat;

import javaiscoffee.groomy.ide.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat")
public class ChatController {
    private final ChatService chatService;
    /**
     * 프로젝트 채팅 로그 받아오기
     * 요구 데이터 : 프로젝트 ID, paging index, 토큰
     * 반환 데이터 : ChatLogResponseDto List
     */
    @GetMapping("/project/logs/{projectId}/{paging}")
    public ResponseEntity<?> getProjectChatLogs(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable(name = "projectId") Long projectId, @PathVariable(name = "paging") int paging) {
        Long memberId = userDetails.getMemberId();
        return ResponseEntity.ok(chatService.getChatLogs(memberId, projectId, paging, 100));
    }

    @PostMapping("/project/write/{projectId}")
    public ResponseEntity<?> writeProjectChat(@PathVariable(name = "projectId") Long projectId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long memberId = userDetails.getMemberId();
        return ResponseEntity.ok(chatService.writeChat(projectId,memberId));
    }

}
