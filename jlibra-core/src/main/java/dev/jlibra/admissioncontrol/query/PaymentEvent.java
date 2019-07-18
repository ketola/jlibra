package dev.jlibra.admissioncontrol.query;

import org.immutables.value.Value;

@Value.Immutable
public interface PaymentEvent {

    byte[] getAddress();

    long getAmount();

    EventPath getEventPath();

}
