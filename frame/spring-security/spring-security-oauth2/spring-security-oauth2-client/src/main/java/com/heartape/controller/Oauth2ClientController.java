package com.heartape.controller;

import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * /.well-known/openid-configuration
 */
@AllArgsConstructor
@RestController
public class Oauth2ClientController {

    private final ClientRegistrationRepository clientRegistrationRepository;
    private final OAuth2AuthorizedClientService authorizedClientService;

    @GetMapping("/")
    public Map<String, String> index(OAuth2AuthenticationToken authenticationToken, Authentication authentication){
        if (authentication == null){
            return new HashMap<>();
        }
        OAuth2AuthorizedClient oAuth2AuthorizedClient = authorizedClientService.loadAuthorizedClient(authenticationToken.getAuthorizedClientRegistrationId(), authenticationToken.getName());
        // 用于请求resource和server，请求头authorization: Bearer xxx
        String accessToken = oAuth2AuthorizedClient.getAccessToken().getTokenValue();
        OidcUser oidcUser = (OidcUser) authentication.getPrincipal();
        // idToken仅用于client获取用户信息
        String idToken = oidcUser.getIdToken().getTokenValue();
        // this.cache.put(idToken, accessToken);
        return Map.of("accessToken", accessToken, "idToken", idToken);
    }

    @GetMapping("/check")
    public String check(){
        return "success";
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
