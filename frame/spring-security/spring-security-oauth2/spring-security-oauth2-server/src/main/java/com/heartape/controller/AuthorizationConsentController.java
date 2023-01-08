package com.heartape.controller;

import jakarta.annotation.Resource;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.*;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.*;

@RestController
public class AuthorizationConsentController {
    @Resource
    private RegisteredClientRepository registeredClientRepository;
    @Resource
    private OAuth2AuthorizationService authorizationService;
    @Resource
    private OAuth2AuthorizationConsentService authorizationConsentService;

    @GetMapping(value = "/oauth2/consent")
    public void consent(Principal principal,
                        @RequestParam(OAuth2ParameterNames.CLIENT_ID) String clientId,
                        @RequestHeader String token) {
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

    public static class ScopeWithDescription {
        private static final String DEFAULT_DESCRIPTION = "UNKNOWN SCOPE - We cannot provide information about this permission, use caution when granting this.";
        private static final Map<String, String> scopeDescriptions = new HashMap<>();
        static {
            scopeDescriptions.put(
                    OidcScopes.PROFILE,
                    "This application will be able to read your profile information."
            );
            scopeDescriptions.put(
                    "message.read",
                    "This application will be able to read your message."
            );
            scopeDescriptions.put(
                    "message.write",
                    "This application will be able to add new messages. It will also be able to edit and delete existing messages."
            );
            scopeDescriptions.put(
                    "other.scope",
                    "This is another scope example of a scope description."
            );
        }

        public final String scope;
        public final String description;

        ScopeWithDescription(String scope) {
            this.scope = scope;
            this.description = scopeDescriptions.getOrDefault(scope, DEFAULT_DESCRIPTION);
        }
    }
}

