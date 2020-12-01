package dev.jlibra.client.views.transaction.vmstatus;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Value.Immutable
@JsonDeserialize(as = ImmutableMoveAbortExplanation.class)
public interface MoveAbortExplanation {

    @JsonProperty("category")
    String category();

    @JsonProperty("category_description")
    String categoryDescription();

    @JsonProperty("reason")
    String reason();

    @JsonProperty("reason_description")
    String reasonDescription();
}
