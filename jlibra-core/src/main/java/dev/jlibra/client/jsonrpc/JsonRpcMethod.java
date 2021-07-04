package dev.jlibra.client.jsonrpc;

import dev.jlibra.client.views.Account;
import dev.jlibra.client.views.CurrencyInfo;
import dev.jlibra.client.views.event.Event;
import dev.jlibra.client.views.BlockMetadata;
import dev.jlibra.client.views.StateProof;
import dev.jlibra.client.views.transaction.Transaction;

public enum JsonRpcMethod {

    GET_ACCOUNT(Account.class, false, true),
    GET_METADATA(BlockMetadata.class, false, false),
    GET_TRANSACTIONS(Transaction.class, true, false),
    GET_ACCOUNT_TRANSACTIONS(Transaction.class, true, false),
    GET_ACCOUNT_TRANSACTION(Transaction.class, false, true),
    GET_EVENTS(Event.class, true, false),
    GET_STATE_PROOF(StateProof.class, false, true),
    GET_CURRENCIES(CurrencyInfo.class, true, false),
    SUBMIT(Void.class, false, false);

    private Class resultType;
    private boolean listResult;
    private boolean optional;

    private JsonRpcMethod(Class resultType, boolean listResult, boolean optional) {
        this.resultType = resultType;
        this.listResult = listResult;
        this.optional = optional;
    }

    public Class resultType() {
        return resultType;
    }

    public boolean isOptional() {
        return optional;
    }
    
    public boolean isListResult() {
    	return listResult;
    }

}
