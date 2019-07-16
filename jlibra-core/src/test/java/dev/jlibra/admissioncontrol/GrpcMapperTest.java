package dev.jlibra.admissioncontrol;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.emptyIterable;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayInputStream;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.util.List;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Hex;
import org.junit.BeforeClass;
import org.junit.Test;

import admission_control.AdmissionControlOuterClass.SubmitTransactionRequest;
import dev.jlibra.KeyUtils;
import dev.jlibra.admissioncontrol.query.GetAccountState;
import dev.jlibra.admissioncontrol.query.GetAccountTransactionBySequenceNumber;
import dev.jlibra.admissioncontrol.transaction.AddressArgument;
import dev.jlibra.admissioncontrol.transaction.Program;
import dev.jlibra.admissioncontrol.transaction.Transaction;
import dev.jlibra.admissioncontrol.transaction.U64Argument;
import types.GetWithProof.RequestItem;
import types.Transaction.RawTransaction;
import types.Transaction.TransactionArgument.ArgType;

public class GrpcMapperTest {

    private static final String PRIVATE_KEY_HEX = "3051020101300506032b6570042204206dadf7a252c0e74add2e545a1e3c811f1f4bdd88f8c5e0080e068f4df6d909128121000b29a7adce0897b2d1ec18cc482237463efa173945fa3bd2703023e1a2489021";
    private static final String PUBLIC_KEY_HEX = "302a300506032b65700321000b29a7adce0897b2d1ec18cc482237463efa173945fa3bd2703023e1a2489021";

    @BeforeClass
    public static void setUpClass() {
        Security.addProvider(new BouncyCastleProvider());
    }

    @Test
    public void testToSubmitTransactionRequest() throws Exception {
        Transaction transaction = Transaction.create()
                .withExpirationTime(10)
                .withGasUnitPrice(1)
                .withMaxGasAmount(6000)
                .withSequenceNumber(1)
                .withProgram(
                        new Program(new ByteArrayInputStream(new byte[] { 1 }),
                                asList(new U64Argument(1000), new AddressArgument(new byte[] { 2 }))));

        PrivateKey privateKey = KeyUtils.privateKeyFromHexString(PRIVATE_KEY_HEX);
        PublicKey publicKey = KeyUtils.publicKeyFromHexString(PUBLIC_KEY_HEX);

        SubmitTransactionRequest request = GrpcMapper.toSubmitTransactionRequest(publicKey, privateKey, transaction);

        RawTransaction rawTransaction = RawTransaction.parseFrom(request.getSignedTxn().getRawTxnBytes());

        // raw transaction
        assertThat(rawTransaction.getExpirationTime(), is(10L));
        assertThat(rawTransaction.getGasUnitPrice(), is(1L));
        assertThat(rawTransaction.getMaxGasAmount(), is(6000L));
        assertThat(rawTransaction.getSequenceNumber(), is(1L));

        // program
        assertThat(rawTransaction.getProgram().getCode().toByteArray(), is(new byte[] { 1 }));
        assertThat(rawTransaction.getProgram().getArgumentsCount(), is(2));
        assertThat(rawTransaction.getProgram().getArgumentsList().get(0).getType(), is(ArgType.U64));
        assertThat(rawTransaction.getProgram().getArgumentsList().get(1).getType(), is(ArgType.ADDRESS));

        // signed transaction
        assertThat(request.getSignedTxn().getSenderPublicKey().toByteArray(),
                is(KeyUtils.stripPublicKeyPrefix(publicKey.getEncoded())));
        assertThat(Hex.toHexString(request.getSignedTxn().getSenderSignature().toByteArray()),
                is(notNullValue()));
    }

    @Test
    public void testAccountStateQueriesToRequestItemsWithNullArgument() {
        assertThat(GrpcMapper.accountStateQueriesToRequestItems(null), is(emptyIterable()));
    }

    @Test
    public void testAccountStateQueriesToRequestItems() {
        byte[] address1 = new byte[] { 1 };
        byte[] address2 = new byte[] { 2 };

        List<RequestItem> requestItems = GrpcMapper
                .accountStateQueriesToRequestItems(
                        asList(new GetAccountState(address1), new GetAccountState(address2)));

        assertThat(requestItems, hasSize(2));
        assertThat(requestItems.get(0).getGetAccountStateRequest().getAddress().toByteArray(), is(address1));
        assertThat(requestItems.get(1).getGetAccountStateRequest().getAddress().toByteArray(), is(address2));
    }

    @Test
    public void testAccountTransactionBySequenceNumberQueriesToRequestItemsWithNullArgument() {
        assertThat(GrpcMapper.accountTransactionBySequenceNumberQueriesToRequestItems(null), is(emptyIterable()));
    }

    @Test
    public void testAccountTransactionBySequenceNumberQueriesToRequestItems() {
        byte[] address1 = new byte[] { 1 };
        byte[] address2 = new byte[] { 2 };

        List<RequestItem> requestItems = GrpcMapper
                .accountTransactionBySequenceNumberQueriesToRequestItems(
                        asList(new GetAccountTransactionBySequenceNumber(address1, 1),
                                new GetAccountTransactionBySequenceNumber(address2, 2)));

        assertThat(requestItems, hasSize(2));
        assertThat(requestItems.get(0).getGetAccountTransactionBySequenceNumberRequest().getAccount().toByteArray(),
                is(address1));
        assertThat(requestItems.get(0).getGetAccountTransactionBySequenceNumberRequest().getSequenceNumber(), is(1L));
        assertThat(requestItems.get(1).getGetAccountTransactionBySequenceNumberRequest().getAccount().toByteArray(),
                is(address2));
        assertThat(requestItems.get(1).getGetAccountTransactionBySequenceNumberRequest().getSequenceNumber(), is(2L));
    }

}
