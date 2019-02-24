package revolut.banking.model;

import revolut.banking.eventsourcing.Event;

public class AccountCreatedEvent extends Event<AccountId, Account> {

    AccountCreatedEvent(AccountId id) {
        super(id, 0l);
    }

    @Override
    public void apply(Account account) {
    }

}
