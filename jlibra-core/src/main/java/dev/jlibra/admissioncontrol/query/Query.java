package dev.jlibra.admissioncontrol.query;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.immutables.value.Value;

import types.GetWithProof.RequestItem;

@Value.Immutable
public interface Query {

    Optional<List<GetAccountState>> getAccountStateQueries();

    Optional<List<GetAccountTransactionBySequenceNumber>> getAccountTransactionBySequenceNumberQueries();

    default List<RequestItem> toGrpcObject() {
        List<RequestItem> requestItems = new ArrayList<>();

        if (getAccountStateQueries().isPresent()) {
            requestItems.addAll(getAccountStateQueries().get().stream().map(GetAccountState::toGrpcObject)
                    .collect(toList()));
        }

        if (getAccountTransactionBySequenceNumberQueries().isPresent()) {
            requestItems.addAll(getAccountTransactionBySequenceNumberQueries().get().stream()
                    .map(GetAccountTransactionBySequenceNumber::toGrpcObject)
                    .collect(toList()));
        }

        return requestItems;
    }
}
