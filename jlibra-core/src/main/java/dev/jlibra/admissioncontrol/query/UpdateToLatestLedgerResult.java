package dev.jlibra.admissioncontrol.query;

import java.util.List;

import org.immutables.value.Value;

@Value.Immutable
public interface UpdateToLatestLedgerResult {

    List<AccountState> getAccountStates();

    List<SignedTransactionWithProof> getAccountTransactionsBySequenceNumber();

}
