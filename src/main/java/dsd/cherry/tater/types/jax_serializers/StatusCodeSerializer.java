package dsd.cherry.tater.types.jax_serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import dsd.cherry.tater.types.ErrorCodes;

import java.io.IOException;

/**
 * Created by James Beach on 5/4/2016.
 */
public class StatusCodeSerializer extends JsonSerializer<ErrorCodes> {
    @Override
    public void serialize(ErrorCodes code, JsonGenerator gen, SerializerProvider provider)
            throws IOException, JsonProcessingException
    {
        gen.writeStartObject();
        gen.writeNumberField("appCode", code.getErrorCode());
        gen.writeStringField("message", code.getMessage());
        gen.writeEndObject();
    }
}
