package com.heartape.http;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.cors.CorsConfig;
import io.netty.handler.codec.http.cors.CorsConfigBuilder;
import io.netty.handler.codec.http.cors.CorsHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class NettyServer {

    private static final int port = 8080;

    public static void main(String[] args) throws Exception {
        // 用于启动服务端
        ServerBootstrap bootstrap = new ServerBootstrap();
        // 用来处理accept事件
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        // 用来处理通道的读写事件
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ChannelFuture channelFuture = bootstrap
                    .group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 5)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel socketChannel) {
                            CorsConfig corsConfig = CorsConfigBuilder.forAnyOrigin().allowNullOrigin().allowCredentials().build();
                            socketChannel.pipeline()
                                    .addLast(new HttpServerCodec())
                                    .addLast(new HttpObjectAggregator(1024 * 1024))
                                    // .addLast(new ChunkedWriteHandler())
                                    .addLast(new CorsHandler(corsConfig))
                                    .addLast(new HttpServerHandler());
                        }
                    }).bind(port).sync();
            System.out.println("Netty启动在端口：" + port);
            //等待服务监听端口关闭
            channelFuture.channel().closeFuture().sync();
        } finally {
            //退出，释放线程资源
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}
