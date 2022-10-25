package com.heartape.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.concurrent.TimeUnit;

/**
 * nacos降级规则配置
 * <pre>
 * [
 *     {
 *         "count":500,
 *         "grade":0,
 *         "resource":"/sentinel/slow",
 *         "timeWindow":20
 *     },
 *     {
 *         "count":5,
 *         "grade":2,
 *         "minRequestAmount":5,
 *         "resource":"/sentinel/error",
 *         "timeWindow":20
 *     }
 * ]
 * </pre>
 */
@RestController
@RequestMapping("/sentinel")
@Slf4j
public class SentinelController {

    @GetMapping("/slow")
    public String slow() throws InterruptedException {
        TimeUnit.SECONDS.sleep(1);
        log.info("test-success");
        return "OK";
    }

    @GetMapping("/error")
    public String error() {
        throw new RuntimeException();
    }

}
