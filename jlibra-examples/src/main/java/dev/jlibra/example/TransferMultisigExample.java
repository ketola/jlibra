package dev.jlibra.example;

import java.security.PrivateKey;
import java.security.Security;
import java.time.Instant;
import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bouncycastle.util.encoders.Hex;

import dev.jlibra.AccountAddress;
import dev.jlibra.AuthenticationKey;
import dev.jlibra.KeyUtils;
import dev.jlibra.MultiSignaturePublicKey;
import dev.jlibra.PublicKey;
import dev.jlibra.admissioncontrol.transaction.AccountAddressArgument;
import dev.jlibra.admissioncontrol.transaction.ByteArrayArgument;
import dev.jlibra.admissioncontrol.transaction.ImmutableScript;
import dev.jlibra.admissioncontrol.transaction.ImmutableSignedTransaction;
import dev.jlibra.admissioncontrol.transaction.ImmutableTransaction;
import dev.jlibra.admissioncontrol.transaction.ImmutableTransactionAuthenticatorMultiEd25519;
import dev.jlibra.admissioncontrol.transaction.Signature;
import dev.jlibra.admissioncontrol.transaction.SignedTransaction;
import dev.jlibra.admissioncontrol.transaction.Transaction;
import dev.jlibra.admissioncontrol.transaction.U64Argument;
import dev.jlibra.client.LibraJsonRpcClient;
import dev.jlibra.client.LibraJsonRpcClientBuilder;
import dev.jlibra.move.Move;
import dev.jlibra.serialization.ByteArray;
import dev.jlibra.serialization.lcs.LCSSerializer;

public class TransferMultisigExample {

    private static final Logger logger = LogManager.getLogger(TransferMultisigExample.class);

    public static void main(String[] args) throws Exception {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

        PrivateKey privateKey1 = KeyUtils.privateKeyFromByteSequence(ByteArray.from(
                "3051020101300506032b6570042204209340bab58bdd0f293dae15a41f8e390eac94cd0f7270120fae25a6c1c9d80a6b81210085c582f99e99edca5417802e108f4cd47c362d352992d616ef30608ee3675955"));
        java.security.PublicKey publicKey1 = KeyUtils.publicKeyFromByteSequence(ByteArray.from(
                "302a300506032b657003210085c582f99e99edca5417802e108f4cd47c362d352992d616ef30608ee3675955"));

        PrivateKey privateKey2 = KeyUtils.privateKeyFromByteSequence(ByteArray.from(
                "3051020101300506032b6570042204200f38bbd64e621c6444abfc3c9b07d6d45e1b52ea9f06eb1740d60c412ddd2b388121003f277edec12f11e182cec2f8eecd7411ba636e9d6e4c8e66ff07e289e9cd3dae"));
        java.security.PublicKey publicKey2 = KeyUtils.publicKeyFromByteSequence(ByteArray.from(
                "302a300506032b65700321003f277edec12f11e182cec2f8eecd7411ba636e9d6e4c8e66ff07e289e9cd3dae"));

        AuthenticationKey authenticationKeyTarget = AuthenticationKey
                .fromHexString("c0c19d6b1d48371ea28f0cdc5f74bba7b3f7e8e38f8c8393f281a2f0792a2849");

        MultiSignaturePublicKey multiPubKey = MultiSignaturePublicKey.create(
                Arrays.asList(PublicKey.fromPublicKey(publicKey1), PublicKey.fromPublicKey(publicKey2)),
                (byte) 2);

        // Arguments for the peer to peer transaction
        U64Argument amountArgument = new U64Argument(1 * 1000000);
        AccountAddressArgument addressArgument = new AccountAddressArgument(
                AccountAddress.fromAuthenticationKey(authenticationKeyTarget));

        // When you are sending money to an account that does not exist, you need to
        // provide the auth key prefix parameter. You can leave it as an empty byte
        // array if
        // the account exists.
        ByteArrayArgument authkeyPrefixArgument = new ByteArrayArgument(
                authenticationKeyTarget.toByteArray().subseq(0, 16));

        AuthenticationKey authenticationKey = AuthenticationKey.fromMultiSignaturePublicKey(multiPubKey);
        AccountAddress senderAddress = AccountAddress.fromAuthenticationKey(authenticationKey);

        logger.info("Sender auth key {}, sender address {}", authenticationKey, senderAddress);
        logger.info("Receiver auth key {}, sender address {}", authenticationKeyTarget,
                AccountAddress.fromAuthenticationKey(authenticationKeyTarget));

        Transaction transaction = ImmutableTransaction.builder()
                .sequenceNumber(0)
                .maxGasAmount(240000)
                .gasUnitPrice(0)
                .senderAccount(
                        senderAddress)
                .expirationTime(Instant.now().getEpochSecond() + 60)
                .payload(ImmutableScript.builder()
                        .code(Move.peerToPeerTransferAsBytes())
                        .addArguments(addressArgument, authkeyPrefixArgument, amountArgument)
                        .build())
                .build();

        Signature signature = Signature.newMultisignature();
        signature = Signature.addSignatureToMultiSignature(signature, 0, transaction, privateKey1);
        signature = Signature.addSignatureToMultiSignature(signature, 1, transaction, privateKey2);

        SignedTransaction signedTransaction = ImmutableSignedTransaction.builder()
                .authenticator(ImmutableTransactionAuthenticatorMultiEd25519.builder()
                        .publicKey(multiPubKey)
                        .signature(signature)
                        .build())
                .transaction(transaction)
                .build();

        LibraJsonRpcClient client = LibraJsonRpcClientBuilder.builder()
                .withUrl("http://client.testnet.libra.org/")
                .build();

        String hexString = Hex
                .toHexString(LCSSerializer.create().serialize(signedTransaction, SignedTransaction.class).toArray());
        client.submit(hexString);
    }

}
