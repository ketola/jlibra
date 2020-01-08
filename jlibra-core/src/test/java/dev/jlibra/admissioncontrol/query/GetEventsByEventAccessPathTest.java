package dev.jlibra.admissioncontrol.query;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.bouncycastle.util.encoders.Hex;
import org.junit.Test;

import dev.jlibra.AccountAddress;
import dev.jlibra.admissioncontrol.query.GetEventsByEventAccessPath.Path;
import dev.jlibra.serialization.ByteSequence;
import types.GetWithProof.GetEventsByEventAccessPathRequest;

public class GetEventsByEventAccessPathTest {

    @Test
    public void testToGrpcObject() {
        String accountAddress = "0703a61585597d9b56a46a658464738dff58222b4393d32dd9899bedb58666e9";
        GetEventsByEventAccessPath o = ImmutableGetEventsByEventAccessPath.builder()
                .accountAddress(AccountAddress.ofByteSequence(
                        ByteSequence.from(accountAddress)))
                .isAscending(true)
                .limit(10)
                .path(Path.SENT_EVENTS)
                .startEventSequenceNumber(1)
                .build();

        GetEventsByEventAccessPathRequest request = o.toGrpcObject().getGetEventsByEventAccessPathRequest();

        assertThat(request.getAscending(), is(true));
        assertThat(Hex.toHexString(request.getAccessPath().getAddress().toByteArray()), is(accountAddress));
        assertThat(request.getLimit(), is(10L));
        assertThat(request.getStartEventSeqNum(), is(1L));
        assertThat(Hex.toHexString(request.getAccessPath().getPath().toByteArray()), is(
                "0116608f05d24742a043e6fd12d3b32735f6bfcba287bea92b28a175cd4f3eee322f73656e745f6576656e74735f636f756e742f"));
    }

}
