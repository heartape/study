package com.heartape.tcp;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
// import lombok.extern.slf4j.Slf4j;

// import java.nio.charset.Charset;

@Sharable
// @Slf4j
public class TcpServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        // log.info("通道已注册");
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        // log.info("通道存活");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        // log.info("通道死亡");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        // log.info("=====start=====");
        // 业务代码
        // log.info("客户端的消息为:{}",((ByteBuf)msg).toString(Charset.defaultCharset()));

        //返回给客户端的数据，告诉我已经读到你的数据了
        String result = "\nhello client ";
        ByteBuf buf = Unpooled.buffer();
        buf.writeBytes(result.getBytes());
        ctx.channel().writeAndFlush(buf);

        ByteBuf buf2 = Unpooled.buffer();
        buf2.writeBytes("\r\n".getBytes());
        ctx.channel().writeAndFlush(buf2);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx);
        // log.info("=====end=====");
    }
}
