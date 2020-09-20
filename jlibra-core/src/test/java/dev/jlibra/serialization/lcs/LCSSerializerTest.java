package dev.jlibra.serialization.lcs;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import dev.jlibra.AccountAddress;
import dev.jlibra.serialization.ByteArray;
import dev.jlibra.serialization.ByteSequence;
import dev.jlibra.transaction.ChainId;
import dev.jlibra.transaction.ImmutableScript;
import dev.jlibra.transaction.ImmutableTransaction;
import dev.jlibra.transaction.Struct;
import dev.jlibra.transaction.Transaction;
import dev.jlibra.transaction.argument.AccountAddressArgument;

public class LCSSerializerTest {

    @Test
    public void serializeTransaction() {
        Transaction transaction = ImmutableTransaction.builder()
                .expirationTimestampSecs(1L)
                .gasUnitPrice(1)
                .maxGasAmount(2)
                .gasCurrencyCode("LBR")
                .payload(ImmutableScript.builder()
                        .addAllTypeArguments(asList(Struct.typeTagForCurrency("LBR")))
                        .code(ByteArray.from("00"))
                        .arguments(
                                asList(AccountAddressArgument.from(AccountAddress.fromHexString(
                                        "8f5fbb9486acc5fb90f1a6be43a0013d4a7f7f06e3d5fe995be1e9b272c09b5d"))))
                        .build())
                .sender(AccountAddress.fromByteArray(
                        ByteArray
                                .from("4e03aec69589026b4a095c9cd2e53ca6")))
                .sequenceNumber(3)
                .chainId(ChainId.TESTNET)
                .build();

        ByteSequence bytes = LCSSerializer.create().serialize(transaction, Transaction.class);

        assertThat(bytes.toString(), Matchers.is(
                "4e03aec69589026b4a095c9cd2e53ca60300000000000000010100010700000000000000000000000000000001034c4252034c42520001038f5fbb9486acc5fb90f1a6be43a0013d4a7f7f06e3d5fe995be1e9b272c09b5d02000000000000000100000000000000034c4252010000000000000002"));

    }

}
