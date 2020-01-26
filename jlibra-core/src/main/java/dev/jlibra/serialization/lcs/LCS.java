package dev.jlibra.serialization.lcs;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

public class LCS {

    @Target(METHOD)
    @Retention(RUNTIME)
    public @interface Field {
        int ordinal();
    }

    @Target(TYPE)
    @Retention(RUNTIME)
    public @interface Enum {
        int ordinal();
    }

}
