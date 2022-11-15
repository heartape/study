package com.heartape.tcp;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class NettyServer {

    private final int port = 80;

    public void run() throws Exception {
        // 用来处理accept事件
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        // 用来处理通道的读写事件
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        System.out.println("Netty启动在端口：" + port);
        try {
            // 用于启动服务端
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    // 绑定服务端通道
                    .channel(NioServerSocketChannel.class)
                    // 用于构造服务端套接字ServerSocket对象，标识当服务器请求处理线程全满时，用于临时存放已完成三次握手的请求的队列的最大长度。
                    // 用来初始化服务端可连接队列
                    // 服务端处理客户端连接请求是按顺序处理的，所以同一时间只能处理一个客户端连接，多个客户端来的时候，服务端将不能处理的客户端连接请求放在队列中等待处理，backlog参数指定了队列的大小。
                    .option(ChannelOption.SO_BACKLOG, 128)
                    // 处理读写事件，ChannelInitializer给通道初始化
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new TcpServerHandler());
                        }
                    });
            //绑定端口，同步等待成功
            ChannelFuture f = bootstrap.bind(port).sync();
            //等待服务监听端口关闭
            f.channel().closeFuture().sync();
        } finally {
            //退出，释放线程资源
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}
