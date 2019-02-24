package revolut.banking.model;

import revolut.banking.eventsourcing.Event;

public class OwnerNameChangedEvent extends Event<AccountId, Account> {

    private final String ownerName;

    OwnerNameChangedEvent(AccountId id, long version, String ownerName) {
        super(id, version);
        this.ownerName = ownerName;
    }

    public String getOwnerName() {
        return ownerName;
    }

    @Override
    public void apply(Account account) {
        account.owner = ownerName;
    }

}
