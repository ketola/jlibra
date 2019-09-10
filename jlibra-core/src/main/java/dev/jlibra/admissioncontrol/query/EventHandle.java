package dev.jlibra.admissioncontrol.query;

import org.immutables.value.Value;

@Value.Immutable
public interface EventHandle {

    public byte[] getKey();

    public int getCount();

}
