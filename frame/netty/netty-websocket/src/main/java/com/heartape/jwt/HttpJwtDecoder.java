package com.heartape.jwt;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.heartape.exception.KeySourceException;
import org.springframework.http.*;
import org.springframework.web.client.RestOperations;

import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Map;

/**
 * 通过http向认证服务器请求公钥
 */
public class HttpJwtDecoder extends AbstractJwtDecoder {

    public final static int FIRST = -1;

    private final RestOperations restOperations;

    private final URL url;

    /**
     * <ul>
     *     <li>-1:仅第一次获取</li>
     *     <li>0:每次都获取</li>
     *     <li>大于0表示间隔的毫秒数</li>
     * </ul>
     */
    private final int strategy;

    public HttpJwtDecoder(RestOperations restOperations, URL url, int strategy) {
        this.restOperations = restOperations;
        this.url = url;
        this.strategy = strategy;
        if (strategy < -1) {
            throw new RuntimeException();
        }
        if (strategy == -1) {
            this.cache = doGet();
        }
        if (strategy > 0){
            this.timestamp = System.currentTimeMillis();
        }
    }

    /**
     * 上一次获取的时间戳
     */
    private volatile long timestamp;

    private volatile PublicKey cache;

    private static final MediaType APPLICATION_JWK_SET_JSON = new MediaType("application", "jwk-set+json");

    @Override
    protected PublicKey get() {
        if (this.strategy == -1){
            return this.cache;
        }
        long l = System.currentTimeMillis();
        if (this.strategy > 0){
            if (l < this.timestamp + this.strategy){
                return this.cache;
            }
            this.timestamp = l;
        }
        PublicKey publicKey = doGet();
        // strategy == 0无需缓存
        if (this.strategy > 0){
            this.cache = publicKey;
        }
        return publicKey;
    }

    private PublicKey doGet(){
        URI uri;
        try {
            uri = this.url.toURI();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON, APPLICATION_JWK_SET_JSON));
        RequestEntity<Void> request = new RequestEntity<>(headers, HttpMethod.GET, uri);
        ResponseEntity<String> response = this.restOperations.exchange(request, String.class);
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new KeySourceException(response.toString());
        }
        String body = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Map<String, List<Map<String, String>>> bodyMap = objectMapper.readValue(body, new TypeReference<>() {});
            List<Map<String, String>> keys = bodyMap.get("keys");
            for (Map<String, String> key : keys) {
                if ("RSA".equals(key.get("kty"))){
                    byte[] eByte = Base64.getDecoder().decode(key.get("e"));
                    byte[] nByte = Base64.getUrlDecoder().decode(key.get("n"));
                    BigInteger e = new BigInteger(1, eByte);
                    BigInteger n = new BigInteger(1, nByte);
                    RSAPublicKeySpec rsaPublicKeySpec = new RSAPublicKeySpec(n, e);
                    return KeyFactory.getInstance("RSA").generatePublic(rsaPublicKeySpec);
                }
            }
        } catch (Exception e){
            throw new KeySourceException("can not find public key!");
        }
        throw new KeySourceException("can not find public key!");
    }
}
