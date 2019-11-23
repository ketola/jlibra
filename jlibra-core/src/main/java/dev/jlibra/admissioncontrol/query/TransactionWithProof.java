package dev.jlibra.admissioncontrol.query;

import static java.util.stream.Collectors.toList;

import java.util.List;

import org.immutables.value.Value;

@Value.Immutable
public interface TransactionWithProof {
    List<Event> getEvents();

    static TransactionWithProof deserialize(types.TransactionOuterClass.TransactionWithProof grpcObject) {
        List<Event> events = grpcObject.getEvents().getEventsList().stream()
                .map(Event::deserialize)
                .collect(toList());

        return ImmutableTransactionWithProof.builder()
                .addAllEvents(events)
                .build();
    }
}
