package dev.jlibra.admissioncontrol.query;

import java.util.List;

import dev.jlibra.AccountState;

public class UpdateToLatestLedgetResult {

    private List<AccountState> accountStates;

    private UpdateToLatestLedgetResult() {
    }

    public static UpdateToLatestLedgetResult create() {
        return new UpdateToLatestLedgetResult();
    }

    public UpdateToLatestLedgetResult withAccountStates(List<AccountState> accountStates) {
        this.accountStates = accountStates;
        return this;
    }

    public List<AccountState> getAccountStates() {
        return accountStates;
    }

}
