package com.heartape.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class SimpleWebSocketHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    /**
     * 用于记录和管理所有客户端的channel
     */
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private final static ChannelGroup clients = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        String content = msg.text();
        clients.writeAndFlush(new TextWebSocketFrame(content));
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
        clients.writeAndFlush(new TextWebSocketFrame("系统通知：有人加入"));
    }

    /**
     * 当触发handlerRemoved，ChannelGroup会自动移除对应客户端的channel
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        clients.writeAndFlush(new TextWebSocketFrame("系统通知：有人离开"));
        clients.remove(ctx.channel());
    }

}
