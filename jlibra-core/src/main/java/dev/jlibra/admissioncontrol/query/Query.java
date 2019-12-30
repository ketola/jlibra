package dev.jlibra.admissioncontrol.query;

import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.immutables.value.Value;

import types.GetWithProof.RequestItem;

@Value.Immutable
public abstract class Query {

    public abstract Optional<List<GetAccountState>> getAccountStateQueries();

    public abstract Optional<List<GetAccountTransactionBySequenceNumber>> getAccountTransactionBySequenceNumberQueries();

    public abstract Optional<List<GetTransactions>> getTransactions();

    public List<RequestItem> toGrpcObject() {
        Stream<RequestItem> getAccountStateStream = getAccountStateQueries()
                .map(Collection::stream)
                .orElse(Stream.empty())
                .map(GetAccountState::toGrpcObject);
        Stream<RequestItem> getAccountTransactionsStream =getAccountTransactionBySequenceNumberQueries()
                .map(Collection::stream)
                .orElse(Stream.empty())
                .map(GetAccountTransactionBySequenceNumber::toGrpcObject);
        Stream<RequestItem> getTransactions = getTransactions()
                .map(Collection::stream)
                .orElse(Stream.empty())
                .map(GetTransactions::toGrpcObject);

        Stream<RequestItem> totalStream = Stream.concat(
                getAccountStateStream,
                getAccountTransactionsStream);
        totalStream= Stream.concat(totalStream, getTransactions);

        return totalStream.collect(toList());
    }
}
