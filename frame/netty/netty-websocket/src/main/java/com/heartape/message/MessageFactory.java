package com.heartape.message;

import com.heartape.receiver.DeadlineSignedMessageReceiver;
import com.heartape.receiver.HelloMessageReceiver;
import com.heartape.receiver.PurposeSignedMessageReceiver;
import com.heartape.receiver.TextMessageReceiver;

public interface MessageFactory {

    HelloMessage hello(String clientId, HelloMessageReceiver receiver);
    TextMessage text(String clientId, TextMessageReceiver receiver);

    PurposeSignedMessage purpose(String clientId, PurposeSignedMessageReceiver receiver);

    DeadlineSignedMessage deadline(String clientId, DeadlineSignedMessageReceiver receiver);

}
