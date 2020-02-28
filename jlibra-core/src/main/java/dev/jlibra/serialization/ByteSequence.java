package dev.jlibra.serialization;

import com.google.protobuf.ByteString;

public interface ByteSequence {

    byte[] toArray();

    ByteString toByteString();
}
