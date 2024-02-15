package javaiscoffee.groomy.ide.websocket;

import javaiscoffee.groomy.ide.chat.ChatMessageDto;
import javaiscoffee.groomy.ide.chat.ChatMessageRequestDto;
import javaiscoffee.groomy.ide.chat.ChatService;
import javaiscoffee.groomy.ide.codeeditor.CodeEditorDto;
import javaiscoffee.groomy.ide.file.FileService;
import javaiscoffee.groomy.ide.file.FileWebsocketAction;
import javaiscoffee.groomy.ide.file.FileWebsocketRequestDto;
import javaiscoffee.groomy.ide.file.FileWebsocketResponseDto;
import javaiscoffee.groomy.ide.project.ProjectService;
import javaiscoffee.groomy.ide.response.ResponseStatus;
import javaiscoffee.groomy.ide.security.BaseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class WebSocketController {
    private final ChatService chatService;
    private final ProjectService projectService;
    private final FileService fileService;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * 프로젝트 채팅 담당 코드
     * 클라이언트가 소켓 정보에 /ws/{projectId}를 넣는다.
     * 클라이언트가 /projectws/{projectId}/messages 주소를 구독한다.
     * 클라이언트 측에서 /app/project-chat/{projectId}/send 주소로 ChatMessageDto를 보낸다.
     * 백엔드에서 MessageMapping 어노테이션으로 위 주소를 지정해서
     * 해당 엔드포인트로 들어오는 메시지를  /projectws/{projectId}/messages 주소로 처리한다.
     * /app은 config에서 자동 처리
     */

    /**
     * 프로젝트 웹소켓 채팅
     * 메세지를 받으면 채팅 로그를 저장하고 멤버들에게 전송한다.
     */
    @MessageMapping("/project-chat/{projectId}/send")
    @SendTo("/projectws/{projectId}/messages")
    public ChatMessageDto sendProjectMessages(@DestinationVariable(value="projectId") Long projectId, ChatMessageRequestDto requestDto) {
        log.info("받은 메시지 로그 = {}",requestDto);
        return chatService.sendProjectChat(requestDto.getData().getMemberId(), projectId, requestDto);
    }

    /**
     * 라이브코딩 웹소켓
     * 멤버의 세션 연결이 끊기면 이 채널을 통해 연결이 끊김이 나머지 멤버들에게 전달된다.
     */
    @MessageMapping("/project-code/{projectId}/send")
    @SendTo("/projectws/{projectId}/code")
    public CodeEditorDto sendCodeEditor(@DestinationVariable(value="projectId") Long projectId, CodeEditorDto requestDto) {
//        if (!projectService.isParticipated(requestDto.getData().getMemberId(), projectId)) {
//            log.error("참여하고 있지 않은 프로젝트입니다. = {}",requestDto);
//            throw new BaseException(ResponseStatus.FORBIDDEN.getMessage());
//        }

        return requestDto;
    }

    /**
     * 라이브코딩 웹세션 yjs 방식
     */
    @MessageMapping("/project-codeYJS/{projectId}/send")
    @SendTo("/projectws/{projectId}/codeYJS")
    public String sendCodeEditorYJS(@DestinationVariable(value="projectId") Long projectId, String requestDto) {
        return requestDto;
    }

    /**
     * 파일계층 웹소켓
     * 파일계층을 수정하는 메세지를 받으면 ec2 내부 파일을 수정하고 멤버들에게 전송한다.
     */
    @MessageMapping("/project-file/{projectId}/send")
    @SendTo("/projectws/{projectId}/files")
    public FileWebsocketResponseDto sendFileChanges(@DestinationVariable(value="projectId") Long projectId, FileWebsocketRequestDto requestDto, SimpMessageHeaderAccessor headerAccessor) {
        FileWebsocketResponseDto responseDto = new FileWebsocketResponseDto();

        // 세션 속성에서 memberId 가져오기
        String memberIdStr = (String) headerAccessor.getSessionAttributes().get("memberId");
        if (memberIdStr == null) {
            throw new BaseException(ResponseStatus.NOT_FOUND.getMessage());
        }
        Long memberId = Long.parseLong(memberIdStr);

        //프로젝트 참여하고 있는지 검증
        if (!projectService.isParticipated(memberId, projectId)) throw new BaseException(ResponseStatus.FORBIDDEN.getMessage());

        FileWebsocketRequestDto.RequestData data = requestDto.getData();
        if(data.getAction().equals(FileWebsocketAction.CREATE)) {
            return fileService.websocketSave(data, memberId, projectId);
        }
        else if (data.getAction().equals(FileWebsocketAction.RENAME)) {
            return fileService.websocketRename(data, memberId, projectId);
        }
        else if (data.getAction().equals(FileWebsocketAction.DELETE)) {
            return fileService.websocketDelete(data, memberId, projectId);
        }
        else {
            throw new BaseException(ResponseStatus.FORBIDDEN.getMessage());
        }
    }
}
