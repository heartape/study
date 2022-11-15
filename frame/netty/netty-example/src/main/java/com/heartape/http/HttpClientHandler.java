package com.heartape.http;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.util.CharsetUtil;

public class HttpClientHandler extends SimpleChannelInboundHandler<FullHttpResponse> {

    private ChannelHandlerContext ctx;

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("连接已断开");
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("连接的服务端地址:" + ctx.channel().remoteAddress());
        this.ctx = ctx;
        send();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, FullHttpResponse response) throws Exception {
        ByteBuf content = response.content();
        HttpHeaders headers = response.headers();
        System.out.println("content:" + content.toString(CharsetUtil.UTF_8));
        System.out.println("headers:" + headers.toString());
    }

    public void send() {
        //配置HttpRequest的请求数据和一些配置信息
        FullHttpRequest request = HttpUtils.GetMapping("/test", "get key value\n");
        ctx.writeAndFlush(request);
    }
}
