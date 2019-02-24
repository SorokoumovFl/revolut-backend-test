package revolut.banking.model;

import revolut.banking.eventsourcing.Aggregate;

import java.math.BigDecimal;

public class Transfer extends Aggregate<TransferId, Transfer> {

    protected BigDecimal amount;
    protected AccountId payerId;
    protected AccountId beneficiaryId;

    private Transfer() {
        super(TransferId.AGGREGATE_ID);
    }

    public static Transfer empty() {
        return new Transfer();
    }

    public void transfer(AccountId payerId, AccountId beneficiaryId, BigDecimal amount, long version) {
        this.payerId = payerId;
        this.beneficiaryId = beneficiaryId;
        this.amount = amount;

        this.apply(new TransferCommittedEvent(version, this.getAggregateId(), amount, payerId, beneficiaryId));
    }

}
