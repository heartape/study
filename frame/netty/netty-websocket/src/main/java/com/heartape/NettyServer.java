package com.heartape;

import com.heartape.handler.WebSocketChannelInboundHandler;
import com.heartape.handler.WebSocketTokenOauthHandler;
import com.heartape.jwt.HttpJwtDecoder;
import com.heartape.message.DefaultMessageFactory;
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
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import org.springframework.web.client.RestTemplate;

import java.net.MalformedURLException;
import java.net.URL;

public class NettyServer {

    private static final int port = 8080;
    private static final URL url;

    static {
        try {
            url = new URL("http://localhost:8888/oauth2/jwks");
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws Exception {
        ServerBootstrap bootstrap = new ServerBootstrap();
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ChannelFuture channelFuture = bootstrap
                    .group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel socketChannel) {
                            socketChannel.pipeline()
                                    .addLast(new HttpServerCodec())
                                    // 对写大数据流的支持
                                    .addLast(new ChunkedWriteHandler())
                                    // 对httpMessage进行聚合，聚合成FullHttpRequest或FullHttpResponse
                                    // 几乎在netty中的编程，都会使用到此handler
                                    .addLast(new HttpObjectAggregator(1024*64))
                                    // 增加心跳支持
                                    // 针对客户端，如果在1分钟时没有向服务端发送读写心跳(ALL)，则主动断开
                                    // 如果是读空闲或者写空闲，不处理
                                    .addLast(new IdleStateHandler(8, 10, 12))
                                    /*
                                     * websocket 服务器处理的协议，用于指定给客户端连接访问的路由 : /ws
                                     * 会帮你处理握手动作： handshaking（close, ping, pong） ping + pong = 心跳
                                     * 对于websocket来讲，都是以frames进行传输的，不同的数据类型对应的frames也不同
                                     */
                                    .addLast(new WebSocketServerProtocolHandler("/ws"))
                                    // token处理
                                    .addLast(new WebSocketTokenOauthHandler(new HttpJwtDecoder(new RestTemplate(), url, -1)))
                                    // 自定义的ws handler
                                    .addLast(new WebSocketChannelInboundHandler(new DefaultMessageFactory()));
                        }
                    }).bind(port).sync();
            System.out.println("Netty启动在端口：" + port);
            channelFuture.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}
