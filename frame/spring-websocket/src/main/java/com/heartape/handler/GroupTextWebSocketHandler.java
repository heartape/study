package com.heartape.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("NullableProblems")
@Slf4j
public class GroupTextWebSocketHandler extends TextWebSocketHandler {

    private final Map<String, WebSocketSession> SESSION_POOL = new ConcurrentHashMap<>();

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        log.info(message.getPayload());
        for (WebSocketSession webSocketSession : SESSION_POOL.values()) {
            webSocketSession.sendMessage(message);
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        SESSION_POOL.put(session.getId(), session);
        log.info("{}", SESSION_POOL.size());
        for (WebSocketSession webSocketSession : SESSION_POOL.values()) {
            webSocketSession.sendMessage(new TextMessage("hello world!"));
        }
    }

    @SuppressWarnings("EmptyTryBlock")
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        try (WebSocketSession ignored = SESSION_POOL.remove(session.getId())) {
        }
    }
}
