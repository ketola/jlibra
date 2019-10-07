package dev.jlibra.move;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import static java.util.stream.Collectors.joining;

public class Move {

    public static byte[] rotateAuthenticationKeyAsBytes() {
        return readMoveScriptBytes("/move/rotate_authentication_key.json");
    }

    public static byte[] peerToPeerTransferAsBytes() {
        return readMoveScriptBytes("/move/peer_to_peer_transfer.json");
    }

    private static byte[] readMoveScriptBytes(String fileName) {
        InputStream jsonBinary = Move.class.getResourceAsStream(fileName);
        String json = readJson(jsonBinary);
        String[] bytesAsString = json.substring(json.indexOf('[') + 1, json.indexOf(']')).split(",");
        byte[] bytes = new byte[bytesAsString.length];
        for (int idx = 0; idx < bytesAsString.length; idx++) {
            bytes[idx] = (byte) Integer.parseInt(bytesAsString[idx]);
        }
        return bytes;
    }

    private static String readJson(InputStream inputStream) {
        return new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .lines()
                .collect(joining());
    }

}
