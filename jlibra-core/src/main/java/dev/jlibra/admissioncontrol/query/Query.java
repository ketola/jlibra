package dev.jlibra.admissioncontrol.query;

import java.util.List;

import org.immutables.value.Value;

@Value.Immutable
public interface Query {

    List<GetAccountState> getAccountStateQueries();

    List<GetAccountTransactionBySequenceNumber> getAccountTransactionBySequenceNumberQueries();
}
