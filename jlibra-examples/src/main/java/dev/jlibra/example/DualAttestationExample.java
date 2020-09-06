package dev.jlibra.example;

import static dev.jlibra.poller.Conditions.accountHasPositiveBalance;
import static dev.jlibra.poller.Conditions.transactionFound;
import static java.util.Arrays.asList;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.Security;
import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.jlibra.AccountAddress;
import dev.jlibra.AuthenticationKey;
import dev.jlibra.PublicKey;
import dev.jlibra.client.LibraClient;
import dev.jlibra.client.views.Account;
import dev.jlibra.client.views.role.ParentVASPAccountRole;
import dev.jlibra.faucet.Faucet;
import dev.jlibra.move.Move;
import dev.jlibra.poller.Wait;
import dev.jlibra.serialization.ByteArray;
import dev.jlibra.transaction.ChainId;
import dev.jlibra.transaction.DualAttestation;
import dev.jlibra.transaction.ImmutableDualAttestation;
import dev.jlibra.transaction.ImmutableScript;
import dev.jlibra.transaction.ImmutableSignedTransaction;
import dev.jlibra.transaction.ImmutableTransaction;
import dev.jlibra.transaction.ImmutableTransactionAuthenticatorEd25519;
import dev.jlibra.transaction.Signature;
import dev.jlibra.transaction.SignedTransaction;
import dev.jlibra.transaction.Struct;
import dev.jlibra.transaction.Transaction;
import dev.jlibra.transaction.argument.AccountAddressArgument;
import dev.jlibra.transaction.argument.U64Argument;
import dev.jlibra.transaction.argument.U8VectorArgument;

/**
 * See:
 * https://github.com/libra/libra/blob/master/client/libra-dev/README.md#dual-attestationtravel-rule-protocol
 * 
 */
public class DualAttestationExample {

    private static final Logger logger = LoggerFactory.getLogger(DualAttestationExample.class);

    public static void main(String[] args) throws Exception {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

        LibraClient client = LibraClient.builder()
                .withUrl("https://client.testnet.libra.org/v1/")
                .build();
        Faucet faucet = Faucet.builder()
                .build();

        KeyPairGenerator kpGen = KeyPairGenerator.getInstance("Ed25519", "BC");

        // 1. Create a VASP account
        KeyPair vasp1KeyPair = kpGen.generateKeyPair();

        PublicKey vasp1PublicKey = PublicKey.fromPublicKey(vasp1KeyPair.getPublic());
        AuthenticationKey authenticationKeyVasp1 = AuthenticationKey.fromPublicKey(vasp1PublicKey);
        AccountAddress accountAddressVasp1 = AccountAddress.fromAuthenticationKey(authenticationKeyVasp1);
        faucet.mint(authenticationKeyVasp1, 300_000L * 1_000_000L, "LBR");
        Wait.until(accountHasPositiveBalance(accountAddressVasp1, client));
        logger.info("Vasp 1 account address: {}, auth key: {}", accountAddressVasp1, authenticationKeyVasp1);

        // 2. Create a second VASP account
        KeyPair vasp2KeyPair = kpGen.generateKeyPair();
        KeyPair vasp2ComplianceKeyPair = kpGen.generateKeyPair();
        PublicKey vasp2PublicKey = PublicKey.fromPublicKey(vasp2KeyPair.getPublic());
        AuthenticationKey authenticationKeyVasp2 = AuthenticationKey.fromPublicKey(vasp2PublicKey);
        AccountAddress accountAddressVasp2 = AccountAddress.fromAuthenticationKey(authenticationKeyVasp2);
        faucet.mint(authenticationKeyVasp2, 10_000L * 1_000_000L, "LBR");
        Wait.until(accountHasPositiveBalance(accountAddressVasp2, client));
        logger.info("Vasp 2 account address: {}, auth key: {}", accountAddressVasp2, authenticationKeyVasp2);

        byte[] metadata = "metadata".getBytes();

        Account vasp2Account = client.getAccount(accountAddressVasp2);
        logger.info("-----------------------------------------------------------------------------------------------");
        logger.info("Vasp 2 account compliance key {}", ((ParentVASPAccountRole) vasp2Account.role()).complianceKey());
        logger.info("Vasp 2 account base url {}", ((ParentVASPAccountRole) vasp2Account.role()).baseUrl());
        logger.info("-----------------------------------------------------------------------------------------------");

        // Try leaving this step out and the transfer transaction fails with
        // vm_status":{"abort_code":1281,"location":"00000000000000000000000000000001::DualAttestation","type":"move_abort"}
        // with this you should get vm_status":{"type":"executed"}
        rotateDualAttestationInfoForVaspAccount(client, vasp2KeyPair, vasp2ComplianceKeyPair, 0);

        vasp2Account = client.getAccount(accountAddressVasp2);
        logger.info("-----------------------------------------------------------------------------------------------");
        logger.info("Vasp 2 account compliance key {}", ((ParentVASPAccountRole) vasp2Account.role()).complianceKey());
        logger.info("Vasp 2 account base url {}", ((ParentVASPAccountRole) vasp2Account.role()).baseUrl());
        logger.info("-----------------------------------------------------------------------------------------------");

        DualAttestation dualAttestation = ImmutableDualAttestation.builder()
                .amount(100_000L * 1_000_000L)
                .metadata(ByteArray.from(metadata))
                .payerAddress(accountAddressVasp1)
                .build();

        transferFromVaspToVasp(client, vasp1KeyPair, vasp2KeyPair, metadata,
                dualAttestation.sign(vasp2ComplianceKeyPair.getPrivate()), 0);
    }

