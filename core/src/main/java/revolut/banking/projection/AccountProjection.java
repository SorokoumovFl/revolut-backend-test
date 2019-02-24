package revolut.banking.projection;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public class AccountProjection {

    @JsonProperty("account-id")
    private String accountId;

    @JsonProperty("owner-name")
    private String accountOwner;

    @JsonProperty("balance")
    private BigDecimal balance = BigDecimal.ZERO;

    private AccountProjection() {
    }

    public static AccountProjection empty() {
        return new AccountProjection();
    }

    public String getAccountId() {
        return accountId;
    }

    public String getAccountOwner() {
        return accountOwner;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public void setAccountOwner(String accountOwner) {
        this.accountOwner = accountOwner;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

}
