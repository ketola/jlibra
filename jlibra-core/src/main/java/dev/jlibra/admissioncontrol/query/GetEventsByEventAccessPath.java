package dev.jlibra.admissioncontrol.query;

import static java.nio.charset.StandardCharsets.UTF_8;

import org.immutables.value.Value;

import dev.jlibra.Hash;
import dev.jlibra.admissioncontrol.transaction.FixedLengthByteSequence;
import dev.jlibra.admissioncontrol.transaction.ImmutableVariableLengthByteSequence;
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
    private static final byte RESOURCE_TAG = 1;

    public enum Path {
        SENT_EVENTS("/sent_events_count/"), RECEIVED_EVENTS("/received_events_count/");

        public final String suffix;

        private Path(String suffix) {
            this.suffix = suffix;
        }
    }

    public abstract FixedLengthByteSequence getAccountAddress();

    public abstract Path getPath();

    public abstract long getLimit();

    public abstract long getStartEventSequenceNumber();

    public abstract boolean isAscending();

    public RequestItem toGrpcObject() {
        return RequestItem.newBuilder()
                .setGetEventsByEventAccessPathRequest(GetEventsByEventAccessPathRequest.newBuilder()
                        .setAccessPath(AccessPath.newBuilder()
                                .setAddress(getAccountAddress().getValue().toByteString())
                                .setPath(generateAccessPath(getPath()).toByteString())
                                .build())
                        .setAscending(isAscending())
                        .setLimit(getLimit())
                        .setStartEventSeqNum(getStartEventSequenceNumber()))
                .build();
    }

    private static ByteSequence generateAccessPath(Path path) {
        ByteSequence serializedStructTag = Serializer.builder()
                .append(ImmutableVariableLengthByteSequence.builder()
                        .value(ByteSequence.from(STRUCT_TAG_ACCOUNT_ADDRESS))
                        .build())
                .appendString(STRUCT_TAG_ADDRESS)
                .appendString(STRUCT_TAG_MODULE)
                .appendInt(STRUCT_TAG_TYPE_PARAMS_LENGTH)
                .toByteSequence();

        ByteSequence structTagHash = Hash.ofInput(serializedStructTag)
                .hash(ByteSequence.from("StructTag::libra_types::language_storage@@$$LIBRA$$@@".getBytes()));

        return Serializer.builder()
                .appendByte(RESOURCE_TAG)
                .append(ImmutableVariableLengthByteSequence.builder()
                        .value(structTagHash)
                        .build())
                .append(ImmutableVariableLengthByteSequence.builder()
                        .value(ByteSequence.from(path.suffix.getBytes(UTF_8)))
                        .build())
                .toByteSequence();
    }
}
