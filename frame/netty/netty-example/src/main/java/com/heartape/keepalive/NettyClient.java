package com.heartape.keepalive;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.nio.charset.StandardCharsets;

public class NettyClient {

    public static void main(String[] args) {
        start();
    }

    public static void start() {
        Bootstrap client = new Bootstrap();
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            ChannelFuture future = client
                    .group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) {
                            ch.config().setKeepAlive(true).setTcpNoDelay(true);
                            ch.pipeline()
                                    .addLast(new StringDecoder(StandardCharsets.UTF_8))
                                    .addLast(new StringEncoder(StandardCharsets.UTF_8))
                                    .addLast(new ClientHandler());
                        }
                    }).connect("127.0.0.1", 8080).sync();
            System.out.println("客户端启动");
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            group.shutdownGracefully();
        }
    }
}
