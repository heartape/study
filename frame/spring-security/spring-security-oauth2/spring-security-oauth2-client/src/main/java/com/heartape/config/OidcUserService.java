package com.heartape.config;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.util.ArrayList;
import java.util.Collection;

/**
 * 用于修改授权信息
 * <pre>
 * .oauth2Login(oauth2 -> oauth2
 *        .userInfoEndpoint(userInfo -> userInfo
 *                .oidcUserService(this.oidcUserService())
 * 		  )
 * 	)
 * </pre>
 */
public class OidcUserService implements OAuth2UserService<OidcUserRequest, OidcUser> {

    private final OidcUserService delegate = new OidcUserService();

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        // Delegate to the default implementation for loading a user
        OidcUser oidcUser = delegate.loadUser(userRequest);
        // 1) Fetch the authority information from the protected resource using accessToken
        // 2) Map the authority information to one or more GrantedAuthority's and add it to mappedAuthorities
        Collection<? extends GrantedAuthority> authorities = oidcUser.getAuthorities();
        // 3) Create a copy of oidcUser but use the mappedAuthorities instead
        Collection<GrantedAuthority> mappedAuthorities = new ArrayList<>(authorities);
        // todo:添加需要信息
        return new DefaultOidcUser(mappedAuthorities, oidcUser.getIdToken(), oidcUser.getUserInfo());
    }
}
