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
        // projectId 추출
        String projectFileId = getProjectFileId(session);
//        log.info("YJS 메시지 전달 projectId = {}",projectId);

        // 같은 프로젝트의 모든 세션에 메시지 브로드캐스트
        broadcastMessageToProject(projectFileId, message);
    }

    private String getProjectId(WebSocketSession session) {
        // URI에서 projectId 추출
        String path = session.getUri().getPath();
        return path.substring(path.lastIndexOf('/') + 1);
    }

    private void broadcastMessageToProject(String projectFileId, BinaryMessage message) {
        Map<String, WebSocketSession> sessions = projectSessionsMap.getOrDefault(projectFileId, new ConcurrentHashMap<>());
        for (WebSocketSession session : sessions.values()) {
            if (session.isOpen()) {
                try {
                    session.sendMessage(message);
                } catch (Exception e) {
                    log.error("YJS 메세지 전송 실패 projectFileId = {}, session = {}", projectFileId, session);
                }
            }
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // 연결된 세션을 projectId에 따라 관리
        String projectFileId = getProjectFileId(session);
        log.debug("YJS 연결 성공 projectFileId = {}", projectFileId);
        String token = extractQueryParam(session.getUri(), "tempToken");
        //토큰 검증 성공
        if (token != null) {
            Authentication auth = jwtTokenProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(auth);
            log.info("YJS 연결 성공 successful for session: {}", session.getId());
            if (auth.getPrincipal() instanceof CustomUserDetails) {
                log.debug("YJS 정보 객체 찾음");
                CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
                Long memberId = userDetails.getMemberId();

                // memberId와 projectId를 세션 속성으로 추가
                session.getAttributes().put("memberId", memberId);
                session.getAttributes().put("projectId", projectId);

                //프로젝트에 참가하지 않을 경우 세션 종료
                if(!projectService.isParticipated(memberId,Long.parseLong(projectFileId))) {
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
                        subscriptionManager.subscribe(memberId, Long.parseLong(projectFileId)); // 구독 추가
                    }
                    //구독 실패했을 경우
                    catch (BaseException e) {
                        log.error("");
                        session.close(new CloseStatus(4000, "failed subscription"));
                        return;
                    }
                }
                log.debug("프로젝트에 참가함  memberId = {} projectFileId={}",memberId,projectFileId);
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
        String projectFileId = getProjectFileId(session);
        Long memberId = (Long) session.getAttributes().get("memberId");
        log.debug("세션 종료 memberId = {}, projectId = {}",memberId,projectFileId);
        // 세션 종료 및 구독 해제 처리
        if (memberId != null) {
            try {
                subscriptionManager.unsubscribe(memberId, Long.parseLong(projectFileId)); // 구독 해제
            } catch (BaseException ignored) {

            }
        }
        Map<String, WebSocketSession> sessions = projectSessionsMap.get(projectFileId);
        if (sessions != null) {
            sessions.remove(session.getId());
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

    private String getProjectFileId(WebSocketSession session) {
        String path = session.getUri().getPath();
        String[] segments = path.split("/");
        String projectId = segments[segments.length - 2];
        String fileId = segments[segments.length - 1];
        return projectId + ";" + fileId; // projectId와 fileId 결합
    }
}

