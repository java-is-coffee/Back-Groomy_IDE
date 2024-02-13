package javaiscoffee.groomy.ide.websocket;

import javaiscoffee.groomy.ide.chat.ChatMessageDto;
import javaiscoffee.groomy.ide.chat.ChatMessageRequestDto;
import javaiscoffee.groomy.ide.chat.ChatService;
import javaiscoffee.groomy.ide.codeeditor.CodeEditorResponseDto;
import javaiscoffee.groomy.ide.project.ProjectService;
import javaiscoffee.groomy.ide.response.ResponseStatus;
import javaiscoffee.groomy.ide.security.BaseException;
import javaiscoffee.groomy.ide.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class WebSocketController {
    private final ChatService chatService;
    private final ProjectService projectService;

    /**
     * 프로젝트 채팅 담당 코드
     * 클라이언트가 소켓 정보에 /ws/{projectId}를 넣는다.
     * 클라이언트가 /projectws/{projectId}/messages 주소를 구독한다.
     * 클라이언트 측에서 /app/projectchat/{projectId}/send 주소로 ChatMessageDto를 보낸다.
     * 백엔드에서 MessageMapping 어노테이션으로 위 주소를 지정해서
     * 해당 엔드포인트로 들어오는 메시지를  /projectws/{projectId}/messages 주소로 처리한다.
     * /app은 config에서 자동 처리
     */


    @MessageMapping("/project-chat/{projectId}/send")
    @SendTo("/projectws/{projectId}/messages")
    public ChatMessageDto sendProjectMessages(@DestinationVariable(value="projectId") Long projectId, ChatMessageRequestDto requestDto) {
        log.info("받은 메시지 로그 = {}",requestDto);
        ChatMessageDto chatMessageDto = new ChatMessageDto();
        return chatService.sendProjectChat(requestDto.getData().getMemberId(), projectId, requestDto);
    }

    @MessageMapping("/project-code/{projectId}/send")
    @SendTo("/projectws/{projectId}/code")
    public CodeEditorResponseDto sendCodeEditor(@DestinationVariable(value="projectId") Long projectId, ChatMessageRequestDto requestDto) {
        if (!projectService.isParticipated(requestDto.getData().getMemberId(), projectId)) {
            log.error("참여하고 있지 않은 프로젝트입니다. = {}",requestDto);
            throw new BaseException(ResponseStatus.UNAUTHORIZED.getMessage());
        }
        CodeEditorResponseDto responseDto = new CodeEditorResponseDto();
        BeanUtils.copyProperties(requestDto.getData(),responseDto);
        return responseDto;
    }
}
