package revolut.banking.model;

import revolut.banking.eventsourcing.Event;

import java.math.BigDecimal;

public class TransferCommittedEvent extends Event<TransferId, Transfer> {

    private final BigDecimal amount;
    private final AccountId payerId;
    private final AccountId beneficiaryId;

    TransferCommittedEvent(long version, TransferId id, BigDecimal amount, AccountId payerId, AccountId beneficiaryId) {
        super(id, version);
        this.amount = amount;
        this.payerId = payerId;
        this.beneficiaryId = beneficiaryId;
    }

    @Override
    public void apply(Transfer aggregate) {

    }

    public BigDecimal getAmount() {
        return amount;
    }

    public AccountId getPayerId() {
        return payerId;
    }

    public AccountId getBeneficiaryId() {
        return beneficiaryId;
    }

}
