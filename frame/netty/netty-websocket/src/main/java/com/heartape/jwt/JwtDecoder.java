package com.heartape.jwt;

import com.nimbusds.jwt.JWTClaimsSet;

/**
 * for "decoding" a JSON Web Token (JWT).
 * <p>
 * 其实可以引入安全框架来做，但是考虑到无法和第三方流媒体服务器适配。
 * 并且当前模块仅作为适配器，引入安全框架过于笨重也没有必要。
 */
public interface JwtDecoder {

    /**
     * Decodes the JWT
     */
    JWTClaimsSet decode(String token);

    void verify(String token);
}
