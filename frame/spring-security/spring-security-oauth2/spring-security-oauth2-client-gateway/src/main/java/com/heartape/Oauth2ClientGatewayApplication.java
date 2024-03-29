package com.heartape;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
public class Oauth2ClientGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(Oauth2ClientGatewayApplication.class, args);
    }

}
