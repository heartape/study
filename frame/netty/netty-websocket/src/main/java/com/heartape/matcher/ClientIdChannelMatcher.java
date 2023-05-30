package com.heartape.matcher;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelMatcher;
import io.netty.util.AttributeKey;

import java.util.Objects;

/**
 * 根据ClientId匹配通道
 * todo:考虑将ClientIdChannelMatcher缓存并定时淘汰
 */
public class ClientIdChannelMatcher implements ChannelMatcher {

    public final static AttributeKey<String> CLIENT_ID_KEY = AttributeKey.<String>valueOf("clientId");
    private final String clientId;

    public ClientIdChannelMatcher(String clientId) {
        this.clientId = clientId;
    }

    public static ClientIdChannelMatcher getInstance(Channel channel){
        String clientId = channel.attr(CLIENT_ID_KEY).get();
        return new ClientIdChannelMatcher(clientId);
    }

    @Override
    public boolean matches(Channel channel) {
        return Objects.equals(channel.attr(CLIENT_ID_KEY).get(), this.clientId);
    }
}
