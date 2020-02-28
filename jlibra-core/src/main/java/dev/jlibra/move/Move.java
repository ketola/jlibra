package dev.jlibra.move;

import static java.util.stream.Collectors.joining;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

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
        String json = readJson(jsonBinary);
        String[] bytesAsString = json.substring(json.indexOf('[') + 1, json.indexOf(']')).split(",");
        byte[] bytes = new byte[bytesAsString.length];
        for (int idx = 0; idx < bytesAsString.length; idx++) {
            bytes[idx] = (byte) Integer.parseInt(bytesAsString[idx]);
        }
        return ByteArray.from(bytes);
    }

    private static String readJson(InputStream inputStream) {
        return new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .lines()
                .collect(joining());
    }

}
