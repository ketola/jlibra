package dev.jlibra.serialization.lcs;

import java.io.ByteArrayInputStream;

import org.bouncycastle.util.encoders.Hex;
import org.junit.Test;

import dev.jlibra.admissioncontrol.transaction.Transaction;
import dev.jlibra.serialization.ByteArray;
import dev.jlibra.serialization.Deserialization;

public class LCSDeserializerTest {

    // @Test
    public void testUleb() {
        String hex = "8001";

        System.out.println(Deserialization.readUleb128Int(new ByteArrayInputStream(Hex.decode(hex))));

    }

    @Test
    public void testDeserializeTransaction() {
        String transactionHexString = "4e03aec69589026b4a095c9cd2e53ca6030000000000000002010001018f5fbb9486acc5fb90f1a6be43a0013d020000000000000001000000000000000600000000000000000000000000000000034c42520154000100000000000000";

        Transaction t = new LCSDeserializer<Transaction>().deserialize(ByteArray.from(Hex.decode(transactionHexString)),
                Transaction.class);

        System.out.println(t);
    }
}
