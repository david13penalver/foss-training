package com.david13penalver.foss_training_api.infrastructure.adapters.out.persistence.jpa.common;

import java.io.IOException;

import com.david13penalver.foss_training_api.domain.model.common.Distance;
import com.david13penalver.foss_training_api.domain.model.common.DistanceUnit;
import com.david13penalver.foss_training_api.domain.model.common.Duration;
import com.david13penalver.foss_training_api.domain.model.common.Pace;
import com.david13penalver.foss_training_api.domain.model.common.Rpe;
import com.david13penalver.foss_training_api.domain.model.common.Weight;
import com.david13penalver.foss_training_api.domain.model.common.WeightUnit;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.module.SimpleModule;

public class DomainValueObjectMapperModule extends SimpleModule {

    public DomainValueObjectMapperModule() {
        super("DomainValueObjectMapperModule");

        addDeserializer(Weight.class, new JsonDeserializer<>() {
            @Override
            public Weight deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
                JsonNode node = p.getCodec().readTree(p);
                double value = node.has("value") ? node.get("value").asDouble() : 0.0;
                String unitStr = node.has("unit") ? node.get("unit").asText() : "KG";
                return new Weight(value, WeightUnit.valueOf(unitStr));
            }
        });

        addDeserializer(Distance.class, new JsonDeserializer<>() {
            @Override
            public Distance deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
                JsonNode node = p.getCodec().readTree(p);
                double value = node.has("value") ? node.get("value").asDouble() : 0.0;
                String unitStr = node.has("unit") ? node.get("unit").asText() : "METERS";
                return new Distance(value, DistanceUnit.valueOf(unitStr));
            }
        });

        addDeserializer(Duration.class, new JsonDeserializer<>() {
            @Override
            public Duration deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
                JsonNode node = p.getCodec().readTree(p);
                int secs = node.has("totalSeconds") ? node.get("totalSeconds").asInt() : (node.isNumber() ? node.asInt() : 0);
                return new Duration(secs);
            }
        });

        addDeserializer(Rpe.class, new JsonDeserializer<>() {
            @Override
            public Rpe deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
                JsonNode node = p.getCodec().readTree(p);
                double value = node.has("value") ? node.get("value").asDouble() : node.asDouble();
                return new Rpe(value);
            }
        });

        addDeserializer(Pace.class, new JsonDeserializer<>() {
            @Override
            public Pace deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
                JsonNode node = p.getCodec().readTree(p);
                int secs = node.has("secondsPerUnit") ? node.get("secondsPerUnit").asInt() : 0;
                String unitStr = node.has("unit") ? node.get("unit").asText() : "METERS";
                return new Pace(secs, DistanceUnit.valueOf(unitStr));
            }
        });
    }
}
