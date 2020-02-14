package dev.jlibra.serialization;

import dev.jlibra.admissioncontrol.transaction.VariableLengthByteSequence;

public interface LibraSerializable {
    VariableLengthByteSequence serialize();
}
