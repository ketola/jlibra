package dev.jlibra.admissioncontrol.query;

import org.immutables.value.Value;

import dev.jlibra.serialization.ByteSequence;

@Value.Immutable
public interface EventHandle {

    ByteSequence getKey();

    int getCount();

}
