package com.heartape.controller;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@AllArgsConstructor
@Controller
public class StompGroupController {

    private final SimpMessagingTemplate template;

    @SubscribeMapping("/group/message")
    @SendTo("/receive/group/message")
    public String subscribe(@Header String id) {
        return "hello " + id;
    }

    @MessageMapping("/group/message")
    @SendTo("/receive/group/message")
    public String group(@Header String id, @Payload String message) {
        return id + ":" + message;
    }

    @PostMapping("/greeting")
    public void greetings(@RequestParam String greeting) {
        this.template.convertAndSend("/receive/group/message", "greeting" + greeting);
    }

    @MessageExceptionHandler
    @SendToUser(destinations="/chat/groups/error", broadcast=false)
    public String handleException(RuntimeException exception) {
        return "error!";
    }
}
