package com.heartape.handler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.heartape.exception.JwtParseException;
import com.heartape.jwt.JwtDecoder;
import com.heartape.matcher.ClientIdChannelMatcher;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.AttributeKey;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class WebSocketTokenOauthHandler extends ChannelInboundHandlerAdapter {

    public final static AttributeKey<String> ATTRIBUTE_TOKEN_KEY = AttributeKey.<String>valueOf("token");

    private final static String TOKEN_KEY = "token";

    private final JwtDecoder jwtDecoder;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof TextWebSocketFrame socketFrame){
            String content = socketFrame.text();
            JsonNode jsonNode = objectMapper.readTree(content);
            if (!jsonNode.has(TOKEN_KEY)){
                errorResponse(ctx);
                return;
            }

            String token = jsonNode.get(TOKEN_KEY).asText();
            String id;
            try {
                if (token == null){
                    throw new JwtParseException("token is empty");
                }
                id = jwtDecoder.decode(token).getSubject();
            } catch (JwtParseException e){
                errorResponse(ctx, e.getMessage());
                return;
            }
            ctx.channel().attr(ClientIdChannelMatcher.CLIENT_ID_KEY).set(id);
            ctx.channel().attr(ATTRIBUTE_TOKEN_KEY).set(token);
        }
        ctx.fireChannelRead(msg);
    }

    private void errorResponse(ChannelHandlerContext ctx){
        errorResponse(ctx, "error!");
    }

    private void errorResponse(ChannelHandlerContext ctx, String error){
        ctx.writeAndFlush(new TextWebSocketFrame(error)).addListener(ChannelFutureListener.CLOSE);
    }
}
