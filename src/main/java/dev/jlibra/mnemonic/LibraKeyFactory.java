package dev.jlibra.mnemonic;

import org.bouncycastle.crypto.digests.SHA3Digest;
import org.bouncycastle.crypto.generators.HKDFBytesGenerator;
import org.bouncycastle.crypto.params.HKDFParameters;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class LibraKeyFactory {

    private static final byte[] MASTER_KEY_SALT = "LIBRA WALLET: master key salt$".getBytes();
    private static final byte[] INFO_PREFIX = "LIBRA WALLET: derived key$".getBytes();

    private final HKDFBytesGenerator hkdf = new HKDFBytesGenerator(new SHA3Digest(256));
    private final Master master;
    private final Seed seed;

    public LibraKeyFactory(Seed seed) {
        this.seed = seed;

        byte[] data = new byte[32];

        hkdf.init(new HKDFParameters(seed.getData(), MASTER_KEY_SALT, null));
        hkdf.generateBytes(data, 0, 32);

        this.master = new Master(data);
    }

    public Master getMaster() {
        return master;
    }

    public ExtendedPrivKey privateChild(ChildNumber childNumber) {

        // application info in the HKDF context is defined as Libra derived key$child_number.
        byte[] info = ByteBuffer
                .allocate(INFO_PREFIX.length + 8)
                .put(INFO_PREFIX)
                .order(ByteOrder.LITTLE_ENDIAN)
                .putLong(childNumber.data)
                .array();

        hkdf.init(new HKDFParameters(seed.getData(), MASTER_KEY_SALT, info));
        byte[] hkdfExpand = new byte[32];
        hkdf.generateBytes(hkdfExpand, 0, 32);

        return new ExtendedPrivKey(childNumber, new SecretKey(hkdfExpand));
    }
}
