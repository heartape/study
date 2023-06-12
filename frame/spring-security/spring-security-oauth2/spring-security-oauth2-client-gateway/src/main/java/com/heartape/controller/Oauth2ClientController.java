package com.heartape.controller;

import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.AbstractOAuth2Token;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
public class Oauth2ClientController {

    private final ReactiveOAuth2AuthorizedClientService authorizedClientService;

    public Oauth2ClientController(ReactiveOAuth2AuthorizedClientService authorizedClientService) {
        this.authorizedClientService = authorizedClientService;
    }

    /**
     * <a href="https://oauth.net/id-tokens-vs-access-tokens/">oauth.net</a>
     * Here are some further differences between ID tokens and access tokens:
     * <ul>
     *     <li>ID tokens are meant to be read by the OAuth client. Access tokens are meant to be read by the resource server.</li>
     *     <li>ID tokens are JWTs. Access tokens can be JWTs but may also be a random string.</li>
     *     <li>ID tokens should never be sent to an API. Access tokens should never be read by the client.</li>
     * </ul>
     * ID token
     * <pre>
     * {
     *   "sub": "1111",
     *   "aud": "111",
     *   "azp": "111",
     *   "iss": "http://localhost:8888",
     *   "exp": 1683365398,
     *   "iat": 1683363598,
     *   "nonce": "OzRK8bJ9qwBxKq3sA716RDTMTawtSxEPXLtbaTC5fuc"
     * }
     * </pre>
     * Access token
     * <pre>
     * {
     *   "sub": "1111",
     *   "aud": "111",
     *   "nbf": 1683363598,
     *   "scope": [
     *     "phone",
     *     "openid",
     *     "profile",
     *     "email"
     *   ],
     *   "iss": "http://localhost:8888",
     *   "exp": 1683968398,
     *   "iat": 1683363598
     * }
     * </pre>
     * @param authentication OAuth2AuthenticationToken
     * @return token
     */
    @GetMapping("/")
    public Mono<Map<String, String>> index(OAuth2AuthenticationToken authentication){
        if (authentication == null || !authentication.isAuthenticated()){
            return Mono.empty();
        }
        // accessToken用于请求server和resource，请求头authorization: Bearer xxx
        // idToken仅用于client获取用户信息
        OidcUser oidcUser = (OidcUser)authentication.getPrincipal();
        OidcIdToken idToken = oidcUser.getIdToken();
        return authorizedClientService
                .loadAuthorizedClient(authentication.getAuthorizedClientRegistrationId(), authentication.getName())
                .flux()
                .flatMap(oAuth2AuthorizedClient -> Flux.just(oAuth2AuthorizedClient.getAccessToken(), oAuth2AuthorizedClient.getRefreshToken()))
                .mergeWith(Mono.just(idToken))
                .collectMap(abstractOAuth2Token -> abstractOAuth2Token.getClass().getSimpleName(), AbstractOAuth2Token::getTokenValue);
    }
}
