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

    public List<RequestItem> toGrpcObject() {
        return Stream.concat(
                getAccountStateQueries()
                        .map(Collection::stream)
                        .orElse(Stream.empty())
                        .map(GetAccountState::toGrpcObject),
                getAccountTransactionBySequenceNumberQueries()
                        .map(Collection::stream)
                        .orElse(Stream.empty())
                        .map(GetAccountTransactionBySequenceNumber::toGrpcObject))
                .collect(toList());
    }
}
