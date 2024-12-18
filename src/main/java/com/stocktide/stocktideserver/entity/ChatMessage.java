package com.stocktide.stocktideserver.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {
    private MessageType type;
    private String content;
    private String sender;
    private String time;

    public enum MessageType {
        CHAT,
        JOIN,
        LEAVE
    }
}