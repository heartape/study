package com.heartape.config;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.HttpSessionOAuth2AuthorizationRequestRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.firewall.StrictHttpFirewall;

/**
 * 第一次oauth2授权时总会出现异常 OAuth2LoginAuthenticationFilter -> authorization_request_not_found。
 * 原因：localhost和127.0.0.1是两个不同的地址，导致session丢失。
 * <p>
 * 另外，分布式情况下可能面临负载均衡时Session丢失的问题:
 * <li>当oauth2 client外层的nginx暴露在公网时，可以将需要进行重定向的请求用ip_hash来保证session一致
 * <li>当oauth2 client外层的nginx不在公网时，通过精确匹配location进行负载均衡
 * <li>将session共享（官方），相比复杂的代理配置更简介高效，但需要引入新的中间件
 */
@AllArgsConstructor
@Configuration
@EnableWebSecurity
public class OAuth2ClientSecurityConfig {

    private final ClientRegistrationRepository clientRegistrationRepository;

    @Bean
    WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web
                .httpFirewall(new StrictHttpFirewall());
    }

    /**
     * 如果网关是客户端的话，spring security过滤器在网关之前，所以各微服务的请求需要permitAll()
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf().disable()
                .authorizeHttpRequests()
                .requestMatchers("/", "/authorized", "/login/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin(Customizer.withDefaults())
                .oauth2Login(Customizer.withDefaults())
                .oauth2Client(Customizer.withDefaults())
                .logout(logout -> logout.logoutSuccessHandler(oidcLogoutSuccessHandler()))
                .build();
    }

    /**
     * 默认情况下注销与令牌吊销无关，因为 OAuth 2.1 侧重于授权，而不是身份验证。您必须通过自定义来连接它们。
     * <a href="https://stackoverflow.com/questions/71646341/spring-authorization-server-how-do-you-implement-a-log-out-user-functionalit/71650060#71650060">stackoverflow</a>
     * <a href="https://docs.spring.io/spring-security/reference/servlet/oauth2/login/advanced.html#oauth2login-advanced-oidc-logout">document</a>
     * @return LogoutSuccessHandler
     */
    private LogoutSuccessHandler oidcLogoutSuccessHandler() {
        OidcClientInitiatedLogoutSuccessHandler oidcLogoutSuccessHandler =
                new OidcClientInitiatedLogoutSuccessHandler(this.clientRegistrationRepository);
        // Sets the location that the End-User's User Agent will be redirected to
        // after the logout has been performed at the Provider
        oidcLogoutSuccessHandler.setPostLogoutRedirectUri("{baseUrl}");
        return oidcLogoutSuccessHandler;
    }

    /**
     * 用于管理oauth2认证请求时的status参数
     * <pre>
     *     .oauth2Client(oauth2 -> oauth2
     *                 .authorizationCodeGrant(codeGrant -> codeGrant
     *                         .authorizationRequestRepository(this.authorizationRequestRepository())
     *                 )
     *          )
     * </pre>
     */
    private AuthorizationRequestRepository<OAuth2AuthorizationRequest> authorizationRequestRepository(){
        return new HttpSessionOAuth2AuthorizationRequestRepository();
    }

    @Bean
    OAuth2AuthorizedClientManager authorizedClientManager(ClientRegistrationRepository clientRegistrationRepository,
                                                          OAuth2AuthorizedClientRepository authorizedClientRepository) {

        OAuth2AuthorizedClientProvider authorizedClientProvider = OAuth2AuthorizedClientProviderBuilder
                .builder()
                .authorizationCode()
                .refreshToken()
                .build();
        DefaultOAuth2AuthorizedClientManager authorizedClientManager =
                new DefaultOAuth2AuthorizedClientManager(clientRegistrationRepository, authorizedClientRepository);
        authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider);

        return authorizedClientManager;
    }
}
