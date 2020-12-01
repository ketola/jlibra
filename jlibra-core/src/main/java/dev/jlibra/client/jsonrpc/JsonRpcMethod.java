package dev.jlibra.client.jsonrpc;

import java.util.List;

import dev.jlibra.client.views.Account;
import dev.jlibra.client.views.BlockMetadata;
import dev.jlibra.client.views.StateProof;
import dev.jlibra.client.views.transaction.Transaction;

public enum JsonRpcMethod {

    GET_ACCOUNT(Account.class, true),
    GET_METADATA(BlockMetadata.class, false),
    GET_TRANSACTIONS(List.class, false),
    GET_ACCOUNT_TRANSACTIONS(List.class, false),
    GET_ACCOUNT_TRANSACTION(Transaction.class, true),
    GET_EVENTS(List.class, false),
    GET_STATE_PROOF(StateProof.class, true),
    GET_CURRENCIES(List.class, false),
    SUBMIT(Void.class, false);

    private Class resultType;
    private boolean optional;

    private JsonRpcMethod(Class resultType, boolean optional) {
        this.resultType = resultType;
        this.optional = optional;
    }

    public Class resultType() {
        return resultType;
    }

    public boolean isOptional() {
        return optional;
    }

}
