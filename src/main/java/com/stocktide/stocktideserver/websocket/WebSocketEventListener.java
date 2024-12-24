package com.stocktide.stocktideserver.websocket;

import com.stocktide.stocktideserver.chat.entity.UserStatusMessage;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Log4j2
@Component
public class WebSocketEventListener {
    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    // 동시성을 고려한 연결된 사용자 목록 관리
    private final Set<String> connectedUsers = ConcurrentHashMap.newKeySet();

    // WebSocket 연결이 성공했을 때 호출되는 이벤트 핸들러
    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        log.info("connect 1111 attributes: {}", headerAccessor.getSessionAttributes());

        // null 체크를 안전하게 수행
        if (headerAccessor.getSessionAttributes() != null) {
            String username = (String) Objects.requireNonNull(headerAccessor.getSessionAttributes()).get("username");

            log.info("connect 2222 username: {}", username);
            if (username != null) {
                // 접속자 목록 업데이트 브로드캐스트
                connectedUsers.add(username);
                log.info("User connected: " + username);
                log.info("Current users: " + connectedUsers);
                messagingTemplate.convertAndSend("/topic/users",
                        new UserStatusMessage("CONNECTED", username, new ArrayList<>(connectedUsers)));
            }
        }
    }

    // 메시지 수신 시
    @EventListener
    public void handleWebSocketSubscribeListener(SessionSubscribeEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        log.info("subscribe 1111 attributes: {}", headerAccessor.getSessionAttributes());

        if (headerAccessor.getSessionAttributes() != null) {
            String username = (String) headerAccessor.getSessionAttributes().get("username");

            log.info("subscribe 2222 username: {}", username);
            if (username != null) {
                connectedUsers.add(username);
                messagingTemplate.convertAndSend("/topic/users",
                        new UserStatusMessage("CONNECTED", username, new ArrayList<>(connectedUsers)));
            }
        }
    }

    // 사용자 메시지 처리를 위한 새로운 이벤트 핸들러 추가
    @EventListener
    public void handleWebSocketMessageListener(SessionConnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String username = headerAccessor.getFirstNativeHeader("username");
        log.info("message 1111 attributes: {}", headerAccessor.getSessionAttributes());

        if (username != null) {
            log.info("message 2222 username: {}", username);
            headerAccessor.getSessionAttributes().put("username", username);
            connectedUsers.add(username);
        }
    }

    // WebSocket 연결이 끊어졌을 때 호출되는 이벤트 핸들러
    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String username = (String) Objects.requireNonNull(headerAccessor.getSessionAttributes()).get("username");

        if (username != null) {
            // 사용자 목록에서 제거하고 모든 클라이언트에게 업데이트 브로드캐스트
            connectedUsers.remove(username);
            messagingTemplate.convertAndSend("/topic/users",
                    new UserStatusMessage("DISCONNECTED", username, new ArrayList<>(connectedUsers)));
        }
    }
}