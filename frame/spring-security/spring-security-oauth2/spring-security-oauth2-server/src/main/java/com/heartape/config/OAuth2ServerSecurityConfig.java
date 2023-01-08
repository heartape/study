package com.heartape.config;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.*;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.oidc.authentication.OidcUserInfoAuthenticationContext;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * OAuth2ClientAuthenticationFilter
 * ClientSecretBasicAuthenticationConverter         -> 从header获取client-id和client-secret
 * FilterChainProxy.VirtualFilterChain.doFilter()   -> 过滤器链
 * OAuth2TokenEndpointFilter                        -> 拦截 /oauth2/token 请求
 */
@Configuration
@EnableWebSecurity
public class OAuth2ServerSecurityConfig {

    /**
     * @return A Spring Security filter chain for the Protocol Endpoints.
     */
    @Bean
    @Order(1)
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http)
            throws Exception {
        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);

        // Custom User Info Mapper that retrieves claims from a signed JWT
        Function<OidcUserInfoAuthenticationContext, OidcUserInfo> userInfoMapper = context -> {
            OAuth2Authorization oAuth2Authorization = context.get(OAuth2Authorization.class);
            String principalName = oAuth2Authorization.getPrincipalName();
            Set<String> authorizedScopes = oAuth2Authorization.getAuthorizedScopes();
            // todo:通过authorizedScopes筛选
            return oidcUserInfoMap.get(principalName);
        };

        http.getConfigurer(OAuth2AuthorizationServerConfigurer.class)
                .oidc(oidc -> oidc
                        .userInfoEndpoint(userinfo -> userinfo
                                .userInfoMapper(userInfoMapper)
                        )
                )
                .and()
                // Redirect to the login page when not authenticated from the
                // authorization endpoint
                .exceptionHandling((exceptions) -> exceptions
                        .authenticationEntryPoint(
                                new LoginUrlAuthenticationEntryPoint("/login"))
                )
                // Accept access tokens for User Info and/or Client Registration
                .oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt);

        return http.build();
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

    private final Map<String, OidcUserInfo> oidcUserInfoMap =  createUserInfoMap();

    private static Map<String, OidcUserInfo> createUserInfoMap() {
        Map<String, OidcUserInfo> oidcUserInfoMap =  new ConcurrentHashMap<>();
        OidcUserInfo oidcUserInfo = OidcUserInfo.builder()
                .subject("1111")
                .name("heartape")
                .givenName("First")
                .familyName("Last")
                .middleName("Middle")
                .nickname("joker")
                .preferredUsername("user")
                .profile("https://blog.heartape.com/profile")
                .picture("https://blog.heartape.com/picture")
                .website("https://blog.heartape.com")
                .email("heartape@example.com")
                .emailVerified(true)
                .gender("female")
                .birthdate("1970-01-01")
                .zoneinfo("Europe/Paris")
                .locale("en-US")
                .phoneNumber("+1 (604) 555-1234;ext=5678")
                .phoneNumberVerified(false)
                .claim("address", Collections.singletonMap("formatted", "Champ de Mars\n5 Av. Anatole France\n75007 Paris\nFrance"))
                .updatedAt("1970-01-01T00:00:00Z")
                .build();
        oidcUserInfoMap.put(oidcUserInfo.getSubject(), oidcUserInfo);
        return oidcUserInfoMap;
    }

    /**
     * @return An instance of RegisteredClientRepository for managing clients.
     */
    @Bean
    public RegisteredClientRepository registeredClientRepository() {
        RegisteredClient registeredClient = RegisteredClient
                .withId(UUID.randomUUID().toString())
                .clientId("111")
                .clientSecret("{noop}222")
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                .redirectUri("http://192.168.31.2:8080/login/oauth2/code/oauth-center")
                .redirectUri("http://192.168.31.2:8080/authorized")
                .scope(OidcScopes.OPENID)
                .scope(OidcScopes.PROFILE)
                .scope(OidcScopes.PHONE)
                .scope(OidcScopes.EMAIL)
                .scope("message.read")
                .clientSettings(ClientSettings.builder().requireAuthorizationConsent(true).build())
                .build();
        return new InMemoryRegisteredClientRepository(registeredClient);
    }

    /**
     * @return An instance of com.nimbusds.jose.jwk.source.JWKSource for signing access tokens.
     */
    @Bean
    public JWKSource<SecurityContext> jwkSource() {
        KeyPair keyPair = generateRsaKey();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        RSAKey rsaKey = new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID(UUID.randomUUID().toString())
                .build();
        JWKSet jwkSet = new JWKSet(rsaKey);
        return new ImmutableJWKSet<>(jwkSet);
    }

    /**
     * @return An instance of java.security.KeyPair with keys generated on startup used to create the JWKSource above.
     */
    private static KeyPair generateRsaKey() {
        KeyPair keyPair;
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            keyPair = keyPairGenerator.generateKeyPair();
        }
        catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
        return keyPair;
    }

    /**
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
