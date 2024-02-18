package javaiscoffee.groomy.ide.websocket;

import jakarta.websocket.server.ServerEndpoint;
import javaiscoffee.groomy.ide.project.ProjectService;
import javaiscoffee.groomy.ide.response.ResponseStatus;
import javaiscoffee.groomy.ide.security.BaseException;
import javaiscoffee.groomy.ide.security.CustomUserDetails;
import javaiscoffee.groomy.ide.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.server.standard.SpringConfigurator;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import java.io.IOException;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
@EnableWebSocketMessageBroker
@ServerEndpoint(value = "/YJS/{projectId}", configurator = SpringConfigurator.class)
public class YJSEndpoint {
    private final JwtTokenProvider jwtTokenProvider;
    private final ProjectService projectService;
    // 프로젝트 ID와 해당 프로젝트에 연결된 세션의 목록을 매핑
    private static Map<String, Set<Session>> projectSessionsMap = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("projectId") String projectId) {
        log.info("YJS 연결 시작");
        // URL 쿼리 파라미터에서 인증 토큰 추출
        String token = getQueryParam(session, "tempToken");

        if (jwtTokenProvider.validateToken(token)) {
            log.info("YJS 토근 검증 성공");
            Authentication auth = jwtTokenProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(auth);
            if (auth.getPrincipal() instanceof CustomUserDetails) {
                log.info("YJS 정보 객체 찾음");
                CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
                Long memberId = userDetails.getMemberId();
                if(!projectService.isParticipated(memberId,Long.parseLong(projectId))) {
                    log.error("YJS 프로젝트 참여하지 않음 memberId = {} projectId={}",memberId,projectId);
                    try {
                        session.close();
                    } catch (IOException e) {
                        throw new BaseException(ResponseStatus.UNAUTHORIZED.getMessage());
                    }
                }
                session.getUserProperties().put("memberId", memberId);
                session.getUserProperties().put("projectId", projectId);
            }
            projectSessionsMap.computeIfAbsent(projectId, k -> ConcurrentHashMap.newKeySet()).add(session);
        } else {
            log.error("YJS 토큰 검증 실패 = {}",token);
            try {
                session.close();
            } catch (IOException e) {
                throw new BaseException(ResponseStatus.UNAUTHORIZED.getMessage());
            }
        }
    }

    @OnClose
    public void onClose(Session session, @PathParam("projectId") String projectId) {
        Set<Session> sessions = projectSessionsMap.getOrDefault(projectId, ConcurrentHashMap.newKeySet());
        sessions.remove(session);
        if (sessions.isEmpty()) {
            projectSessionsMap.remove(projectId);
        }
    }

    @OnMessage
    public void onMessage(String message, Session session, @PathParam("projectId") String projectId) throws IOException {
        // projectId에 해당하는 모든 세션에 메시지를 브로드캐스트
        for (Session s : projectSessionsMap.getOrDefault(projectId, ConcurrentHashMap.newKeySet())) {
            if (s.isOpen()) {
                s.getBasicRemote().sendText(message);
            }
        }
    }

    private String getQueryParam(Session session, String key) {
        URI uri = session.getRequestURI();
        String query = uri.getQuery();
        if (query == null) return null;
        Map<String, String> queryParams = parseQueryParams(query);
        return queryParams.getOrDefault(key, null);
    }

    private Map<String, String> parseQueryParams(String query) {
        Map<String, String> queryParams = new HashMap<>();
        String[] params = query.split("&");
        for (String param : params) {
            String[] pair = param.split("=");
            if (pair.length > 1) {
                String key = URLDecoder.decode(pair[0], StandardCharsets.UTF_8);
                String value = URLDecoder.decode(pair[1], StandardCharsets.UTF_8);
                queryParams.put(key, value);
            }
        }
        return queryParams;
    }
}
