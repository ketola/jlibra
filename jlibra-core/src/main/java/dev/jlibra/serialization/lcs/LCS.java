package dev.jlibra.serialization.lcs;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import dev.jlibra.serialization.lcs.type.LibraEnum;
import dev.jlibra.serialization.lcs.type.TransactionPayload;

/**
 * Contains annotations to instruct the serialization of the java types to byte
 * arrays using the Libra Canonical Serialization.
 * 
 * To find out more about the details of the serialization, see
 * https://github.com/libra/libra/blob/master/common/lcs/src/lib.rs
 */
public class LCS {

    /**
     * An annotation for marking a method that is capable of returning the the value
     * for either a Structure or an Externally Tagged Enumeration
     */
    @Target(METHOD)
    @Retention(RUNTIME)
    public @interface Field {
        /**
         * Returns the ordinal of the field.
         * 
         * @return
         */
        int value();

        /**
         * Set the value to true if the returned value of the field should be serialized
         * as a fixed-length byte array.
         * 
         * @return
         */
        boolean fixedLength() default false;
    }

    /**
     * An annotation for marking a java type as an Externally Tagged Enumeration
     */
    @Target(TYPE)
    @Retention(RUNTIME)
    public @interface ExternallyTaggedEnumeration {
        /**
         * Returns the variant index of the enumeration.
         * 
         * @return
         */
        int value();

        Class<? extends LibraEnum> libraEnum() default TransactionPayload.class;

        Class<?>[] types() default TransactionPayload.class;
    }

    /**
     * An annotation for marking a java type as a data structure serializable with
     * the Libra Canonical Serialization.
     */
    @Target(TYPE)
    @Retention(RUNTIME)
    public @interface Structure {
        Class<?> builderClass() default Object.class;
    }

}
