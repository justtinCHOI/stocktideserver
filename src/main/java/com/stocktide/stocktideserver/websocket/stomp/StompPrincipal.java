package com.stocktide.stocktideserver.websocket.stomp;

import java.security.Principal;

public class StompPrincipal implements Principal {
    private final String name;

    public StompPrincipal(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}