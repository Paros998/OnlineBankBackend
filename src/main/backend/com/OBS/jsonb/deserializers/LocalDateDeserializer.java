package com.OBS.jsonb.deserializers;

import javax.json.bind.serializer.DeserializationContext;
import javax.json.bind.serializer.JsonbDeserializer;
import javax.json.stream.JsonParser;
import java.lang.reflect.Type;
import java.time.LocalDate;

public class LocalDateDeserializer implements JsonbDeserializer<LocalDate> {
    @Override
    public LocalDate deserialize(JsonParser parser, DeserializationContext ctx, Type rtType) {
        int[] date = new int[3];

        for (int i = 0; i < 3; i++) {
            date[i] = parser.getInt();
        }

        return LocalDate.of(date[0],date[1],date[2]);
    }
}
