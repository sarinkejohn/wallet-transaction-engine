package com.sarinkejohn.minipaymentengine.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Mini Payment Charge & Wallet Transaction Engine API")
                        .description("A robust API for managing customer wallets, dynamic charge rules, " +
                                "and idempotent payment transactions. Supports concurrent request protection " +
                                "via Redis distributed locking.")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("sarinkejohn")
                                .email("dev@sarinkejohn.com"))
                        .license(new License()
                                .name("MIT License")))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Local Development"),
                        new Server().url("http://app:8080").description("Docker Network")
                ));
    }
}
