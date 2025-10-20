package com.quyhoang.flexistudy.configuration;

import org.springframework.context.annotation.Configuration;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;

@Configuration
@Getter
public class PayOSConfig {
    @Value("${payos.client-id}")
    private String clientId;

    @Value("${payos.api-key}")
    private String apiKey;

    @Value("${payos.checksum-key}")
    private String checksumKey;
}