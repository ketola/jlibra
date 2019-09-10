package dev.jlibra.admissioncontrol.query;

import java.util.List;

import org.immutables.value.Value;

@Value.Immutable
public interface SignedTransactionWithProof {

    byte[] getSenderPublicKey();

    byte[] getSenderSignature();

    List<Event> getEvents();
}
