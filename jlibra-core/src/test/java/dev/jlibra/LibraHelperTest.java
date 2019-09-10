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

import dev.jlibra.admissioncontrol.query.AccountData;
import types.AccountStateBlobOuterClass.AccountStateBlob;
import types.AccountStateBlobOuterClass.AccountStateWithProof;
import types.GetWithProof.GetAccountStateResponse;
import types.Transaction.RawTransaction;

public class LibraHelperTest {

    private static final String PRIVATE_KEY_HEX = "3051020101300506032b6570042204202b1115484c64c297179d4ec8aa660f09eeae900a1ba6f16423f82869a101c8e98121002e00f50d1ba024895c72a92cee1310dfafefcc826629c266a4c80b914772f82d";
    private static final String ACCOUNT_STATE_HEX = "010000002100000001217da6c6b3e19f1825cfb2676daecce3bf3de03cf26647c78df00b371b25cc978d000000200000006674633c78e2e00c69fd6e027aa6d1db2abc2a6c80d78a3e129eaf33dd49ce1c306588010000000000030000000000000020000000713683f27b7941f8178a11aa63e84df91f145778e4643916e444412eb6d6b0e5040000000000000020000000071f1ea79b401b3dc196a5814e11b0f52072c7a7d56fcdaa8f9d68f0022905550400000000000000";

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
        List<AccountData> accountStates = LibraHelper
                .readAccountStates(GetAccountStateResponse.newBuilder().setAccountStateWithProof(AccountStateWithProof
                        .newBuilder().setBlob(
                                AccountStateBlob.newBuilder()
                                        .setBlob(ByteString.copyFrom(Hex.decode(ACCOUNT_STATE_HEX.getBytes())))
                                        .build())
                        .build()).build());

        assertThat(accountStates, is(iterableWithSize(1)));
    }
}
