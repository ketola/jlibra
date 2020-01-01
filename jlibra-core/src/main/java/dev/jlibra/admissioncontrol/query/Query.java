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
        Stream.Builder<RequestItem> resultBuiler = Stream.builder();

        getAccountStateQueries()
                .map(Collection::stream)
                .orElse(Stream.empty())
                .map(GetAccountState::toGrpcObject)
                .forEach(resultBuiler);

        getAccountTransactionBySequenceNumberQueries()
                .map(Collection::stream)
                .orElse(Stream.empty())
                .map(GetAccountTransactionBySequenceNumber::toGrpcObject)
                .forEach(resultBuiler);

        getTransactions()
                .map(Collection::stream)
                .orElse(Stream.empty())
                .map(GetTransactions::toGrpcObject)
                .forEach(resultBuiler);

        return resultBuiler.build()
                .collect(toList());
    }
}
