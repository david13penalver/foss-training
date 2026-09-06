package com.david13penalver.foss_training_api.infrastructure.configuration;

import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import tools.jackson.databind.cfg.EnumFeature;

@Configuration
public class JacksonConfig {

    @Bean
    public JsonMapperBuilderCustomizer jsonCustomizer() {
        return builder -> {
            builder.disable(EnumFeature.READ_ENUMS_USING_TO_STRING);
            builder.disable(EnumFeature.WRITE_ENUMS_USING_TO_STRING);
        };
    }
}
