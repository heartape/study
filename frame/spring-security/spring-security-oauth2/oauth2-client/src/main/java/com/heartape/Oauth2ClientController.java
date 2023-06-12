package com.heartape;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

@RestController
public class Oauth2ClientController {

    private final RestTemplate restTemplate = new RestTemplateBuilder()
            .additionalMessageConverters(new FormHttpMessageConverter())
            .build();

    @GetMapping(value = "/authorized/code")
    public void authorized(@RequestParam String code) throws URISyntaxException {
        System.out.println(code);
        URI uri = new URI("http://localhost:8888/oauth2/token");
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        Map<String, String> body = Map.of(
                "grant_type", "authorization_code",
                "code", code,
                "client_id", "111",
                "client_secret", "222",
                "redirect_uri", "http://127.0.0.1:8079/authorized/token"
        );
        HttpEntity<Map<String, String>> httpEntity = new HttpEntity<>(new HashMap<>(body), httpHeaders);
        ResponseEntity<Map> responseEntity = restTemplate.exchange(uri, HttpMethod.POST, httpEntity, Map.class);
        Map map = responseEntity.getBody();
        System.out.println(map);
    }

    @GetMapping(value = "/authorized/token")
    public void authorized(@RequestParam Map<String, String> tokenMap){
        System.out.println(tokenMap);
    }
}
