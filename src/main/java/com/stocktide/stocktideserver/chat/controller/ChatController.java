package com.stocktide.stocktideserver.chat.controller;

import com.stocktide.stocktideserver.chat.entity.ChatMessage;
import com.stocktide.stocktideserver.chat.entity.UserStatusMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Controller
public class ChatController {
    @Autowired
    private SimpMessageSendingOperations messagingTemplate;
    private final Set<String> connectedUsers = ConcurrentHashMap.newKeySet();


    // '/app/chat.addUser's 새 사용자 참여 메시지가 전송되면 처리
    // 세션에 사용자 정보를 저장하고 참여 메시지를 브로드캐스트
    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public ChatMessage addUser(@Payload ChatMessage chatMessage,
                               SimpMessageHeaderAccessor headerAccessor) {
        log.info("addUser chatMessage: {}", chatMessage.toString());
        // 웹소켓 세션에 유저 이름 추가
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
        log.info("Adding user to session: {}", chatMessage.getSender());
        // 사용자 목록에 추가하고 모든 클라이언트에게 업데이트된 목록 전송
        connectedUsers.add(chatMessage.getSender());
        messagingTemplate.convertAndSend("/topic/users",
                new UserStatusMessage("CONNECTED",
                        chatMessage.getSender(),
                        new ArrayList<>(connectedUsers)));
        return chatMessage;
    }

    // '/app/chat.sendMessage'로 메시지가 전송되면 처리하는 메서드
    // @SendTo로 지정된 '/topic/public'으로 메시지를 브로드캐스트
    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
        // 현재 시간을 메시지에 추가
        log.info("sendMessage chatMessage: {}", chatMessage.toString());
        chatMessage.setTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));
        return chatMessage;
    }
}
