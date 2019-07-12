package dev.jlibra.admissioncontrol;

import java.io.InputStream;
import java.util.List;

public class Program {

    private InputStream code;

    private List<TransactionArgument> arguments;

    public Program(InputStream code, List<TransactionArgument> arguments) {
        this.code = code;
        this.arguments = arguments;
    }

    public InputStream getCode() {
        return code;
    }

    public List<TransactionArgument> getArguments() {
        return arguments;
    }

}
