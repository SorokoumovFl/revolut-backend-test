package revolut.banking.projection;

import com.fasterxml.jackson.annotation.JsonProperty;
import revolut.banking.model.AccountId;

import java.math.BigDecimal;

public class TransferProjection {

    @JsonProperty("payer-id")
    private AccountId payerId;

    @JsonProperty("beneficiary-id")
    private AccountId beneficiaryId;

    @JsonProperty("amount")
    private BigDecimal amount;

    private TransferProjection() {
    }

    public static TransferProjection empty() {
        return new TransferProjection();
    }

    public AccountId getPayerId() {
        return payerId;
    }

    public TransferProjection setPayerId(AccountId payerId) {
        this.payerId = payerId;
        return this;
    }

    public AccountId getBeneficiaryId() {
        return beneficiaryId;
    }

    public TransferProjection setBeneficiaryId(AccountId beneficiaryId) {
        this.beneficiaryId = beneficiaryId;
        return this;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public TransferProjection setAmount(BigDecimal amount) {
        this.amount = amount;
        return this;
    }

}
