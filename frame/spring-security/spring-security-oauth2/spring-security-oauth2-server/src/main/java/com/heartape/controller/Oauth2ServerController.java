package com.heartape.controller;

import jakarta.annotation.Resource;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.server.authorization.*;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.*;

@RestController
public class Oauth2ServerController {
    @Resource
    private RegisteredClientRepository registeredClientRepository;
    @Resource
    private OAuth2AuthorizationService authorizationService;
    @Resource
    private OAuth2AuthorizationConsentService authorizationConsentService;

    /**
     * 演示了一些基本操作，用于自定义功能
     * @param principal 框架自动注入
     * @param clientId 客户端id
     * @param token accessToken
     */
    @Deprecated
    @GetMapping(value = "/oauth2/example")
    public void consent(Principal principal,
                        @RequestParam(OAuth2ParameterNames.CLIENT_ID) String clientId,
                        @RequestParam String token) {
        RegisteredClient registeredClient = this.registeredClientRepository.findByClientId(clientId);
        if (registeredClient == null){
            return;
        }
        String id = registeredClient.getId();
        OAuth2Authorization authorization = authorizationService.findByToken(token, OAuth2TokenType.ACCESS_TOKEN);
        OAuth2AuthorizationConsent authorizationConsent = this.authorizationConsentService.findById(id, principal.getName());
        if (authorizationConsent != null) {
            Set<String> authorizedScopes = authorizationConsent.getScopes();
            System.out.println(authorizedScopes);
        }
    }
}

