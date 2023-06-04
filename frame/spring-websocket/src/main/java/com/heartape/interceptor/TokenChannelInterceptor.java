package com.heartape.interceptor;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;

@AllArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
@Configuration
public class TokenChannelInterceptor implements ChannelInterceptor {

    private final JwtDecoder jwtDecoder;

    private final static String BEAR = "Bearer ";

    @SuppressWarnings("NullableProblems")
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        StompCommand command = accessor.getCommand();
        if (StompCommand.CONNECT.equals(command) || StompCommand.SUBSCRIBE.equals(command) || StompCommand.SEND.equals(command)) {
            String bearToken = accessor.getFirstNativeHeader(HttpHeaders.AUTHORIZATION);
            if (bearToken == null || !bearToken.startsWith(BEAR)){
                return null;
            }
            String token = bearToken.substring(BEAR.length());
            // BearerTokenResolver
            // BearerTokenAuthenticationFilter
            // Authentication authenticate = authenticationManager.authenticate(new BearerTokenAuthenticationToken(token));
            Jwt jwt = jwtDecoder.decode(token);
            JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
            AbstractAuthenticationToken abstractAuthenticationToken = jwtAuthenticationConverter.convert(jwt);
            accessor.setUser(abstractAuthenticationToken);
        }
        return message;
    }
}
