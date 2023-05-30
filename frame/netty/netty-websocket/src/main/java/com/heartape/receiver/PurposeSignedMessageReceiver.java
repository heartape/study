package com.heartape.receiver;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 签收消息
 */
@Getter
@Setter
public class PurposeSignedMessageReceiver extends ImMessageReceiver {

    public List<String> purpose;

}
