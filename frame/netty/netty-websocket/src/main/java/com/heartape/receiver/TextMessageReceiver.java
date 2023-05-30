package com.heartape.receiver;

import com.heartape.message.TextMessage;

/**
 * 文字聊天消息
 */

public class TextMessageReceiver extends ImMessageReceiver {

    public String content;

    public TextMessage entity(String id, String clientId){
        return new TextMessage(id, clientId, super.type, super.receiver, super.receiverType, super.timestamp, this.content);
    }
}
