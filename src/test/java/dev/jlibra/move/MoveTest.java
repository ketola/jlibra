package dev.jlibra.move;

import static org.apache.commons.io.IOUtils.toByteArray;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class MoveTest {

    @Test
    public void testPeerToPeerTransfer() throws Exception {
        assertThat(Move.peerToPeerTransfer(),
                equalTo(toByteArray(MoveTest.this.getClass().getResourceAsStream("/move/peer_to_peer_transfer.bin"))));
    }

}
