package info.nemoworks.udo.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.saasquatch.jsonschemainferrer.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Udo extends Identifiable {

    private UdoType type;

    private JsonElement data;

    public Udo(JsonElement data) throws JsonProcessingException {
        super();
        this.data = data;
        this.type = this.inferType();
    }

    public Udo() {

    }

    public Udo(UdoType type, JsonElement data) {
        super();
        this.type = type;
        this.data = data;
    }

    @Override
    public JsonObject toJsonObject() {
        Gson gson = new Gson();
        JsonElement jsonElement = gson.toJsonTree(this);
        return (JsonObject) jsonElement;
    }

    public UdoType inferType() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String jsonStr = new Gson().toJson(data);
        String typeStr = " ";
        JsonSchemaInferrer inferrer = JsonSchemaInferrer.newBuilder()
                .setSpecVersion(SpecVersion.DRAFT_06)
                // Requires commons-validator
//                .addFormatInferrers(FormatInferrers.email(), FormatInferrers.ip(), FormatInferrers.noOp())
                .setAdditionalPropertiesPolicy(AdditionalPropertiesPolicies.notAllowed())
                .setRequiredPolicy(RequiredPolicies.nonNullCommonFields())
                .addEnumExtractors(EnumExtractors.validEnum(java.time.Month.class),
                        EnumExtractors.validEnum(java.time.DayOfWeek.class))
                .build();
        JsonNode jsonNode = mapper.readTree(jsonStr);
//        System.out.println("data: " + jsonStr);
//        System.out.println("node: " + jsonNode.toPrettyString());
        JsonNode res = inferrer.inferForSample(jsonNode);
        typeStr = res.toPrettyString();
        JsonObject jsonObject = new Gson().fromJson(typeStr, JsonObject.class);
        UdoType udoType = new UdoType(jsonObject);
        udoType.setId(this.getId() + "type");
        return udoType;
    }
}
