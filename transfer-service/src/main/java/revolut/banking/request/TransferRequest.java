package revolut.banking.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import revolut.banking.model.AccountId;

import java.math.BigDecimal;

public class TransferRequest {

    private AccountId payerId;
    private AccountId beneficiaryId;
    private BigDecimal amount;

    public TransferRequest(
            @JsonProperty("payer-id") AccountId payerId,
            @JsonProperty("beneficiary-id") AccountId beneficiaryId,
            @JsonProperty("amount") BigDecimal amount) {
        this.payerId = payerId;
        this.beneficiaryId = beneficiaryId;
        this.amount = amount;
    }

    public AccountId getPayerId() {
        return payerId;
    }

    public AccountId getBeneficiaryId() {
        return beneficiaryId;
    }

    public BigDecimal getAmount() {
        return amount;
    }
}
