package revolut.banking.exception.banking;

import revolut.banking.model.AccountId;

import java.math.BigDecimal;

public class InsufficientFundsException extends BankingException {

    private final AccountId accountId;
    private final BigDecimal amount;

    public InsufficientFundsException(AccountId accountId, BigDecimal amount) {
        this.accountId = accountId;
        this.amount = amount;
    }

    public AccountId getAccountId() {
        return accountId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    @Override
    public String getMessage() {
        return "Insufficient funds";
    }
}
