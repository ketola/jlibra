package dev.jlibra.admissioncontrol.query;

import java.util.List;

import dev.jlibra.AccountState;

public class UpdateToLatestLedgerResult {

    private List<AccountState> accountStates;

    private UpdateToLatestLedgerResult() {
    }

    public static UpdateToLatestLedgerResult create() {
        return new UpdateToLatestLedgerResult();
    }

    public UpdateToLatestLedgerResult withAccountStates(List<AccountState> accountStates) {
        this.accountStates = accountStates;
        return this;
    }

    public List<AccountState> getAccountStates() {
        return accountStates;
    }

}
