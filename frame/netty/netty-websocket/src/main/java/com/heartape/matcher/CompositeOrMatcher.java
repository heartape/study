package com.heartape.matcher;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelMatcher;

public class CompositeOrMatcher implements ChannelMatcher {

    private final ChannelMatcher[] matchers;

    public CompositeOrMatcher(ChannelMatcher... matchers) {
        this.matchers = matchers;
    }

    @Override
    public boolean matches(Channel channel) {
        for (ChannelMatcher m: matchers) {
            if (m.matches(channel)) {
                return true;
            }
        }
        return false;
    }
}
