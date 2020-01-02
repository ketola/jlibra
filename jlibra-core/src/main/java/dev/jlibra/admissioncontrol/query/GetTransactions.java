package dev.jlibra.admissioncontrol.query;

import org.immutables.value.Value;
import types.GetWithProof;
import types.GetWithProof.RequestItem;

@Value.Immutable
public abstract class GetTransactions {
    public abstract long getStartVersion();
    public abstract long getLimit();
    public abstract boolean getFetchEvents();

    public RequestItem toGrpcObject() {
        GetWithProof.GetTransactionsRequest getTransactionsRequest = GetWithProof.GetTransactionsRequest.newBuilder()
                .setStartVersion(getStartVersion())
                .setLimit(getLimit())
                .setFetchEvents(getFetchEvents())
                .build();
        return RequestItem.newBuilder()
                .setGetTransactionsRequest(getTransactionsRequest)
                .build();
    }
}
