package dev.jlibra.mnemonic;

import org.bouncycastle.crypto.digests.SHA3Digest;
import org.bouncycastle.crypto.generators.HKDFBytesGenerator;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.params.HKDFParameters;
import org.bouncycastle.crypto.params.KeyParameter;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

public class LibraKeyFactory {

    private static final byte[] MASTER_KEY_SALT = "LIBRA WALLET: master key salt$".getBytes(StandardCharsets.UTF_8);
    private static final byte[] INFO_PREFIX = "LIBRA WALLET: derived key$".getBytes(StandardCharsets.UTF_8);

    private final Master master;

    public LibraKeyFactory(Seed seed) {
        byte[] data = new byte[32];
        SHA3Digest sha3 = new SHA3Digest(256);
        HMac mac = new HMac(sha3);
        mac.init(new KeyParameter(MASTER_KEY_SALT));
        mac.update(seed.getData(), 0, seed.getData().length);
        mac.doFinal(data, 0);

        this.master = new Master(data);
    }

    public Master getMaster() {
        return master;
    }

    public ExtendedPrivKey privateChild(ChildNumber childNumber) {
        byte[] secretKey = new byte[32];
        byte[] info = createInfo(childNumber);
        SHA3Digest sha3 = new SHA3Digest(256);
        HKDFBytesGenerator hkdf = new HKDFBytesGenerator(sha3);
        hkdf.init(HKDFParameters.skipExtractParameters(master.getData(), info));
        hkdf.generateBytes(secretKey, 0, 32);

        return new ExtendedPrivKey(new SecretKey(secretKey));
    }

    /**
     * application info in the HKDF context is defined as Libra derived key$child_number.
     */
    private byte[] createInfo(ChildNumber childNumber) {
        return ByteBuffer.allocate(INFO_PREFIX.length + 8)
                .put(INFO_PREFIX)
                .order(ByteOrder.LITTLE_ENDIAN)
                .putLong(childNumber.data)
                .array();
    }
}
