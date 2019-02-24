package revolut.banking.model;

import revolut.banking.eventsourcing.Aggregate;

public class Account extends Aggregate<AccountId, Account> {

    protected String owner;

    private Account() {
        super(AccountId.generate());
        this.apply(new AccountCreatedEvent(this.getAggregateId()));
    }

    public Account(AccountId accountId) {
        super(accountId);
    }

    public void changeOwnerName(String ownerName) {
        this.owner = ownerName;
        this.apply(new OwnerNameChangedEvent(this.getAggregateId(), nextVersion(), ownerName));
    }

    public static Account empty() {
        return new Account();
    }

}