    private static void transferFromVaspToVasp(LibraClient client, KeyPair sourceKeyPair, KeyPair targetKeyPair,
            byte[] metadata,
            ByteArray dualAttestationSignature, int sequenceNumber) {
        AuthenticationKey authenticationKeySource = AuthenticationKey.fromPublicKey(sourceKeyPair.getPublic());
        AuthenticationKey authenticationKeyTarget = AuthenticationKey.fromPublicKey(targetKeyPair.getPublic());
        long transferAmount = 100_000L * 1_000_000L;

        // Arguments for the peer to peer transaction
        U64Argument amountArgument = new U64Argument(transferAmount);
        AccountAddressArgument addressArgument = new AccountAddressArgument(
                AccountAddress.fromAuthenticationKey(authenticationKeyTarget));

        U8VectorArgument metadataArgument = new U8VectorArgument(
                ByteArray.from(metadata));
        U8VectorArgument signatureArgument = new U8VectorArgument(
                dualAttestationSignature);

        logger.info("Receiver address {}, sender address {}",
                AccountAddress.fromAuthenticationKey(authenticationKeyTarget),
                AccountAddress.fromAuthenticationKey(authenticationKeySource));

        Transaction transaction = ImmutableTransaction.builder()
                .sequenceNumber(sequenceNumber)
                .maxGasAmount(2_000_000)
                .gasCurrencyCode("LBR")
                .gasUnitPrice(1)
                .sender(AccountAddress
                        .fromAuthenticationKey(authenticationKeySource))
                .expirationTimestampSecs(Instant.now().getEpochSecond() + 60)
                .payload(ImmutableScript.builder()
                        .code(Move.peerToPeerTransferWithMetadata())
                        .typeArguments(asList(Struct.typeTagForCurrency("LBR")))
                        .addArguments(addressArgument, amountArgument, metadataArgument, signatureArgument)
                        .build())
                .chainId(ChainId.TESTNET)
                .build();

        Signature signature = Signature.signTransaction(transaction, sourceKeyPair.getPrivate());

        SignedTransaction signedTransaction = ImmutableSignedTransaction.builder()
                .authenticator(ImmutableTransactionAuthenticatorEd25519.builder()
                        .publicKey(PublicKey.fromPublicKey(sourceKeyPair.getPublic()))
                        .signature(signature)
                        .build())
                .transaction(transaction)
                .build();

        client.submit(signedTransaction);

        Wait.until(transactionFound(AccountAddress.fromAuthenticationKey(authenticationKeySource), sequenceNumber,
                client));
    }

    /**
     * This transaction will set the compliance key for the account, the compliance
     * key is used to sign the dual attestation information
     */
    private static void rotateDualAttestationInfoForVaspAccount(LibraClient client, KeyPair parentVaspKeyPair,
            KeyPair vaspComplianceKeyPair,
            int sequenceNumber) {

        U8VectorArgument newUrlArgument = new U8VectorArgument(ByteArray.from("https://jlibra.dev".getBytes()));
        U8VectorArgument compliancePublicKey = new U8VectorArgument(
                PublicKey.fromPublicKey(vaspComplianceKeyPair.getPublic()));

        Transaction transaction = ImmutableTransaction.builder()
                .sequenceNumber(sequenceNumber)
                .maxGasAmount(640000)
                .gasUnitPrice(1)
                .gasCurrencyCode("LBR")
                .sender(AccountAddress
                        .fromAuthenticationKey(AuthenticationKey.fromPublicKey(parentVaspKeyPair.getPublic())))
                .expirationTimestampSecs(Instant.now().getEpochSecond() + 60)
                .payload(ImmutableScript.builder()
                        .code(Move.rotateDualAttestationInfo())
                        .addArguments(newUrlArgument, compliancePublicKey)
                        .build())
                .chainId(ChainId.TESTNET)
                .build();

        Signature signature = Signature.signTransaction(transaction, parentVaspKeyPair.getPrivate());

        SignedTransaction signedTransaction = ImmutableSignedTransaction.builder()
                .authenticator(ImmutableTransactionAuthenticatorEd25519.builder()
                        .publicKey(PublicKey.fromPublicKey(parentVaspKeyPair.getPublic()))
                        .signature(signature)
                        .build())
                .transaction(transaction)
                .build();

        client.submit(signedTransaction);
        Wait.until(transactionFound(
                AccountAddress.fromAuthenticationKey(AuthenticationKey.fromPublicKey(parentVaspKeyPair.getPublic())),
                sequenceNumber, client));
    }

}
