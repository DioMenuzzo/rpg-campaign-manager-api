package com.rpgmanager.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI rpgManagerOpenApi() {
        return new OpenAPI().info(new Info()
                .title("RPG Campaign Manager API")
                .version("v1")
                .description("Gerenciamento de campanhas de RPG de mesa: campanhas, personagens, NPCs e sessões."));
    }
}
