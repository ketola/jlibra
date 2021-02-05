package dev.jlibra.client.jsonrpc;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import dev.jlibra.client.views.Account;

public class LibraJsonRpcClientTest {

    private HttpClient httpClient;

    private String url = "https://urltodiem";

    @BeforeEach
    public void setUp() {
        this.httpClient = mock(HttpClient.class);
    }

    @Test
    public void testGetAccount() throws Exception {
        HttpResponse httpResponse = mock(HttpResponse.class);
        when(httpResponse.body()).thenReturn(
                "{\"diem_chain_id\":2,\"diem_ledger_version\":4867582,\"diem_ledger_timestampusec\":1604134778415882,\"jsonrpc\":\"2.0\",\"id\":\"5e0a1c17-d3ca-4813-817e-92f0680ccfdc\",\"result\":{\"address\":\"b3f7e8e38f8c8393f281a2f0792a2849\",\"authentication_key\":\"c0c19d6b1d48371ea28f0cdc5f74bba7b3f7e8e38f8c8393f281a2f0792a2849\",\"balances\":[{\"amount\":500000000,\"currency\":\"Coin1\"}],\"delegated_key_rotation_capability\":false,\"delegated_withdrawal_capability\":false,\"is_frozen\":false,\"received_events_key\":\"0200000000000000b3f7e8e38f8c8393f281a2f0792a2849\",\"role\":{\"base_url\":\"\",\"base_url_rotation_events_key\":\"0100000000000000b3f7e8e38f8c8393f281a2f0792a2849\",\"compliance_key\":\"\",\"compliance_key_rotation_events_key\":\"0000000000000000b3f7e8e38f8c8393f281a2f0792a2849\",\"expiration_time\":18446744073709551615,\"human_name\":\"No. 307\",\"num_children\":0,\"type\":\"parent_vasp\"},\"sent_events_key\":\"0300000000000000b3f7e8e38f8c8393f281a2f0792a2849\",\"sequence_number\":0}}");
        when(httpClient.sendAsync(Mockito.any(), Mockito.any()))
                .thenReturn(CompletableFuture.completedFuture(httpResponse));

        DiemJsonRpcClient jsonRpcClient = new DiemJsonRpcClient(url, httpClient,
                () -> "5e0a1c17-d3ca-4813-817e-92f0680ccfdc");
        Optional<Account> account = jsonRpcClient.getAccount("b3f7e8e38f8c8393f281a2f0792a2849").get();

        assertTrue(account.isPresent());
        assertThat(account.get().address(), is(("b3f7e8e38f8c8393f281a2f0792a2849")));
    }

    @Test
    public void testErrorResponse() throws Exception {
        HttpResponse httpResponse = mock(HttpResponse.class);
        when(httpResponse.body()).thenReturn(
                "{\"diem_chain_id\":2,\"diem_ledger_version\":4875853,\"diem_ledger_timestampusec\":1604136151157457,\"jsonrpc\":\"2.0\",\"id\":\"0678e45d-b38b-423c-9d56-92116517fe82\",\"error\":{\"code\":-32602,\"message\":\"Invalid param account address(params[0]): should be hex-encoded string\",\"data\":null}}");
        when(httpClient.sendAsync(Mockito.any(), Mockito.any()))
                .thenReturn(CompletableFuture.completedFuture(httpResponse));

        DiemJsonRpcClient jsonRpcClient = new DiemJsonRpcClient(url, httpClient,
                () -> "0678e45d-b38b-423c-9d56-92116517fe82");

        assertThrows(ExecutionException.class,
                () -> jsonRpcClient.getAccount("b3f7e8e38f8c8393f281a2f0792a2849aa").get(),
                "-32602: Invalid param account address(params[0]): should be hex-encoded string");
    }

}
