package dev.jlibra.client.views;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

@JsonTypeInfo(use = Id.NAME, include = As.WRAPPER_OBJECT)
@JsonSubTypes({
        @JsonSubTypes.Type(value = ImmutableBlockMetadataTransaction.class, name = "blockmetadata"),
        @JsonSubTypes.Type(value = ImmutableUserTransaction.class, name = "user")
})
public interface TransactionData {

}
