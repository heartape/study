package com.heartape.message;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 签收消息
 */
@Getter
@Setter
public class DeadlineSignedMessage extends ImMessage {

    private LocalDate deadline;

    public DeadlineSignedMessage(String clientId, String type, String receiver, String receiverType, LocalDateTime timestamp, LocalDate deadline) {
        super(null, clientId, type, receiver, receiverType, timestamp);
        this.deadline = deadline;
    }
    public DeadlineSignedMessage(String id, String clientId, String type, String receiver, String receiverType, LocalDateTime timestamp, LocalDate deadline) {
        super(id, clientId, type, receiver, receiverType, timestamp);
        this.deadline = deadline;
    }

}
