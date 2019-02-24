package revolut.banking.service.command;

import revolut.banking.exception.banking.InsufficientFundsException;
import revolut.banking.exception.banking.NoSuchAccountException;
import revolut.banking.exception.banking.OperationCurrentlyUnavailableException;
import revolut.banking.model.AccountId;
import revolut.banking.persistence.command.AccountEventStoreRepository;
import revolut.banking.persistence.command.TransferEventStoreRepository;

import java.math.BigDecimal;

public interface BankingCommandService {

    AccountId createAccount(String ownerName);

    void changeAccount(AccountId id, String ownerName) throws NoSuchAccountException, OperationCurrentlyUnavailableException;

    void transfer(BigDecimal amount, AccountId payerId, AccountId beneficiaryId)
            throws NoSuchAccountException, OperationCurrentlyUnavailableException, InsufficientFundsException;

    static BankingCommandService create(AccountEventStoreRepository repository, TransferEventStoreRepository transferRepository) {
        return new BankingCommandServiceImpl(repository, transferRepository);
    }

}
