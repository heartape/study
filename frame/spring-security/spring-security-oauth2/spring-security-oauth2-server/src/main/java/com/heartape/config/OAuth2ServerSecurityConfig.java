package com.heartape.config;

import com.heartape.util.SecretKeyUtils;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.core.oidc.endpoint.OidcParameterNames;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.*;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;
import java.util.UUID;

/**
 * <li>OAuth2ClientAuthenticationFilter
 * <li>ClientSecretBasicAuthenticationConverter         -> 从header获取client-id和client-secret
 * <li>FilterChainProxy.VirtualFilterChain.doFilter()   -> 过滤器链
 * <li>OAuth2TokenEndpointFilter                        -> 拦截 /oauth2/token 请求
 */
@Configuration
@EnableWebSecurity
public class OAuth2ServerSecurityConfig {

    /**
     * <li>authorizationEndpoint.consentPage()将自定义授权同意页面，to see
     * <a href="https://github.com/spring-projects/spring-authorization-server/blob/main/samples/custom-consent-authorizationserver/src/main/java/sample/config/AuthorizationServerConfig.java#L65">AuthorizationServerConfig</a>
     * and
     * <a href="https://github.com/spring-projects/spring-authorization-server/blob/main/samples/custom-consent-authorizationserver/src/main/java/sample/web/AuthorizationConsentController.java#L51">AuthorizationConsentController</a>
     *
     * <li>oidc(Customizer.withDefaults())为默认配置，不配置会导致部分请求被重定向至/login
     * @return A Spring Security filter chain for the Protocol Endpoints.
     */
    @Bean
    @Order(1)
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http)
            throws Exception {
        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);

        http.getConfigurer(OAuth2AuthorizationServerConfigurer.class)
                // .authorizationEndpoint(authorizationEndpoint -> authorizationEndpoint
                //         .consentPage("/oauth2/consent")
                // )
                .oidc(oidc -> oidc
                        .userInfoEndpoint(userInfoEndpoint -> userInfoEndpoint
                                .userInfoMapper(new UserInfoMapper())
                        )
                        .clientRegistrationEndpoint(Customizer.withDefaults())
                );
        http
                // Redirect to the login page when not authenticated from the authorization endpoint
                .exceptionHandling((exceptions) -> exceptions
                        .authenticationEntryPoint(
                                new LoginUrlAuthenticationEntryPoint("/login"))
                )
                // Accept access tokens for User Info and/or Client Registration
                .oauth2ResourceServer(oauth2ResourceServerConfigurer -> oauth2ResourceServerConfigurer.jwt(Customizer.withDefaults()));

        return http.build();
    }

    /**
     * 自定义ID_TOKEN、ACCESS_TOKEN
     */
    @Bean
    public OAuth2TokenCustomizer<JwtEncodingContext> tokenCustomizer() {
        return (context) -> {
            if (OidcParameterNames.ID_TOKEN.equals(context.getTokenType().getValue())) {
                context.getClaims().claims(claims -> claims.put("xx", "yy"));
            } else if (OAuth2TokenType.ACCESS_TOKEN.equals(context.getTokenType())) {
                context.getClaims().claims(claims -> claims.put("aa", "bb"));
            }
        };
    }

    /**
     * OidcUserInfoAuthenticationProvider.getClaimsRequestedByScope()
     * @return An instance of Resource Owner save OAuth2AuthorizationConsent(授权信息)
     */
    @Bean
    public OAuth2AuthorizationConsentService authorizationConsentService() {
        return new InMemoryOAuth2AuthorizationConsentService();
    }

    @Bean
    public OAuth2AuthorizationService authorizationService() {
        return new InMemoryOAuth2AuthorizationService();
    }

    /**
     * 可以针对客户端进行配置{@link ClientSettings}、{@link TokenSettings}
     * @return An instance of RegisteredClientRepository for managing clients.
     */
    @Bean
    public RegisteredClientRepository registeredClientRepository() {
        ClientSettings clientSettings = ClientSettings
                .builder()
                .requireAuthorizationConsent(true)
                .build();

        TokenSettings tokenSettings = TokenSettings
                .builder()
                .accessTokenTimeToLive(Duration.ofDays(7))
                .build();

        RegisteredClient registeredClient = RegisteredClient
                .withId(UUID.randomUUID().toString())
                .clientId("111")
                .clientSecret("{noop}222")
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                .redirectUri("http://127.0.0.1:8079/authorized/code")
                .redirectUri("http://127.0.0.1/login/oauth2/code/oauth2-server")
                .redirectUri("http://127.0.0.1:8080/login/oauth2/code/oauth2-server")
                .scope(OidcScopes.OPENID)
                .scope(OidcScopes.PROFILE)
                .scope(OidcScopes.PHONE)
                .scope(OidcScopes.EMAIL)
                .clientSettings(clientSettings)
                .tokenSettings(tokenSettings)
                .build();
        return new InMemoryRegisteredClientRepository(registeredClient);
    }

    @Value("${secret-key.rsa.public-key-pem}")
    private String PUB_KEY_PATH;
    @Value("${secret-key.rsa.private-key-pem}")
    private String PRI_KEY_PATH;

    /**
     * @return An instance of com.nimbusds.jose.jwk.source.JWKSource for signing access tokens.
     */
    @Bean
    public JWKSource<SecurityContext> jwkSource() {
        // 从文件读取，可以使用openssl生成
        RSAPublicKey publicKey = SecretKeyUtils.getPublicKeyFromPem(PUB_KEY_PATH);
        RSAPrivateKey privateKey = SecretKeyUtils.getPrivateKeyFromPem(PRI_KEY_PATH);
        RSAKey rsaKey = new RSAKey
                .Builder(publicKey)
                .privateKey(privateKey)
                // keyID用于关联私钥和公钥
                .keyID("1")
                .build();
        JWKSet jwkSet = new JWKSet(rsaKey);
        return new ImmutableJWKSet<>(jwkSet);
    }

    /**
     * @see org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider
     * @return 	An instance of JwtDecoder for decoding signed access tokens.
     */
    @Bean
    public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
    }

    /**
     * @return 	An instance of AuthorizationServerSettings to configure Spring Authorization Server.
     */
    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings.builder().build();
    }
}
