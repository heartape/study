package com.heartape.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.endpoint.DefaultAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.HttpSessionOAuth2AuthorizationRequestRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.firewall.StrictHttpFirewall;

import java.util.ArrayList;
import java.util.Collection;

/**
 * 第一次oauth2授权时总会出现异常 OAuth2LoginAuthenticationFilter -> authorization_request_not_found
 * 原因：session == null 或 session中不存在org.springframework.security.oauth2.client.web.HttpSessionOAuth2AuthorizationRequestRepository.AUTHORIZATION_REQUEST
 */
@Configuration
@EnableWebSecurity
public class OAuth2ClientSecurityConfig {

    @Bean
    WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web
                .httpFirewall(new StrictHttpFirewall());
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf().disable()
                .authorizeHttpRequests()
                .requestMatchers("/token", "/authorize", "/authorized", "/login/oauth2/**")
                .permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .formLogin(Customizer.withDefaults())
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo
                                .oidcUserService(this.oidcUserService())
			            )
			    )
                // .oauth2Client(withDefaults())
                .oauth2Client(oauth2 -> oauth2
                        .authorizationCodeGrant(codeGrant -> codeGrant
                                .authorizationRequestRepository(this.authorizationRequestRepository())
                                .accessTokenResponseClient(this.accessTokenResponseClient())
                                // 创建认证请求
                                // .authorizationRequestResolver()
                                // 重定向跳转，oauth2-client发送认证请求"/oauth2/authorization"时,根据选择的registration,重定向到对应的认证服务器"/oauth2/authorize"
                                // .authorizationRedirectStrategy()
                        )
                )
                // .logout(logout -> logout.logoutSuccessHandler())
                .build();
    }

    /**
     * 默认oauth2的请求参数以及state等保存在Session中，分布式情况下可能面临Session共享的问题
     */
    private AuthorizationRequestRepository<OAuth2AuthorizationRequest> authorizationRequestRepository(){
        return new HttpSessionOAuth2AuthorizationRequestRepository();
    }

    /**
     * 解析token请求响应
     */
    private OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> accessTokenResponseClient(){
        return new DefaultAuthorizationCodeTokenResponseClient();
    }

    private OAuth2UserService<OidcUserRequest, OidcUser> oidcUserService() {
        final OidcUserService delegate = new OidcUserService();

        return (userRequest) -> {
            // Delegate to the default implementation for loading a user
            OidcUser oidcUser = delegate.loadUser(userRequest);

            // 1) Fetch the authority information from the protected resource using accessToken
            // 2) Map the authority information to one or more GrantedAuthority's and add it to mappedAuthorities
            Collection<? extends GrantedAuthority> authorities = oidcUser.getAuthorities();
            // 3) Create a copy of oidcUser but use the mappedAuthorities instead
            Collection<GrantedAuthority> mappedAuthorities = new ArrayList<>(authorities);
            // todo:添加需要信息

            oidcUser = new DefaultOidcUser(mappedAuthorities, oidcUser.getIdToken(), oidcUser.getUserInfo());

            return oidcUser;
        };
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
