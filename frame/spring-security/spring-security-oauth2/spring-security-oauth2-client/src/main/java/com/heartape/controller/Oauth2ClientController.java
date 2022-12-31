package com.heartape.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * /.well-known/openid-configuration
 */
@RestController
public class Oauth2ClientController {

    private final ClientRegistrationRepository clientRegistrationRepository;
    private final OAuth2AuthorizedClientService authorizedClientService;

    public Oauth2ClientController(ClientRegistrationRepository clientRegistrationRepository, OAuth2AuthorizedClientService authorizedClientService) {
        this.clientRegistrationRepository = clientRegistrationRepository;
        this.authorizedClientService = authorizedClientService;
    }

    @GetMapping("/")
    public String check(HttpServletRequest request){
        return "success";
    }

    @GetMapping("/token")
    public void token(Authentication authentication){
        OidcUser oidcUser = (OidcUser) authentication.getPrincipal();
        OidcIdToken oidcIdToken = oidcUser.getIdToken();
        String tokenValue = oidcIdToken.getTokenValue();
        System.out.println(tokenValue);

        // 用于请求认证服务器和资源服务器，请求头authorization: Bearer xxx
        OAuth2AuthorizedClient oAuth2AuthorizedClient = authorizedClientService.loadAuthorizedClient("oauth-center", authentication.getName());
        System.out.println(oAuth2AuthorizedClient.getAccessToken().getTokenValue());
    }

    @GetMapping("/userinfo")
    public OidcUserInfo userinfo(Authentication authentication){
        OidcUser oidcUser = (OidcUser) authentication.getPrincipal();
        return oidcUser.getUserInfo();
    }

    /**
     * 在拥有多个认证选项时需要从中选择一个
     * <a href="http://127.0.0.1:8080/authorize?grant_type=authorization_code">...</a>
     * @param authorizedClient 认证选项
     */
    @GetMapping(value = "/authorize", params = "grant_type=authorization_code")
    public void authorizationCodeGrant(@RegisteredOAuth2AuthorizedClient("oauth-center")
                                       OAuth2AuthorizedClient authorizedClient) {
        ClientRegistration clientRegistration = clientRegistrationRepository.findByRegistrationId("oauth-center");
        System.out.println(authorizedClient.getPrincipalName());
        System.out.println(authorizedClient.getClientRegistration());
        System.out.println(authorizedClient.getAccessToken());
    }

    /**
     * 自定义获取授权码，获取授权码后再手动获取token
     * <a href="http://localhost:8888/oauth2/token?grant_type=authorization_code&code={}">...</a>
     * 如无特殊需求使用框架自带路径即可 <a href="/login/oauth2/code/{registrationId}">...</a>
     * @param code 授权码
     * @param state 随机验证码，由当前应用发送，并由认证服务器原样返回，用于防止csrf（code被盗用）。
     */
    @GetMapping(value = "/authorized")
    public void authorized(@RequestParam String code, @RequestParam String state){
        System.out.println(code);
        System.out.println(state);
    }
}
