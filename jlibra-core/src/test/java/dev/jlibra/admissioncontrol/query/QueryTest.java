package dev.jlibra.admissioncontrol.query;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.emptyIterable;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Test;

import dev.jlibra.AccountAddress;
import dev.jlibra.serialization.ByteSequence;
import types.GetWithProof.RequestItem;

public class QueryTest {

    @Test
    public void testAccountStateQueriesToRequestItemsWithNoQueryItems() {
        assertThat(ImmutableQuery.builder().build().toGrpcObject(),
                is(emptyIterable()));
    }

    @Test
    public void testAccountStateQueriesToRequestItems() {
        ByteSequence address1 = ByteSequence.from(new byte[] { 1 });
        ByteSequence address2 = ByteSequence.from(new byte[] { 2 });

        ImmutableQuery query = ImmutableQuery.builder()
                .accountStateQueries(
                        asList(
                                ImmutableGetAccountState.builder()
                                        .address(AccountAddress.ofByteSequence(address1))
                                        .build(),
                                ImmutableGetAccountState.builder()
                                        .address(AccountAddress.ofByteSequence(address2))
                                        .build()))
                .build();

        List<RequestItem> requestItems = query.toGrpcObject();
        assertThat(requestItems, hasSize(2));
        assertThat(requestItems.get(0).getGetAccountStateRequest().getAddress().toByteArray(), is(address1.toArray()));
        assertThat(requestItems.get(1).getGetAccountStateRequest().getAddress().toByteArray(), is(address2.toArray()));
    }

    @Test
    public void testAccountTransactionBySequenceNumberQueriesToRequestItems() {
        ByteSequence address1 = ByteSequence.from(new byte[] { 1 });
        ByteSequence address2 = ByteSequence.from(new byte[] { 2 });

        ImmutableQuery query = ImmutableQuery.builder()
                .accountTransactionBySequenceNumberQueries(
                        asList(
                                ImmutableGetAccountTransactionBySequenceNumber.builder()
                                        .accountAddress(AccountAddress.ofByteSequence(address1))
                                        .sequenceNumber(1)
                                        .fetchEvents(true)
                                        .build(),
                                ImmutableGetAccountTransactionBySequenceNumber.builder()
                                        .accountAddress(AccountAddress.ofByteSequence(address2))
                                        .sequenceNumber(2)
                                        .fetchEvents(false)
                                        .build()))
                .build();

        List<RequestItem> requestItems = query.toGrpcObject();

        assertThat(requestItems, hasSize(2));
        assertThat(requestItems.get(0).getGetAccountTransactionBySequenceNumberRequest().getAccount().toByteArray(),
                is(address1.toArray()));
        assertThat(requestItems.get(0).getGetAccountTransactionBySequenceNumberRequest().getSequenceNumber(), is(1L));
        assertThat(requestItems.get(0).getGetAccountTransactionBySequenceNumberRequest().getFetchEvents(), is(true));
        assertThat(requestItems.get(1).getGetAccountTransactionBySequenceNumberRequest().getAccount().toByteArray(),
                is(address2.toArray()));
        assertThat(requestItems.get(1).getGetAccountTransactionBySequenceNumberRequest().getSequenceNumber(), is(2L));
        assertThat(requestItems.get(1).getGetAccountTransactionBySequenceNumberRequest().getFetchEvents(), is(false));
    }
}
