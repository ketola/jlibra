package dev.jlibra.admissioncontrol.transaction;

import com.google.protobuf.ByteString;

import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
public interface Program {

    ByteString getCode();

    List<TransactionArgument> getArguments();

}
