package dev.jlibra.client.views.transaction;

import java.util.List;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Value.Immutable
@JsonDeserialize(as = ImmutableUserTransaction.class)
public interface UserTransaction extends TransactionData {

    @JsonProperty("expiration_timestamp_secs")
    Long expirationTimestampSecs();

    @JsonProperty("gas_currency")
    String gasUCurrency();

    @JsonProperty("gas_unit_price")
    Long gasUnitPrice();

    @JsonProperty("max_gas_amount")
    Long maxGasAmount();

    @JsonProperty("public_key")
    String publicKey();

    @JsonProperty("script_hash")
    String scriptHash();

    @JsonProperty("sender")
    String sender();

    @JsonProperty("sequence_number")
    Long sequenceNumber();

    @JsonProperty("signature")
    String signature();

    @JsonProperty("signature_scheme")
    String signatureScheme();

    @JsonProperty("script")
    Script script();

    @JsonProperty("chain_id")
    Integer chainId();

    @JsonProperty("script_bytes")
    String scriptBytes();
    
    @JsonProperty("secondary_signers")
    List<String> secondarySigners();
    
    @JsonProperty("secondary_signature_schemes")
    List<String> secondarySignatureSchemes();
    
    @JsonProperty("secondary_signatures")
    List<String> secondarySignatures();
    
    @JsonProperty("secondary_public_keys")
    List<String> secondaryPublicKeys();
    
}
