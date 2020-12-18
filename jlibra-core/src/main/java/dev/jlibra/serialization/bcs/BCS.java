package dev.jlibra.serialization.bcs;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Contains annotations to instruct the serialization of the java types to byte
 * arrays using the Object Canonical Serialization.
 * 
 * To find out more about the details of the serialization, see
 * https://docs.rs/bcs/
 */
public class BCS {

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
        Class<?>[] classes();
    }

    /**
     * An annotation for marking a java type as a data structure serializable with
     * the Diem Canonical Serialization.
     */
    @Target(TYPE)
    @Retention(RUNTIME)
    public @interface Structure {
    }

}
