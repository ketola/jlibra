package dev.jlibra;

import dev.jlibra.admissioncontrol.transaction.*;
import dev.jlibra.move.Move;
import dev.jlibra.serialization.ByteSequence;

import java.nio.charset.StandardCharsets;

import static java.time.Instant.now;

public class SimpleTransactionIT extends AbstractTransactionIT {

    @Override
    protected Transaction createTransaction(long sequenceNumber, U64Argument amountArgument, AccountAddressArgument addressArgument) {
        return ImmutableTransaction.builder()
                    .sequenceNumber(sequenceNumber)
                    .maxGasAmount(600000)
                    .gasUnitPrice(1)
                    .senderAccount(AccountAddress.ofPublicKey(sourceAccount.publicKey))
                    .expirationTime(now().getEpochSecond() + 1000)
                    .payload(ImmutableScript.builder()
                            .code(Move.peerToPeerTransferAsBytes())
                            .addArguments(addressArgument, amountArgument)
                            .build())
                    .build();
        }
}
