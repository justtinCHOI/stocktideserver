package com.stocktide.stocktideserver.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class UserStatusMessage {
    private String type;
    private String username;
    private List<String> connectedUsers;
}