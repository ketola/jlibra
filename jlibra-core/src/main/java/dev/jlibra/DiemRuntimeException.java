package dev.jlibra;

public class DiemRuntimeException extends RuntimeException {
    public DiemRuntimeException(String message) {
        super(message);
    }

    public DiemRuntimeException(String message, Throwable t) {
        super(message, t);
    }
}
