package dev.jlibra.admissioncontrol.query;

import java.util.Arrays;
import java.util.List;

public class Query {

    private List<GetAccountState> accountStateQueries;
    private List<GetAccountTransactionBySequenceNumber> accountTransactionBySequenceNumberQueries;

    private Query() {
    }

    public static Query create() {
        return new Query();
    }

    public Query forAccountState(GetAccountState accountStateQuery) {
        return forAccountState(Arrays.asList(accountStateQuery));
    }

    public Query forAccountState(List<GetAccountState> accountStateQueries) {
        this.accountStateQueries = accountStateQueries;
        return this;
    }

    public Query forAccountTransactionBySequenceNumber(
            GetAccountTransactionBySequenceNumber accountTransactionBySequenceNumberQuery) {
        return forAccountTransactionBySequenceNumber(Arrays.asList(accountTransactionBySequenceNumberQuery));
    }

    public Query forAccountTransactionBySequenceNumber(
            List<GetAccountTransactionBySequenceNumber> accountTransactionBySequenceNumberQueries) {
        this.accountTransactionBySequenceNumberQueries = accountTransactionBySequenceNumberQueries;
        return this;
    }

    public List<GetAccountState> getAccountStateQueries() {
        return accountStateQueries;
    }

    public List<GetAccountTransactionBySequenceNumber> getAccountTransactionBySequenceNumberQueries() {
        return accountTransactionBySequenceNumberQueries;
    }

}
