package dev.jlibra.admissioncontrol.transaction;

import java.io.InputStream;
import java.util.List;

import org.immutables.value.Value;

@Value.Immutable
public interface Program {

    InputStream getCode();

    List<TransactionArgument> getArguments();

}
