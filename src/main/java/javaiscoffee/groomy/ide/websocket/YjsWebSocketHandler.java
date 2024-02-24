package javaiscoffee.groomy.ide.websocket;

import javaiscoffee.groomy.ide.project.ProjectService;
import javaiscoffee.groomy.ide.response.ResponseStatus;
import javaiscoffee.groomy.ide.security.BaseException;
import javaiscoffee.groomy.ide.security.CustomUserDetails;
import javaiscoffee.groomy.ide.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@EnableWebSocket
@RequiredArgsConstructor
public class YjsWebSocketHandler extends AbstractWebSocketHandler {
    private final JwtTokenProvider jwtTokenProvider;
    private final ProjectService projectService;
    private final SubscriptionManager subscriptionManager; // SubscriptionManager 추가
    private final Map<String, Map<String, WebSocketSession>> projectSessionsMap = new ConcurrentHashMap<>();

    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) throws Exception {
        // projectFileId 추출
        String projectFileId = (String) session.getAttributes().get("projectFileId");
        // 같은 프로젝트의 모든 세션에 메시지 브로드캐스트
        broadcastMessageToProject(projectFileId, message);
    }

    private void broadcastMessageToProject(String projectFileId, BinaryMessage message) {
        Map<String, WebSocketSession> sessions = projectSessionsMap.getOrDefault(projectFileId, new ConcurrentHashMap<>());
        for (WebSocketSession session : sessions.values()) {
            if (session.isOpen()) {
                try {
                    session.sendMessage(message);
//                    log.info("YJS 메시지 전달 projectId = {} => 세션 ID = {}",projectFileId, session);
                } catch (Exception e) {
//                    log.error("YJS 메세지 전송 실패 projectFileId = {}, session = {}", projectFileId, session);
                }
            }
            else {
//                log.error("YJS 세션 끊겨서 못 보냄 세션 ID = {}",session);
            }
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // 연결된 세션을 projectId에 따라 관리
        String projectId = getProjectId(session);
        if(projectId==null) {
            try {
                session.close(new CloseStatus(400, "Invalid projectId"));
            } catch (IOException e) {
                throw new BaseException(ResponseStatus.UNAUTHORIZED.getMessage());
            }
        }
        String projectFileId = getProjectFileId(session);
        String token = extractQueryParam(session.getUri(), "tempToken");
        //토큰 검증 성공
        if (token != null && jwtTokenProvider.validateToken(token)) {
            Authentication auth = jwtTokenProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(auth);
            log.info("YJS 연결 성공 successful for session: {}", session.getId());
            if (auth.getPrincipal() instanceof CustomUserDetails) {
                log.debug("YJS 정보 객체 찾음");
                CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
                Long memberId = userDetails.getMemberId();

                // memberId와 projectId를 세션 속성으로 추가
                session.getAttributes().put("memberId", memberId);
                session.getAttributes().put("projectId", Long.parseLong(projectId));
                session.getAttributes().put("projectFileId",projectFileId);

                //프로젝트에 참가하지 않을 경우 세션 종료
                if(!projectService.isParticipated(memberId,Long.parseLong(projectId))) {
//                    log.error("YJS 프로젝트 참여하지 않음 memberId = {} projectId={}",memberId,projectId);
                    try {
                        session.close(new CloseStatus(4000, "not Participated"));
                        return;
                    } catch (IOException e) {
                        throw new BaseException(ResponseStatus.UNAUTHORIZED.getMessage());
                    }

                }
                //프로젝트에 참가해서 세션 연결을 생성
                else {
                    try {
                        subscriptionManager.subscribe(memberId, Long.parseLong(projectId)); // 구독 추가
                    }
                    //구독 실패했을 경우
                    catch (BaseException e) {
                        log.error("");
                        session.close(new CloseStatus(4000, "failed subscription"));
                        return;
                    }
                }
                log.info("프로젝트에 참가함  memberId = {} projectFileId={}",memberId,projectFileId);
                projectSessionsMap.computeIfAbsent(projectFileId, k -> new ConcurrentHashMap<>()).put(session.getId(), session);
            }
        }
        //토큰 검증 실패
        else {
            log.error("YJS 토큰 검증 실패 for session: {}. Token: {}", session.getId(), token);
            try {
                session.close(new CloseStatus(4000, "Invalid token"));
            } catch (IOException e) {
                throw new BaseException(ResponseStatus.UNAUTHORIZED.getMessage());
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        // 세션 종료 처리
        Long projectId = (Long) session.getAttributes().get("projectId");
        String projectFileId = (String) session.getAttributes().get("projectFileId");
        Long memberId = (Long) session.getAttributes().get("memberId");
        log.info("==================================================================");
        log.info("YJS 세션 종료 memberId = {}, projectId = {}, projectFileId",memberId,projectId,projectFileId);
        // 세션 종료 및 구독 해제 처리
        if (memberId != null) {
            try {
                subscriptionManager.disconnect(memberId); // 구독 해제
            } catch (BaseException ignored) {

            }
        }
        Map<String, WebSocketSession> sessions = projectSessionsMap.get(projectFileId);
        if (sessions != null) {
            sessions.remove(session.getId());
            log.info("YJS 세션 종료 끝");
            if (sessions.isEmpty()) {
                projectSessionsMap.remove(projectFileId);
            }
        }
    }

    private String extractQueryParam(URI uri, String paramName) {
        String query = uri.getQuery();
        if (query != null) {
            String[] params = query.split("&");
            for (String param : params) {
                String[] keyValue = param.split("=");
                if (keyValue.length == 2 && keyValue[0].equals(paramName)) {
                    return keyValue[1];
                }
            }
        }
        return null; // 파라미터가 없는 경우
    }

    private String getProjectId(WebSocketSession session) {
        String path = session.getUri().getPath();
        String[] segments = path.split("/");
        // 예제 URI: /YJS/123/456?tempToken=어쩌구
        // segments 배열은 ["", "YJS", "123", "456.txt"]
        if (segments.length > 2) { // 세그먼트 길이 검증
            return segments[2]; // 실제 projectId 위치에 따라 인덱스 조정 필요
        }
        // 적절한 projectId를 찾을 수 없는 경우
        return null;
    }

    private String getProjectFileId(WebSocketSession session) {
        String path = session.getUri().getPath();
        String[] segments = path.split("/");
        String projectId = segments[segments.length - 2];
        String fileId = segments[segments.length - 1];
        return projectId + ";" + fileId; // projectId와 fileId 결합
    }
}

