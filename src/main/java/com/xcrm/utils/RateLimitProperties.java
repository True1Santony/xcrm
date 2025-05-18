package com.xcrm.utils;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "rate.limit")
public class RateLimitProperties {
    private List<String> routes;
    private int maxAttempts;
    private int refillDuration;
}
