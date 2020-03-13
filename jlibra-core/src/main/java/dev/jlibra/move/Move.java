package dev.jlibra.move;

import java.io.IOException;
import java.io.InputStream;

import com.google.common.io.ByteStreams;

import dev.jlibra.serialization.ByteArray;
import dev.jlibra.serialization.ByteSequence;

public class Move {

    public static ByteSequence rotateAuthenticationKeyAsBytes() {
        return readMoveScriptBytes("/move/rotate_authentication_key.mv");
    }

    public static ByteSequence peerToPeerTransferAsBytes() {
        return readMoveScriptBytes("/move/peer_to_peer_transfer.mv");
    }

    public static ByteSequence peerToPeerTransferWithMetadataAsBytes() {
        return readMoveScriptBytes("/move/peer_to_peer_transfer_with_metadata.mv");
    }

    private static ByteSequence readMoveScriptBytes(String fileName) {
        InputStream jsonBinary = Move.class.getResourceAsStream(fileName);

        try {
            return ByteArray.from(ByteStreams.toByteArray(jsonBinary));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
