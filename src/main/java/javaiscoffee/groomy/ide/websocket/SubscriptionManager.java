package javaiscoffee.groomy.ide.websocket;

import javaiscoffee.groomy.ide.security.BaseException;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 각 멤버의 구독 정보를 저장하는 클래스
 * 현재 구독하고 있는 프로젝트가 있는데 다른 프로젝트를 구독하려 하면 예외 처리
 */
@Component
public class SubscriptionManager {
    private final ConcurrentHashMap<Long, Long> subscriptions = new ConcurrentHashMap<>();

    /**
     * 사용자가 어떤 프로젝트를 구독할 때 사용
     */
    public void subscribe(Long memberId, Long projectId) {
        Long existingProjectId = subscriptions.get(memberId);
        //이미 구독중인 프로젝트와 다른 프로젝트를 같이 구독하려하면 예외처리
        if(existingProjectId != null && !existingProjectId.equals(projectId)) {
            throw new BaseException("다른 프로젝트를 이미 구독 중입니다.");
        }
        subscriptions.put(memberId,projectId);
    }

    /**
     * 특정 사용자가 특정 프로젝트의 구독을 명시적으로 해지할 때 사용
     * EX) 세션 연결은 유지하지만 프로젝트 구독만 해제할 때 사용
     */
    public synchronized void unsubscribe(Long memberId, Long projectId) {
        Long existingProjectId = subscriptions.get(memberId);
        //구독 중인 프로젝트가 있을 경우에만 삭제
        if (existingProjectId != null) {
            subscriptions.remove(memberId);
        }
    }

    /**
     * 사용자의 웹소켓 연결이 끊어졌을 때, 해당 사용자의 모든 구독 정보를 제거
     * EX) 사용자가 웹소켓 연결을 종료하거나, 네트워크 문제로 서버 측에서 연결이 끊어지는 등
     * 완전히 세션이 종료될 때 disconnect 사용
     */
    public synchronized void disconnect(Long memberId) {
        subscriptions.remove(memberId);
    }
}
