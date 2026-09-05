package com.david13penalver.foss_training_api.infrastructure.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiConfig {

    @Bean
    OpenAPI fossTrainingOpenApi() {
        return new OpenAPI().info(new Info()
                .title("FOSS Training API")
                .description("REST API for exercise catalog, reference data, and training sessions. "
                        + "The OpenAPI document is generated automatically from the running application.")
                .version("0.0.1-SNAPSHOT"));
    }
}