package com.moodleV2.Academia.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("Academia API")
                        .version("1.0")
                        .description("API for managing Professors, Students and Courses"))
                        .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                        .components(new io.swagger.v3.oas.models.Components()
                                .addSecuritySchemes("Bearer Authentication",
                                        new SecurityScheme()
                                                .name("Authorization")
                                                .type(SecurityScheme.Type.HTTP)
                                                .scheme("bearer")
                                                .bearerFormat("JWT")));
    }
}

