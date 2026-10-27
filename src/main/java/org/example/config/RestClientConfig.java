package org.example.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.time.Duration;

@Configuration
public class RestClientConfig {

    @Bean
    public RestClient userRestClient(
            @Value("${app.user-service.base-url:http://localhost:8081}") String baseUrl) { // Добавили @Value
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofSeconds(2));
        factory.setReadTimeout(Duration.ofSeconds(2));

        return RestClient.builder()
                .baseUrl(baseUrl) // Теперь берется из конфигурации
                .requestFactory(factory)
                .build();
    }
}