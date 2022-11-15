package com.heartape.http;

import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.*;

import java.nio.charset.StandardCharsets;

public class HttpUtils {

    public static FullHttpRequest GetMapping(String path, String comment) {

        FullHttpRequest request = new DefaultFullHttpRequest(
                HttpVersion.HTTP_1_1,
                HttpMethod.GET,
                path,
                Unpooled.wrappedBuffer(comment.getBytes(StandardCharsets.UTF_8)));
        request.headers()
                // .set(HttpHeaderNames.CONTENT_TYPE,"text/plain;charset=UTF-8")
                //开启长连接
                .set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE)
                //设置传递请求内容的长度
                // .set(HttpHeaderNames.CONTENT_LENGTH,request.content().readableBytes())
                .set(HttpHeaderNames.ACCEPT, "*/*")
                .set(HttpHeaderNames.ACCEPT_ENCODING, "gzip, deflate, br");
        return request;
    }
}
