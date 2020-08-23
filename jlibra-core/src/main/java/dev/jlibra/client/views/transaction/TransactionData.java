package dev.jlibra.client.views.transaction;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

import dev.jlibra.client.views.ImmutableBlockMetadataTransaction;

@JsonTypeInfo(use = Id.NAME, include = As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = ImmutableBlockMetadataTransaction.class, name = "blockmetadata"),
        @JsonSubTypes.Type(value = ImmutableUserTransaction.class, name = "user")
})
public interface TransactionData {

}
