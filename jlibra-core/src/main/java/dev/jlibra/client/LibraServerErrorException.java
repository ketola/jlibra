package dev.jlibra.client;

import dev.jlibra.LibraRuntimeException;

public class LibraServerErrorException extends LibraRuntimeException {

    private int code;

    private String errorMessage;

    public LibraServerErrorException(int code, String message) {
        super(String.format("%d: %s", code, message));
        this.code = code;
        this.errorMessage = message;
    }

    public int getCode() {
        return code;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

}