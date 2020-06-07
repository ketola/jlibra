package dev.jlibra.move;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import dev.jlibra.serialization.ByteArray;
import dev.jlibra.serialization.ByteSequence;

public class Move {

    public static ByteSequence rotateAuthenticationKey() {
        return readMoveScriptBytes("/move/rotate_authentication_key.mv");
    }

    public static ByteSequence peerToPeerTransferWithMetadata() {
        return readMoveScriptBytes("/move/peer_to_peer_with_metadata.mv");
    }

    public static ByteSequence createChildVaspAccount() {
        return readMoveScriptBytes("/move/create_child_vasp_account.mv");
    }

    private static ByteSequence readMoveScriptBytes(String fileName) {
        try {
            return ByteArray.from(streamToByteArray(Move.class.getResourceAsStream(fileName)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * from https://stackoverflow.com/a/30618794
     */
    public static byte[] streamToByteArray(InputStream stream) throws IOException {

        byte[] buffer = new byte[1024];
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        int line = 0;
        // read bytes from stream, and store them in buffer
        while ((line = stream.read(buffer)) != -1) {
            // Writes bytes from byte array (buffer) into output stream.
            os.write(buffer, 0, line);
        }
        stream.close();
        os.flush();
        os.close();
        return os.toByteArray();
    }
}
