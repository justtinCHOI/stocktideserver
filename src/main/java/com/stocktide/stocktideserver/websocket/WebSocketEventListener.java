package com.stocktide.stocktideserver.websocket;

import com.stocktide.stocktideserver.entity.UserStatusMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

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

        // null 체크를 안전하게 수행
        if (headerAccessor.getSessionAttributes() != null) {
            String username = (String) Objects.requireNonNull(headerAccessor.getSessionAttributes()).get("username");

            if (username != null) {
                // 접속자 목록 업데이트 브로드캐스트
                connectedUsers.add(username);
                messagingTemplate.convertAndSend("/topic/users",
                        new UserStatusMessage("CONNECTED", username, new ArrayList<>(connectedUsers)));
            }
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