package com.heartape.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    public String check(OAuth2AuthenticationToken authentication){
        if (authentication == null){
            return "error";
        }
        // 用于请求认证服务器和资源服务器，请求头authorization: Bearer xxx
        OAuth2AuthorizedClient oAuth2AuthorizedClient = authorizedClientService.loadAuthorizedClient(authentication.getAuthorizedClientRegistrationId(), authentication.getName());
        return oAuth2AuthorizedClient.getAccessToken().getTokenValue();
    }

    @GetMapping("/userinfo")
    public OidcUserInfo userinfo(Authentication authentication){
        OidcUser oidcUser = (OidcUser) authentication.getPrincipal();
        return oidcUser.getUserInfo();
    }

    /**
     * <li>在拥有多个认证选项时需要从中选择一个
     * <a href="http://127.0.0.1:8080/oauth2/authorization/oauth-center">/oauth2/authorization/oauth-center</a>
     * <li>携带选择的认证中心的信息请求授权码
     * <a href="http://127.0.0.1:8888/oauth2/authorize?response_type=authorization_code">/oauth2/authorize?response_type=authorization_code</a>
     * <li>接收授权码默认路径
     * <a href="http://127.0.0.1:8080/login/oauth2/code/{registrationId}">/login/oauth2/code/{registrationId}</a>
     * <li>获取授权码后再获取token
     * <a href="http://localhost:8888/oauth2/token?grant_type=authorization_code&code={}">/oauth2/token?grant_type=authorization_code</a>
     * @param code 授权码
     * @param state 随机验证码，由当前应用发送，并由认证服务器原样返回，用于防止csrf（code被盗用）。
     */
    @GetMapping(value = "/login/oauth2/code/{registrationId}/example")
    public void authorized(@RequestParam String code, @RequestParam String state, @PathVariable String registrationId){

    }
}
