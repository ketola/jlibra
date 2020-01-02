package dev.jlibra.admissioncontrol.query;

import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.List;

import org.immutables.value.Value;

import dev.jlibra.admissioncontrol.transaction.Transaction;
import types.Events;

@Value.Immutable
public interface TransactionListWithProof {

    List<Event> getEvents();

    List<Transaction> getTransactions();

    static TransactionListWithProof fromGrpcObject(types.TransactionOuterClass.TransactionListWithProof grpcObject) {

        List<Transaction> transactions = grpcObject.getTransactionsList().stream()
                .map(Transaction::fromGrpcObject)
                .collect(toList());

        List<Event> eventResult = grpcObject.getEventsForVersions().getEventsForVersionList().stream()
                .map(Events.EventsList::getEventsList)
                .map(eventGrpcObjects -> eventGrpcObjects.stream().map(Event::fromGrpcObject).collect(toList()))
                .flatMap(Collection::stream).collect(toList());

        return ImmutableTransactionListWithProof.builder()
                .addAllEvents(eventResult)
                .addAllTransactions(transactions)
                .build();
    }
}
