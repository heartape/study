package com.heartape.producer;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnMissingBean(IdGenerator.class)
public class DefaultIdentifierGenerator implements IdGenerator {
    @Override
    public String generate() {
        // todo
        return "";
    }
}
