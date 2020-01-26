package dev.jlibra;

public class LibraRuntimeException extends RuntimeException {
    public LibraRuntimeException(String message) {
        super(message);
    }

    public LibraRuntimeException(String message, Throwable t) {
        super(message, t);
    }
}
