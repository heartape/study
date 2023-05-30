package com.heartape.message;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 签收消息
 */
@Getter
@Setter
public class PurposeSignedMessage extends ImMessage {

    private List<String> purpose;

    public PurposeSignedMessage(String clientId, String type, String receiver, String receiverType, LocalDateTime timestamp, List<String> purpose) {
        super(null, clientId, type, receiver, receiverType, timestamp);
        this.purpose = purpose;
    }

    public PurposeSignedMessage(String id, String clientId, String type, String receiver, String receiverType, LocalDateTime timestamp, List<String> purpose) {
        super(id, clientId, type, receiver, receiverType, timestamp);
        this.purpose = purpose;
    }

}
