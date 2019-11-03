package dev.jlibra.admissioncontrol.query;

import static dev.jlibra.serialization.Deserialization.readBytes;
import static dev.jlibra.serialization.Deserialization.readInt;
import static java.util.stream.Collectors.toList;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.util.ArrayList;
import java.util.List;

import org.immutables.value.Value;

import types.GetWithProof.GetAccountStateResponse;
import types.GetWithProof.GetAccountTransactionBySequenceNumberResponse;
import types.GetWithProof.ResponseItem;
import types.GetWithProof.UpdateToLatestLedgerResponse;

@Value.Immutable
public abstract class UpdateToLatestLedgerResult {

    public abstract List<AccountResource> getAccountResources();

    public abstract List<SignedTransactionWithProof> getAccountTransactionsBySequenceNumber();

    public static UpdateToLatestLedgerResult fromGrpcObject(UpdateToLatestLedgerResponse grpcObject) {
        List<AccountResource> accountStates = new ArrayList<>();
        List<SignedTransactionWithProof> accountTransactionsBySequenceNumber = new ArrayList<>();

        for (ResponseItem item : grpcObject.getResponseItemsList()) {
            accountStates.addAll(readAccountStates(item.getGetAccountStateResponse()));
            accountTransactionsBySequenceNumber
                    .add(readSignedTransactionWithProof(
                            item.getGetAccountTransactionBySequenceNumberResponse()));
        }

        return ImmutableUpdateToLatestLedgerResult.builder()
                .accountResources(accountStates)
                .accountTransactionsBySequenceNumber(accountTransactionsBySequenceNumber)
                .build();
    }

    private static List<AccountResource> readAccountStates(GetAccountStateResponse getAccountStateResponse) {
        List<AccountResource> accountResources = new ArrayList<>();

        byte[] blobBytes = getAccountStateResponse.getAccountStateWithProof().getBlob().getBlob().toByteArray();

        DataInputStream in = new DataInputStream(new ByteArrayInputStream(blobBytes));
        int dataSize = readInt(in, 4);

        for (int i = 0; i < dataSize; i++) {
            int keyLength = readInt(in, 4);
            byte[] key = readBytes(in, keyLength);
            int valLength = readInt(in, 4);
            byte[] val = readBytes(in, valLength);
            accountResources.add(AccountResource.deserialize(val));
        }

        return accountResources;
    }

    private static SignedTransactionWithProof readSignedTransactionWithProof(
            GetAccountTransactionBySequenceNumberResponse responseItem) {
        List<Event> events = responseItem.getTransactionWithProof()
                .getEvents().getEventsList().stream()
                .map(Event::deserialize)
                .collect(toList());

        return ImmutableSignedTransactionWithProof.builder()
                .addAllEvents(events)
                .build();
    }

}
