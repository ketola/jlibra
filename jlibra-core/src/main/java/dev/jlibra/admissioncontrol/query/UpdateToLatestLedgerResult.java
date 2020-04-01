package dev.jlibra.admissioncontrol.query;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;

import org.immutables.value.Value;

import types.AccountStateBlobOuterClass.AccountStateWithProof;
import types.Events.EventWithProof;
import types.GetWithProof.ResponseItem;
import types.GetWithProof.UpdateToLatestLedgerResponse;

@Value.Immutable
public abstract class UpdateToLatestLedgerResult {

    public abstract List<AccountResource> getAccountStateQueryResults();

    public abstract List<TransactionWithProof> getAccountTransactionBySequenceNumberQueryResults();

    public abstract List<TransactionListWithProof> getTransactionsQueryResults();

    public abstract List<Event> getEventsByEventAccessPathQueryResults();

    public static UpdateToLatestLedgerResult fromGrpcObject(UpdateToLatestLedgerResponse grpcObject) {
        List<AccountResource> accountStates = new ArrayList<>();
        List<TransactionWithProof> accountTransactionsBySequenceNumber = new ArrayList<>();
        List<TransactionListWithProof> transactions = new ArrayList<>();
        List<Event> eventsByAccessPath = new ArrayList<>();

        for (ResponseItem item : grpcObject.getResponseItemsList()) {
            AccountStateWithProof accountStateWithProof = item.getGetAccountStateResponse().getAccountStateWithProof();
            if (accountStateWithProof.hasBlob()) {
                accountStates.add(AccountResource.fromGrpcObject(accountStateWithProof));
            }

            types.TransactionOuterClass.TransactionWithProof transactionWithProof = item
                    .getGetAccountTransactionBySequenceNumberResponse().getTransactionWithProof();
            if (transactionWithProof.hasProof()) {
                accountTransactionsBySequenceNumber
                        .add(TransactionWithProof.fromGrpcObject(
                                transactionWithProof));
            }

            types.TransactionOuterClass.TransactionListWithProof txnListWithProof = item.getGetTransactionsResponse()
                    .getTxnListWithProof();
            if (txnListWithProof.hasProof()) {
                transactions.add(TransactionListWithProof.fromGrpcObject(
                        txnListWithProof));
            }

            List<EventWithProof> events = item.getGetEventsByEventAccessPathResponse().getEventsWithProofList();
            if (events != null && events.size() > 0) {
                eventsByAccessPath.addAll(
                        events.stream()
                                .map(EventWithProof::getEvent)
                                .map(Event::fromGrpcObject)
                                .collect(toList()));
            }
        }

        return ImmutableUpdateToLatestLedgerResult.builder()
                .accountStateQueryResults(accountStates)
                .accountTransactionBySequenceNumberQueryResults(accountTransactionsBySequenceNumber)
                .transactionsQueryResults(transactions)
                .eventsByEventAccessPathQueryResults(eventsByAccessPath)
                .build();
    }
}
