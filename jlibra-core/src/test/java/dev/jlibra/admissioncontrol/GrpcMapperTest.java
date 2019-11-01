package dev.jlibra.admissioncontrol;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.emptyIterable;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.security.Security;
import java.util.List;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Hex;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.protobuf.ByteString;

import admission_control.AdmissionControlOuterClass.SubmitTransactionRequest;
import dev.jlibra.admissioncontrol.query.ImmutableGetAccountState;
import dev.jlibra.admissioncontrol.query.ImmutableGetAccountTransactionBySequenceNumber;
import dev.jlibra.admissioncontrol.query.UpdateToLatestLedgerResult;
import dev.jlibra.admissioncontrol.transaction.AccountAddressArgument;
import dev.jlibra.admissioncontrol.transaction.ImmutableProgram;
import dev.jlibra.admissioncontrol.transaction.ImmutableSignedTransaction;
import dev.jlibra.admissioncontrol.transaction.ImmutableTransaction;
import dev.jlibra.admissioncontrol.transaction.SignedTransaction;
import dev.jlibra.admissioncontrol.transaction.Transaction;
import dev.jlibra.admissioncontrol.transaction.U64Argument;
import types.GetWithProof;
import types.GetWithProof.RequestItem;

public class GrpcMapperTest {
    @BeforeClass
    public static void setUpClass() {
        Security.addProvider(new BouncyCastleProvider());
    }

    @Test
    public void testToSubmitTransactionRequest() throws Exception {
        Transaction transaction = ImmutableTransaction.builder()
                .expirationTime(10)
                .maxGasAmount(6000)
                .gasUnitPrice(1)
                .sequenceNumber(1)
                .expirationTime(1L)
                .senderAccount(new byte[] { 1 })
                .program(ImmutableProgram.builder()
                        .addArguments(new U64Argument(1000), new AccountAddressArgument(new byte[] { 1 }))
                        .code(ByteString.copyFrom(new byte[] { 1 }))
                        .build())
                .build();

        SignedTransaction signedTransaction = ImmutableSignedTransaction.builder()
                .publicKey(new byte[] { 1 })
                .signature(new byte[] { 1 })
                .transaction(transaction)
                .build();

        SubmitTransactionRequest request = GrpcMapper.toSubmitTransactionRequest(signedTransaction);

        assertThat(Hex.toHexString(request.getTransaction().getTxnBytes().toByteArray()), is(
                "010000000101000000000000000000000001000000010200000000000000e8030000000000000100000001000000010000000070170000000000000100000000000000010000000000000001000000010100000001"));
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
                        asList(ImmutableGetAccountState.builder().address(address1).build(),
                                ImmutableGetAccountState.builder().address(address2).build()));

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
                        asList(ImmutableGetAccountTransactionBySequenceNumber.builder()
                                .accountAddress(address1)
                                .sequenceNumber(1)
                                .build(),
                                ImmutableGetAccountTransactionBySequenceNumber.builder()
                                        .accountAddress(address2)
                                        .sequenceNumber(2)
                                        .build()));

        assertThat(requestItems, hasSize(2));
        assertThat(requestItems.get(0).getGetAccountTransactionBySequenceNumberRequest().getAccount().toByteArray(),
                is(address1));
        assertThat(requestItems.get(0).getGetAccountTransactionBySequenceNumberRequest().getSequenceNumber(), is(1L));
        assertThat(requestItems.get(1).getGetAccountTransactionBySequenceNumberRequest().getAccount().toByteArray(),
                is(address2));
        assertThat(requestItems.get(1).getGetAccountTransactionBySequenceNumberRequest().getSequenceNumber(), is(2L));
    }

    @Test
    public void updateToLatestLedgerResponseToResult() {
        GetWithProof.UpdateToLatestLedgerResponse response = GetWithProof.UpdateToLatestLedgerResponse.newBuilder()
                .addResponseItems(GetWithProof.ResponseItem.newBuilder().build())
                .build();

        UpdateToLatestLedgerResult result = GrpcMapper
                .updateToLatestLedgerResponseToResult(response);

        assertEquals(0, result.getAccountTransactionsBySequenceNumber().size());
    }
}
