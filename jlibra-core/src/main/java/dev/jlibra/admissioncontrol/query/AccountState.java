package dev.jlibra.admissioncontrol.query;

import org.immutables.value.Value;

@Value.Immutable
public interface AccountState {

    byte[] getAddress();

    long getBalanceInMicroLibras();

    long getReceivedEvents();

    long getSentEvents();

    long getSequenceNumber();

    boolean getDelegatedWithdrawalCapability();

}
