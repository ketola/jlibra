package dev.jlibra.admissioncontrol.query;

import org.immutables.value.Value;
import types.Events;

import java.util.Collection;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Value.Immutable
public interface TransactionListWithProof {
    List<Event> getEvents();

    static TransactionWithProof fromGrpcObject(types.TransactionOuterClass.TransactionListWithProof grpcObject) {
        List<Event> eventResult = grpcObject.getEventsForVersions().getEventsForVersionList().stream()
                .map(Events.EventsList::getEventsList)
                .map(eventGrpcObjects -> eventGrpcObjects.stream().map(Event::fromGrpcObject).collect(toList()))
                .flatMap(Collection::stream).collect(toList());

        return ImmutableTransactionWithProof.builder()
                .addAllEvents(eventResult)
                .build();
    }
}
