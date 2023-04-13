package com.heartape.controller;

import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.AbstractOAuth2Token;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

@RestController
public class Oauth2ClientController {

    private final ReactiveOAuth2AuthorizedClientService authorizedClientService;

    public Oauth2ClientController(ReactiveOAuth2AuthorizedClientService authorizedClientService) {
        this.authorizedClientService = authorizedClientService;
    }

    /**
     * <a href="https://oauth.net/id-tokens-vs-access-tokens/">...</a>
     * Here are some further differences between ID tokens and access tokens:
     * <ul>
     *     <li>ID tokens are meant to be read by the OAuth client. Access tokens are meant to be read by the resource server.</li>
     *     <li>ID tokens are JWTs. Access tokens can be JWTs but may also be a random string.</li>
     *     <li>ID tokens should never be sent to an API. Access tokens should never be read by the client.</li>
     * </ul>
     * @param authentication OAuth2AuthenticationToken
     * @return token
     */
    @GetMapping("/")
    public Mono<Map<String, String>> index(OAuth2AuthenticationToken authentication){
        if (authentication == null || !authentication.isAuthenticated()){
            return Mono.empty();
        }
        // accessToken用于请求server和resource，请求头authorization: Bearer xxx
        // idToken用于请求用于请求client，请求头authorization: Bearer xxx
        OidcUser oidcUser = (OidcUser)authentication.getPrincipal();
        OidcIdToken idToken = oidcUser.getIdToken();
        return authorizedClientService
                .loadAuthorizedClient(authentication.getAuthorizedClientRegistrationId(), authentication.getName())
                .map(OAuth2AuthorizedClient::getAccessToken)
                .map(AbstractOAuth2Token::getTokenValue)
                .flux()
                .collectMap(a -> "accessToken", Function.identity())
                .doOnNext(map -> map.put("idToken", idToken.getTokenValue()));
    }
}
