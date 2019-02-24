package revolut.banking.persistence.command;

import revolut.banking.model.Account;
import revolut.banking.model.AccountId;
import revolut.banking.persistence.EventStoreRepository;

public class AccountEventStoreRepository extends EventStoreRepository<AccountId, Account> {

    @Override
    protected Account emptyInstance(AccountId id) {
        return new Account(id);
    }

}
