package dev.jlibra.admissioncontrol.transaction;

import org.junit.Test;

import dev.jlibra.serialization.ByteSequence;

public class AccountAddressArgumentTest {

    @Test
    public void testSerialize() {
        AccountAddressArgument argument = new AccountAddressArgument(
                ImmutableFixedLengthByteSequence.builder()
                        .value(ByteSequence.from("2c25991785343b23ae073a50e5fd809a2cd867526b3c1db2b0bf5d1924c693ed"))
                        .build());

        // assertThat(argument.serialize(.toString().toUpperCase(),
        // is("010000002C25991785343B23AE073A50E5FD809A2CD867526B3C1DB2B0BF5D1924C693ED"));
    }
}
