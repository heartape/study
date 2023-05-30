package com.heartape.message;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 文字聊天消息
 */
@Getter
@Setter
public class TextMessage extends ImMessage {

    private String content;

    public TextMessage(String clientId, String type, String receiver, String receiverType, LocalDateTime timestamp, String content) {
        super(null, clientId, type, receiver, receiverType, timestamp);
        this.content = content;
    }

    public TextMessage(String id, String clientId, String type, String receiver, String receiverType, LocalDateTime timestamp, String content) {
        super(id, clientId, type, receiver, receiverType, timestamp);
        this.content = content;
    }
}
