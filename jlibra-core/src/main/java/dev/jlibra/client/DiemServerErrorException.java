package dev.jlibra.client;

import dev.jlibra.DiemRuntimeException;

public class DiemServerErrorException extends DiemRuntimeException {

    private int code;

    private String errorMessage;

    public DiemServerErrorException(int code, String message) {
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