package dev.jlibra.example.wait.condition;

import static java.util.stream.Collectors.toList;

import java.util.List;

import dev.jlibra.admissioncontrol.AdmissionControl;
import dev.jlibra.admissioncontrol.query.ImmutableGetAccountTransactionBySequenceNumber;
import dev.jlibra.admissioncontrol.query.ImmutableQuery;
import dev.jlibra.admissioncontrol.query.UpdateToLatestLedgerResult;
import dev.jlibra.admissioncontrol.transaction.Transaction;

public class Transactions {
    public static WaitCondition executed(List<Transaction> transactions, AdmissionControl admissionControl) {
        return () -> {
            UpdateToLatestLedgerResult result = admissionControl
                    .updateToLatestLedger(ImmutableQuery.builder()
                            .accountTransactionBySequenceNumberQueries(transactions.stream()
                                    .map(t -> ImmutableGetAccountTransactionBySequenceNumber.builder()
                                            .accountAddress(t.getSenderAccount())
                                            .sequenceNumber(t.getSequenceNumber())
                                            .fetchEvents(false)
                                            .build())
                                    .collect(toList()))
                            .build());
            return result.getAccountTransactionBySequenceNumberQueryResults().size() == transactions.size();
        };
    }
}
