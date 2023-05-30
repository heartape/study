package com.heartape.jwt;

import com.heartape.exception.JwtParseException;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jwt.JWTClaimsSet;

import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;

/**
 * 通过公钥对jwt解密
 */
public abstract class AbstractJwtDecoder implements JwtDecoder {

    /**
     * 获取公钥
     */
    protected abstract PublicKey get();

    @Override
    public JWTClaimsSet decode(String token) {
        try {
            JWSObject jwsObject = JWSObject.parse(token);
            JWSVerifier jwsVerifier = new RSASSAVerifier((RSAPublicKey) get());
            if (!jwsObject.verify(jwsVerifier)) {
                throw new JwtParseException();
            }
            return JWTClaimsSet.parse(jwsObject.getPayload().toJSONObject());
        } catch (Exception e){
            throw new JwtParseException("Jwt Parse error");
        }
    }

    @Override
    public void verify(String token) {
        try {
            JWSObject jwsObject = JWSObject.parse(token);
            JWSVerifier jwsVerifier = new RSASSAVerifier((RSAPublicKey) get());
            if (!jwsObject.verify(jwsVerifier)) {
                throw new JwtParseException();
            }
        } catch (Exception e){
            throw new JwtParseException("Jwt Parse error");
        }
    }
}
