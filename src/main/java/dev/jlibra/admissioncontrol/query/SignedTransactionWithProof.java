package dev.jlibra.admissioncontrol.query;

import java.util.List;

public class SignedTransactionWithProof {
    private byte[] senderPublicKey;
    private byte[] senderSignature;
    private List<PaymentEvent> events;

    public SignedTransactionWithProof(byte[] senderPublicKey, byte[] senderSignature, List<PaymentEvent> events) {
        this.senderPublicKey = senderPublicKey;
        this.senderSignature = senderSignature;
        this.events = events;
    }

    public byte[] getSenderPublicKey() {
        return senderPublicKey;
    }

    public byte[] getSenderSignature() {
        return senderSignature;
    }

    public List<PaymentEvent> getEvents() {
        return events;
    }

}
