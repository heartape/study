package com.heartape.receiver;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * 签收消息
 */
@Getter
@Setter
public class DeadlineSignedMessageReceiver extends ImMessageReceiver {

    public LocalDate deadline;

}
