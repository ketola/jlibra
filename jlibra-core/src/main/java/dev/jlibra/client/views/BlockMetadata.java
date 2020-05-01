package dev.jlibra.client.views;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Value.Immutable
@JsonDeserialize(as = ImmutableBlockMetadata.class)
public interface BlockMetadata {

    Long timestamp();

    Long version();
}
