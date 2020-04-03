package dev.jlibra.admissioncontrol.query;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.bouncycastle.util.encoders.Hex;
import org.junit.Test;

import dev.jlibra.AccountAddress;
import dev.jlibra.admissioncontrol.query.GetEventsByEventAccessPath.Path;
import types.GetWithProof.GetEventsByEventAccessPathRequest;

public class GetEventsByEventAccessPathTest {

    @Test
    public void testToGrpcObject() {
        String accountAddress = "0703a61585597d9b56a46a658464738dff58222b4393d32dd9899bedb58666e9";
        GetEventsByEventAccessPath o = ImmutableGetEventsByEventAccessPath.builder()
                .accountAddress(AccountAddress.fromHexString(accountAddress))
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
                "019332949da81c9a7b3e66a75c06e192584740a89bf9302a6e519e0eacc24061d02f73656e745f6576656e74735f636f756e742f"));
    }

}
