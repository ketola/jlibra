package dev.jlibra;

import static org.bouncycastle.util.encoders.Hex.encode;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.iterableWithSize;
import static org.junit.Assert.assertThat;

import java.security.PrivateKey;
import java.security.Security;
import java.util.List;

import org.bouncycastle.util.encoders.Hex;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.protobuf.ByteString;

import types.AccountStateBlobOuterClass.AccountStateBlob;
import types.AccountStateBlobOuterClass.AccountStateWithProof;
import types.Transaction.RawTransaction;

public class LibraHelperTest {

    private static final String PRIVATE_KEY_HEX = "3051020101300506032b6570042204202b1115484c64c297179d4ec8aa660f09eeae900a1ba6f16423f82869a101c8e98121002e00f50d1ba024895c72a92cee1310dfafefcc826629c266a4c80b914772f82d";
    private static final String ACCOUNT_STATE_HEX = "010000002100000001217da6c6b3e19f1825cfb2676daecce3bf3de03cf26647c78df00b371b25cc974400000020000000045d3e63dba85f759d66f9bed4a0e4c262d17f9713f25e846fdae63891837a98208efc20000000000b0000000000000001000000000000000100000000000000";

    @BeforeClass
    public static void setUpClass() {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    }

    @Test
    public void testSignTransaction() {
        PrivateKey privateKey = KeyUtils.privateKeyFromHexString(PRIVATE_KEY_HEX);
        String signature = new String(
                encode(LibraHelper.signTransaction(RawTransaction.newBuilder().build(), privateKey)));

        assertThat(signature, is(
                "68b31901fd58bf7dfe9a66b9c57f40e4d26f4e20dff3a30895cadc9180f85f086c9911caefb70e5670801b96fe6ca1d1a14f456209df981520308389151d840d"));
    }

    @Test
    public void testReadAccountStates() {
        List<AccountState> accountStates = LibraHelper.readAccountStates(AccountStateWithProof.newBuilder().setBlob(
                AccountStateBlob.newBuilder().setBlob(ByteString.copyFrom(Hex.decode(ACCOUNT_STATE_HEX.getBytes())))
                        .build())
                .build());

        assertThat(accountStates, is(iterableWithSize(1)));
        assertThat(new String(encode(accountStates.get(0).getAddress())),
                is("045d3e63dba85f759d66f9bed4a0e4c262d17f9713f25e846fdae63891837a98"));
        assertThat(accountStates.get(0).getBalanceInMicroLibras(), is(553422368L));
        assertThat(accountStates.get(0).getReceivedEvents(), is(11L));
        assertThat(accountStates.get(0).getSentEvents(), is(1L));
        assertThat(accountStates.get(0).getSentEvents(), is(1L));
    }
}
