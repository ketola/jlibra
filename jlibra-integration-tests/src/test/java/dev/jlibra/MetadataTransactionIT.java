package dev.jlibra;

import static java.time.Instant.now;

import java.nio.charset.StandardCharsets;

import dev.jlibra.admissioncontrol.transaction.AccountAddressArgument;
import dev.jlibra.admissioncontrol.transaction.ByteArrayArgument;
import dev.jlibra.admissioncontrol.transaction.ImmutableScript;
import dev.jlibra.admissioncontrol.transaction.ImmutableTransaction;
import dev.jlibra.admissioncontrol.transaction.Transaction;
import dev.jlibra.admissioncontrol.transaction.U64Argument;
import dev.jlibra.move.Move;
import dev.jlibra.serialization.ByteArray;

public class MetadataTransactionIT extends AbstractTransactionIT {

    @Override
    protected Transaction createTransaction(long sequenceNumber, U64Argument amountArgument,
            AccountAddressArgument addressArgument) {
        return ImmutableTransaction.builder()
                .sequenceNumber(sequenceNumber)
                .maxGasAmount(600000)
                .gasUnitPrice(1)
                .senderAccount(AccountAddress.fromPublicKey(sourceAccount.publicKey))
                .expirationTime(now().getEpochSecond() + 1000)
                .payload(ImmutableScript.builder()
                        .code(Move.peerToPeerTransferWithMetadataAsBytes())
                        .addArguments(addressArgument, amountArgument,
                                new ByteArrayArgument(ByteArray.from("libra".getBytes(StandardCharsets.UTF_8))))
                        .build())
                .build();
    }
}
