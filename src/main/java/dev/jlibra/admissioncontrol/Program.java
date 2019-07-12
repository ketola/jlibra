package dev.jlibra.admissioncontrol;

import java.io.InputStream;
import java.util.Set;

public class Program {

    private InputStream code;

    private Set<TransactionArgument> arguments;

    public Program(InputStream code, Set<TransactionArgument> arguments) {
        this.code = code;
        this.arguments = arguments;
    }

    public InputStream getCode() {
        return code;
    }

    public Set<TransactionArgument> getArguments() {
        return arguments;
    }

}
