package dev.jlibra.admissioncontrol.query;

import static java.nio.charset.StandardCharsets.UTF_8;

import org.bouncycastle.jcajce.provider.digest.SHA3;
import org.immutables.value.Value;

import com.google.protobuf.ByteString;

import dev.jlibra.AccountAddress;
import dev.jlibra.serialization.ByteSequence;
import dev.jlibra.serialization.Serializer;
import types.AccessPathOuterClass.AccessPath;
import types.GetWithProof.GetEventsByEventAccessPathRequest;
import types.GetWithProof.RequestItem;

@Value.Immutable
public abstract class GetEventsByEventAccessPath {

    private static final int STRUCT_TAG_TYPE_PARAMS_LENGTH = 0;
    private static final String STRUCT_TAG_MODULE = "T";
    private static final String STRUCT_TAG_ADDRESS = "LibraAccount";
    private static final String STRUCT_TAG_ACCOUNT_ADDRESS = "0000000000000000000000000000000000000000000000000000000000000000";
    private static final byte RESOURCE_TAG = (byte) 1;

    public enum Path {
        SENT_EVENTS("/sent_events_count/"), RECEIVED_EVENTS("/received_events_count/");

        public final String suffix;

        private Path(String suffix) {
            this.suffix = suffix;
        }
    }

    public abstract AccountAddress getAccountAddress();

    public abstract Path getPath();

    public abstract long getLimit();

    public abstract long getStartEventSequenceNumber();

    public abstract boolean isAscending();

    public RequestItem toGrpcObject() {
        return RequestItem.newBuilder()
                .setGetEventsByEventAccessPathRequest(GetEventsByEventAccessPathRequest.newBuilder()
                        .setAccessPath(AccessPath.newBuilder()
                                .setAddress(ByteString.copyFrom(getAccountAddress().getByteSequence().toArray()))
                                .setPath(ByteString.copyFrom(generateAccessPath(getPath())))
                                .build())
                        .setAscending(isAscending())
                        .setLimit(getLimit())
                        .setStartEventSeqNum(getStartEventSequenceNumber()))
                .build();
    }

    private static byte[] generateAccessPath(Path path) {
        SHA3.DigestSHA3 digestSHA3 = new SHA3.Digest256();

        byte[] serializedStructTag = Serializer.builder()
                .appendWithoutLengthInformation(
                        ByteSequence.from(STRUCT_TAG_ACCOUNT_ADDRESS))
                .appendString(STRUCT_TAG_ADDRESS)
                .appendString(STRUCT_TAG_MODULE)
                .appendInt(STRUCT_TAG_TYPE_PARAMS_LENGTH)
                .toByteSequence()
                .toArray();

        byte[] salt = digestSHA3.digest("StructTag::libra_types::language_storage@@$$LIBRA$$@@".getBytes());
        byte[] saltAndStructTag = new byte[salt.length + serializedStructTag.length];
        System.arraycopy(salt, 0, saltAndStructTag, 0, salt.length);
        System.arraycopy(serializedStructTag, 0, saltAndStructTag, salt.length, serializedStructTag.length);
        byte[] structTagHash = digestSHA3.digest(saltAndStructTag);

        byte[] pathBytes = Serializer.builder()
                .appendByte(RESOURCE_TAG)
                .appendWithoutLengthInformation(ByteSequence.from(structTagHash))
                .appendWithoutLengthInformation(ByteSequence.from(path.suffix.getBytes(UTF_8)))
                .toByteSequence()
                .toArray();

        return pathBytes;
    }
}
