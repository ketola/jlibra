package dev.jlibra.admissioncontrol.transaction;

import types.Transaction;
import java.util.function.Function;

public interface RawTransactionSigner extends Function<Transaction.RawTransaction, byte[]> {
}
