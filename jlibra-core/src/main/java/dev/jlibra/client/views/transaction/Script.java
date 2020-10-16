package dev.jlibra.client.views.transaction;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

@JsonTypeInfo(use = Id.NAME, include = As.PROPERTY, property = "type", defaultImpl = DefaultScript.class)
@JsonSubTypes({
        @JsonSubTypes.Type(value = ImmutablePeerToPeerWithMetadataScript.class, name = "peer_to_peer_with_metadata")
})
public interface Script {

    @JsonProperty("code")
    String code();

    @JsonProperty("arguments")
    List<String> arguments();

    @JsonProperty("type_arguments")
    List<String> typeArguments();

}
