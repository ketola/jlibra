package dev.jlibra.client;

import static com.github.arteam.simplejsonrpc.client.ParamsType.ARRAY;

import java.util.List;

import com.github.arteam.simplejsonrpc.client.JsonRpcId;
import com.github.arteam.simplejsonrpc.client.JsonRpcParams;
import com.github.arteam.simplejsonrpc.client.generator.SecureRandomStringIdGenerator;
import com.github.arteam.simplejsonrpc.core.annotation.JsonRpcMethod;
import com.github.arteam.simplejsonrpc.core.annotation.JsonRpcParam;
import com.github.arteam.simplejsonrpc.core.annotation.JsonRpcService;

import dev.jlibra.client.views.Account;
import dev.jlibra.client.views.BlockMetadata;
import dev.jlibra.client.views.CurrencyInfo;
import dev.jlibra.client.views.StateProof;
import dev.jlibra.client.views.event.Event;
import dev.jlibra.client.views.transaction.Transaction;

@JsonRpcService
@JsonRpcId(SecureRandomStringIdGenerator.class)
@JsonRpcParams(ARRAY)
public interface LibraJsonRpcClient {

    @JsonRpcMethod("get_account")
    Account getAccount(@JsonRpcParam("address") String address);

    @JsonRpcMethod("get_metadata")
    BlockMetadata getMetadata();

    @JsonRpcMethod("get_transactions")
    List<Transaction> getTransactions(@JsonRpcParam("version") long version, @JsonRpcParam("limit") long limit,
            @JsonRpcParam("include_events") boolean includeEvents);

    @JsonRpcMethod("get_account_transaction")
    Transaction getAccountTransaction(@JsonRpcParam("addresss") String address,
            @JsonRpcParam("sequence_number") long sequenceNumber,
            @JsonRpcParam("include_events") boolean includeEvents);

    @JsonRpcMethod("get_events")
    List<Event> getEvents(@JsonRpcParam("event_key") String eventKey,
            @JsonRpcParam("start") long start,
            @JsonRpcParam("limit") long limit);

    @JsonRpcMethod("get_state_proof")
    StateProof getStateProof(@JsonRpcParam("know_version") long knownVersion);

    @JsonRpcMethod("currencies_info")
    List<CurrencyInfo> currenciesInfo();

    @JsonRpcMethod("submit")
    void submit(@JsonRpcParam("payload") String payload);
}
