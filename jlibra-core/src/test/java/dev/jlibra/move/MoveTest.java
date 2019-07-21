package dev.jlibra.move;

import org.junit.Test;

import static com.google.protobuf.ByteString.readFrom;
import static dev.jlibra.move.Move.peerToPeerTransfer;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class MoveTest {

    @Test
    public void testPeerToPeerTransfer() throws Exception {
        assertThat(
                peerToPeerTransfer,
                equalTo(readFrom(MoveTest.this.getClass().getResourceAsStream("/move/peer_to_peer_transfer.bin")))
        );
    }

}
