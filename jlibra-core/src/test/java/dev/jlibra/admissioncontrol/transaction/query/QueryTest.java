package dev.jlibra.admissioncontrol.transaction.query;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.emptyIterable;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Test;

import dev.jlibra.admissioncontrol.query.ImmutableGetAccountState;
import dev.jlibra.admissioncontrol.query.ImmutableGetAccountTransactionBySequenceNumber;
import dev.jlibra.admissioncontrol.query.ImmutableQuery;
import types.GetWithProof.RequestItem;

public class QueryTest {

    @Test
    public void testAccountStateQueriesToRequestItemsWithNoQueryItems() {
        assertThat(ImmutableQuery.builder().build().toGrpcObject(),
                is(emptyIterable()));
    }

    @Test
    public void testAccountStateQueriesToRequestItems() {
        byte[] address1 = new byte[] { 1 };
        byte[] address2 = new byte[] { 2 };

        ImmutableQuery query = ImmutableQuery.builder()
                .accountStateQueries(
                        asList(
                                ImmutableGetAccountState.builder()
                                        .address(address1)
                                        .build(),
                                ImmutableGetAccountState.builder()
                                        .address(address2)
                                        .build()))
                .build();

        List<RequestItem> requestItems = query.toGrpcObject();
        assertThat(requestItems, hasSize(2));
        assertThat(requestItems.get(0).getGetAccountStateRequest().getAddress().toByteArray(), is(address1));
        assertThat(requestItems.get(1).getGetAccountStateRequest().getAddress().toByteArray(), is(address2));
    }

    @Test
    public void testAccountTransactionBySequenceNumberQueriesToRequestItems() {
        byte[] address1 = new byte[] { 1 };
        byte[] address2 = new byte[] { 2 };

        ImmutableQuery query = ImmutableQuery.builder()
                .accountTransactionBySequenceNumberQueries(
                        asList(
                                ImmutableGetAccountTransactionBySequenceNumber.builder()
                                        .accountAddress(address1)
                                        .sequenceNumber(1)
                                        .build(),
                                ImmutableGetAccountTransactionBySequenceNumber.builder()
                                        .accountAddress(address2)
                                        .sequenceNumber(2)
                                        .build()))
                .build();

        List<RequestItem> requestItems = query.toGrpcObject();

        assertThat(requestItems, hasSize(2));
        assertThat(requestItems.get(0).getGetAccountTransactionBySequenceNumberRequest().getAccount().toByteArray(),
                is(address1));
        assertThat(requestItems.get(0).getGetAccountTransactionBySequenceNumberRequest().getSequenceNumber(), is(1L));
        assertThat(requestItems.get(1).getGetAccountTransactionBySequenceNumberRequest().getAccount().toByteArray(),
                is(address2));
        assertThat(requestItems.get(1).getGetAccountTransactionBySequenceNumberRequest().getSequenceNumber(), is(2L));
    }
}
