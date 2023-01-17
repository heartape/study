package com.heartape.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.web.SecurityFilterChain;

/**
 * <li>通过jwk-set-uri配置，资源服务器会在启动时从目标地址（一般是认证服务器）获取公钥，
 * to see {@link org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerJwtConfiguration}
 */
@Configuration
@EnableWebSecurity
public class OAuth2ResourceConfig {

    /**
     * 在权限配置中，官方不建议使用role来进行鉴权，hasAuthority()可以满足要求，需要的话在认证服务器自己实现。
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/email").hasAuthority("SCOPE_email")
                        .requestMatchers("/phone").hasAuthority("SCOPE_phone")
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt);
        return http.build();
    }
}
