package dev.jlibra.client.views.transaction;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

@JsonTypeInfo(use = Id.NAME, include = As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = ImmutableCreateChildVaspAccountScript.class, name = "create_child_vasp_account"),
        @JsonSubTypes.Type(value = ImmutablePeerToPeerWithMetadataScript.class, name = "peer_to_peer_with_metadata"),
        @JsonSubTypes.Type(value = ImmutableRotateAuthenticationKeyScript.class, name = "rotate_authentication_key"),
        @JsonSubTypes.Type(value = ImmutableRotateDualAttestationInfoScript.class, name = "rotate_dual_attestation_info"),
        @JsonSubTypes.Type(value = ImmutableUnknownTransactionScript.class, name = "unknown_transaction")
})
public interface Script {

    @JsonProperty("code")
    String code();

    @JsonProperty("arguments")
    List<String> arguments();

    @JsonProperty("type_arguments")
    List<String> typeArguments();

}
