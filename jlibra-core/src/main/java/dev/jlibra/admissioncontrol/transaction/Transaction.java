package dev.jlibra.admissioncontrol.transaction;

import org.immutables.value.Value;

@Value.Immutable
public interface Transaction {

    public long getSequenceNumber();

    public Program getProgram();

    public long getExpirationTime();

    public long getGasUnitPrice();

    public long getMaxGasAmount();

}
