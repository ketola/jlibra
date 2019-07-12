package dev.jlibra.example;

import static dev.jlibra.KeyUtils.toHexStringLibraAddress;
import static java.util.Arrays.asList;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.util.HashSet;

import org.bouncycastle.util.encoders.Hex;

import dev.jlibra.KeyUtils;
import dev.jlibra.admissioncontrol.AddressArgument;
import dev.jlibra.admissioncontrol.AdmissionControl;
import dev.jlibra.admissioncontrol.Program;
import dev.jlibra.admissioncontrol.Result;
import dev.jlibra.admissioncontrol.Transaction;
import dev.jlibra.admissioncontrol.U64Argument;
import dev.jlibra.move.Move;

public class TransferExample {

    public static void main(String[] args) throws Exception {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

        PrivateKey privateKey = KeyUtils.privateKeyFromHexString(
                "3051020101300506032b6570042204207422e9df27029f7b83c37035622f93cd0e9b3a2a705d0745d573252756fd8c888121008e23fbceaa5b7a038c8994ca8258c8815e6e9007e3de86598cd46357e5e60024");
        PublicKey publicKey = KeyUtils.publicKeyFromHexString(
                "302a300506032b65700321008e23fbceaa5b7a038c8994ca8258c8815e6e9007e3de86598cd46357e5e60024");

        String toAddress = "045d3e63dba85f759d66f9bed4a0e4c262d17f9713f25e846fdae63891837a98";

        long amount = 5;
        int sequenceNumber = 2;

        System.out.println(
                String.format("Sending from %s to %s", toHexStringLibraAddress(publicKey.getEncoded()), toAddress));

        // Arguments for the peer to peer transaction
        U64Argument amountArgument = new U64Argument(amount * 1000000);
        AddressArgument addressArgument = new AddressArgument(Hex.decode(toAddress));

        Transaction transaction = new Transaction(sequenceNumber,
                new Program(Move.peerToPeerTransfer(), new HashSet<>(asList(amountArgument, addressArgument))));
        Result result = new AdmissionControl().sendTransaction(publicKey, privateKey,
                transaction);

        System.out.println("Admission control status: " + result.getAdmissionControlStatus());
        System.out.println("Mempool status: " + result.getMempoolStatus());
        System.out.println("VM status: " + result.getVmStatus());

        Thread.sleep(2000); // add sleep to prevent premature closing of channel
    }

}
