package dev.jlibra.client.views;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

@JsonTypeInfo(use = Id.NAME, include = As.WRAPPER_OBJECT)
@JsonSubTypes({
        @JsonSubTypes.Type(value = ImmutablePeerToPeerTransactionScript.class, name = "peer_to_peer_transaction"),
        @JsonSubTypes.Type(value = ImmutableUnknownTransactionScript.class, name = "unknown_transaction")
})
public interface Script {

}
