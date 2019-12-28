package dev.jlibra.admissioncontrol.query;

import static java.util.stream.Collectors.toList;

import java.util.List;

import org.immutables.value.Value;

import dev.jlibra.admissioncontrol.transaction.Transaction;

@Value.Immutable
public interface TransactionWithProof {

    List<Event> getEvents();

    Transaction getTransaction();

    long getVersion();

    static TransactionWithProof fromGrpcObject(types.TransactionOuterClass.TransactionWithProof grpcObject) {
        List<Event> events = grpcObject.getEvents().getEventsList().stream()
                .map(Event::fromGrpcObject)
                .collect(toList());
        Transaction transaction = Transaction.fromGrpcObject(grpcObject.getTransaction());

        return ImmutableTransactionWithProof.builder()
                .addAllEvents(events)
                .version(grpcObject.getVersion())
                .transaction(transaction)
                .build();
    }
}
