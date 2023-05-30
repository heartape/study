package com.heartape.handler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.heartape.matcher.ClientIdChannelMatcher;
import com.heartape.MessageTypeEnum;
import com.heartape.matcher.CompositeOrMatcher;
import com.heartape.message.HelloMessage;
import com.heartape.message.MessageFactory;
import com.heartape.message.PurposeSignedMessage;
import com.heartape.message.TextMessage;
import com.heartape.receiver.HelloMessageReceiver;
import com.heartape.receiver.PurposeSignedMessageReceiver;
import com.heartape.receiver.TextMessageReceiver;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Slf4j
@AllArgsConstructor
public class WebSocketChannelInboundHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    /**
     * 用于记录和管理所有客户端的channel
     */
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private final static ChannelGroup clients = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final MessageFactory messageFactory;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        Channel channel = ctx.channel();
        String clientId = channel.attr(ClientIdChannelMatcher.CLIENT_ID_KEY).get();

        String content = msg.text();

        JsonNode jsonNode = objectMapper.readTree(content);
        if (!jsonNode.has("type")){
            errorResponse(ctx);
            return;
        }
        String type = jsonNode.get("type").asText();
        if (type == null){
            errorResponse(ctx);
            return;
        }
        if (MessageTypeEnum.HELLO.name().equals(type)){
            HelloMessageReceiver helloMessageReceiver;
            try {
                helloMessageReceiver = this.objectMapper.readValue(content, HelloMessageReceiver.class);
            } catch (Exception e){
                return;
            }
            HelloMessage helloMessage = this.messageFactory.hello(clientId, helloMessageReceiver);
            helloMessage.setId(UUID.randomUUID().toString());
            // todo:数据库流水

            String res = objectMapper.writeValueAsString(helloMessage);
            clients.writeAndFlush(new TextWebSocketFrame(res));
        } else if (MessageTypeEnum.TEXT.name().equals(type)){
            TextMessageReceiver textMessageReceiver;
            try {
                textMessageReceiver = objectMapper.readValue(content, TextMessageReceiver.class);
            } catch (Exception e){
                return;
            }
            if (textMessageReceiver == null || textMessageReceiver.getReceiver() == null){
                return;
            }

            String receiver = textMessageReceiver.getReceiver();
            TextMessage textMessage = this.messageFactory.text(clientId, textMessageReceiver);
            textMessage.setId(UUID.randomUUID().toString());
            // todo:保存到数据库

            String res = objectMapper.writeValueAsString(textMessage);
            clients.writeAndFlush(new TextWebSocketFrame(res), new CompositeOrMatcher(new ClientIdChannelMatcher(receiver), ClientIdChannelMatcher.getInstance(channel)));
        } else if (MessageTypeEnum.PURPOSE_SIGNED.name().equals(type)){
            PurposeSignedMessageReceiver purposeSignedMessageReceiver;
            try {
                purposeSignedMessageReceiver = objectMapper.readValue(content, PurposeSignedMessageReceiver.class);
            } catch (Exception e){
                return;
            }
            if (purposeSignedMessageReceiver == null || purposeSignedMessageReceiver.getReceiver() == null){
                return;
            }

            String receiver = purposeSignedMessageReceiver.getReceiver();
            PurposeSignedMessage purposeSignedMessage = this.messageFactory.purpose(clientId, purposeSignedMessageReceiver);
            purposeSignedMessage.setId(UUID.randomUUID().toString());
            // todo:更新数据库消息状态

            String res = objectMapper.writeValueAsString(purposeSignedMessage);
            clients.writeAndFlush(new TextWebSocketFrame(res), new CompositeOrMatcher(new ClientIdChannelMatcher(receiver), ClientIdChannelMatcher.getInstance(channel)));
        } else {
            errorResponse(ctx);
        }
    }

    /**
     * 开启连接
     * 获取客户端的channel，并且放到ChannelGroup中去进行管理
     * 同时建立客户端索引
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        clients.add(channel);
    }

    /**
     * 当触发handlerRemoved，ChannelGroup会自动移除对应客户端的channel
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        clients.remove(ctx.channel());
    }

    private void warnResponse(ChannelHandlerContext ctx, String warn){
        ctx.writeAndFlush(new TextWebSocketFrame(warn));
    }

    private void errorResponse(ChannelHandlerContext ctx){
        errorResponse(ctx, "error!");
    }

    private void errorResponse(ChannelHandlerContext ctx, String error){
        ctx.writeAndFlush(new TextWebSocketFrame(error)).addListener(ChannelFutureListener.CLOSE);
    }
}
