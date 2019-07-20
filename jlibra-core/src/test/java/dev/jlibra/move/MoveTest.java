package dev.jlibra.move;

import com.google.protobuf.ByteString;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class MoveTest {

    @Test
    public void testPeerToPeerTransfer() throws Exception {
        assertThat(Move.peerToPeerTransfer,
                equalTo(ByteString.readFrom(MoveTest.this.getClass().getResourceAsStream("/move/peer_to_peer_transfer.bin"))));
    }

}
