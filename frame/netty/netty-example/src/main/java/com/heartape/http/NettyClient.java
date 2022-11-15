package com.heartape.http;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;

public class NettyClient {

    public static void main(String[] args) {
        Bootstrap client = new Bootstrap();
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            ChannelFuture future = client
                    .group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) {
                            ch.config().setKeepAlive(true).setTcpNoDelay(true);
                            ch.pipeline()
                                    .addLast(new HttpClientCodec())
                                    .addLast(new HttpObjectAggregator(1024 * 1024))
                                    // .addLast(new ChunkedWriteHandler())
                                    .addLast(new HttpClientHandler());
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
