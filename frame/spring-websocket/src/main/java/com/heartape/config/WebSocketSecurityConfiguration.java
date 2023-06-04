package com.heartape.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.security.authorization.AuthorityAuthorizationManager;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.web.socket.EnableWebSocketSecurity;
import org.springframework.security.messaging.access.intercept.MessageMatcherDelegatingAuthorizationManager;

/**
 * todo:目前无法禁用csrf配置，在不使用cookie的情况下不注入
 */
// @Configuration
// @EnableWebSocketSecurity
public class WebSocketSecurityConfiguration {

    // @Bean
    // public AuthorizationManager<Message<?>> messageAuthorizationManager(MessageMatcherDelegatingAuthorizationManager.Builder messages) {
    //     return AuthorityAuthorizationManager.hasAuthority("profile");
        // return messages
        //         .nullDestMatcher().authenticated()
        //         .simpTypeMatchers(SimpMessageType.CONNECT).hasAuthority("read")
        //         .simpSubscribeDestMatchers("/user/**", "/topic/friends/*").hasAuthority("read")
        //         .simpDestMatchers("/person/**", "/group/**").hasAuthority("write")
        //         .simpTypeMatchers(SimpMessageType.MESSAGE, SimpMessageType.SUBSCRIBE).denyAll()
        //         .anyMessage().denyAll()
        //         .build();
    // }
}
