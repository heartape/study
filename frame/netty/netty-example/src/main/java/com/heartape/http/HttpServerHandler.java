package com.heartape.http;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

public class HttpServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    /**
     * 用自己写的netty http客户端发送请求时，会接收两次请求，第二次path="/bad-request",原因未知。
     */
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, FullHttpRequest httpRequest) throws Exception {
        try{
            //获取路径
            String path=httpRequest.uri();
            System.out.println("path:" + path);
            //获取body
            String body = httpRequest.content().toString(CharsetUtil.UTF_8);
            System.out.println("body:" + body);
            //获取请求方法
            HttpMethod method=httpRequest.method();
            System.out.println("method:" + method.name());

            boolean keepAlive = HttpUtil.isKeepAlive(httpRequest);
            System.out.println("keepAlive:" + keepAlive);

            FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, Unpooled.copiedBuffer("已收到请求", CharsetUtil.UTF_8));
            response.headers().set(HttpHeaderNames.CONTENT_TYPE,"text/plain;charset=UTF-8");
            channelHandlerContext.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        }catch(Exception e){
            System.out.println("处理请求失败!");
        }
    }

    /**
     * 建立连接时，返回消息
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("连接的客户端地址:" + ctx.channel().remoteAddress());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Channel channel = ctx.channel();
        if(channel.isActive())ctx.close();
    }
}
