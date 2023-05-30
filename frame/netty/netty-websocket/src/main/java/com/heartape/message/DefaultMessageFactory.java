package com.heartape.message;

import com.heartape.receiver.DeadlineSignedMessageReceiver;
import com.heartape.receiver.HelloMessageReceiver;
import com.heartape.receiver.PurposeSignedMessageReceiver;
import com.heartape.receiver.TextMessageReceiver;

public class DefaultMessageFactory implements MessageFactory {

    @Override
    public HelloMessage hello(String clientId, HelloMessageReceiver receiver) {
        return new HelloMessage(clientId, receiver.type, receiver.timestamp);
    }

    @Override
    public TextMessage text(String clientId, TextMessageReceiver receiver) {
        return new TextMessage(clientId, receiver.type, receiver.receiver, receiver.receiverType, receiver.timestamp, receiver.content);
    }

    @Override
    public PurposeSignedMessage purpose(String clientId, PurposeSignedMessageReceiver receiver) {
        return new PurposeSignedMessage(clientId, receiver.type, receiver.receiver, receiver.receiverType, receiver.timestamp, receiver.purpose);
    }

    @Override
    public DeadlineSignedMessage deadline(String clientId, DeadlineSignedMessageReceiver receiver) {
        return new DeadlineSignedMessage(clientId, receiver.type, receiver.receiver, receiver.receiverType, receiver.timestamp, receiver.deadline);
    }


}
