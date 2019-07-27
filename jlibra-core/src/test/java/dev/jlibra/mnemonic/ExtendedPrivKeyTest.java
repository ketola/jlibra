package dev.jlibra.mnemonic;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import dev.jlibra.admissioncontrol.transaction.AddressArgument;
import dev.jlibra.admissioncontrol.transaction.U64Argument;
import dev.jlibra.move.Move;
import types.Transaction;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Hex;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.security.Security;
import java.util.List;

/**
 * Test data and mnemonic seed generated using libra cli.
 */
public class ExtendedPrivKeyTest {

    @BeforeClass
    public static void setUpClass() {
        Security.addProvider(new BouncyCastleProvider());
    }

    private ExtendedPrivKey childPrivate0;

    @Before
    public void setUp() {
        Mnemonic mnemonic = Mnemonic.fromString("aim layer grit goat orchard daring lady work dice lottery tent virus push heavy hello endless inner bread cliff brick swallow general method walnut");
        Seed seed = new Seed(mnemonic, "LIBRA");
        LibraKeyFactory libraKeyFactory = new LibraKeyFactory(seed);
        childPrivate0 = libraKeyFactory.privateChild(new ChildNumber(0));
    }

    @Test
    public void getAddress() {
        assertEquals(
                "9263e21488ea4742c54de0d961c94743a01a974c6f095d8710f83044f0408ae7",
                childPrivate0.getAddress()
        );
    }

    @Test
    public void getPublic() {
        assertEquals(
                "be10d382d1f3de00c19607f667b5b127da22f42f0a3a4b70eaef690365421511",
                childPrivate0.publicKey.toString()
        );
    }

    /**
     * From https://github.com/libra/libra/blob/master/client/libra_wallet/src/key_factory.rs:
     *
     * "NOTE: In Libra, we do not sign the raw bytes of a transaction, instead we sign the raw
     *        bytes of the sha3 hash of the raw bytes of a transaction."
     */
    @Test
    public void sign() {
        U64Argument amountArgument = new U64Argument(1_000_000);
        AddressArgument addressArgument = new AddressArgument(Hex.decode(childPrivate0.getAddress()));
        List<Transaction.TransactionArgument> transactionArguments = asList(
                amountArgument.toGrpcTransactionArgument(),
                addressArgument.toGrpcTransactionArgument()
        );

        Transaction.Program program = Transaction.Program.newBuilder()
                .addAllArguments(transactionArguments)
                .setCode(Move.peerToPeerTransfer)
                .addAllModules(emptyList())
                .build();

        Transaction.RawTransaction rawTransaction = Transaction.RawTransaction.newBuilder()
                .setSequenceNumber(0)
                .setMaxGasAmount(6000)
                .setGasUnitPrice(1)
                .setExpirationTime(10000)
                .setProgram(program)
                .build();

        byte[] signature = childPrivate0.sign(rawTransaction);

        assertEquals(
                "3eaf7b596d8231bea5e51695a5ceba98949624fb0da2f46b5274f57416823c48fc73bfaceaecda0797a994889d84f442c12e87ab73554a4cceae3e2ddf149f03",
                Hex.toHexString(signature)
        );
    }

    @Test
    public void signAndVerifyMessageSuccessful() {
        byte[] message = "The things I do for love.".getBytes();
        byte[] signature = childPrivate0.sign(message);

        assertTrue(childPrivate0.verify(signature, message));
    }
}