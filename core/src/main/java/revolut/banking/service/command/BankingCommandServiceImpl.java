package revolut.banking.service.command;

import revolut.banking.exception.banking.InsufficientFundsException;
import revolut.banking.exception.banking.NoSuchAccountException;
import revolut.banking.exception.OptimisticLockingException;
import revolut.banking.exception.banking.OperationCurrentlyUnavailableException;
import revolut.banking.model.Account;
import revolut.banking.model.AccountId;
import revolut.banking.model.Transfer;
import revolut.banking.persistence.command.AccountEventStoreRepository;
import revolut.banking.persistence.command.TransferEventStoreRepository;

import java.math.BigDecimal;

public class BankingCommandServiceImpl implements BankingCommandService {

    private final AccountEventStoreRepository repository;
    private final TransferEventStoreRepository transferRepository;

    public BankingCommandServiceImpl(AccountEventStoreRepository repository, TransferEventStoreRepository transferRepository) {
        this.repository = repository;
        this.transferRepository = transferRepository;
    }

    @Override
    public AccountId createAccount(String ownerName) {
        final Account account = Account.empty();
        account.changeOwnerName(ownerName);
        repository.store(account);
        return account.getAggregateId();
    }

    @Override
    public void changeAccount(AccountId id, String ownerName) throws NoSuchAccountException, OperationCurrentlyUnavailableException {
        try {
            Account account = getAccount(id);
            account.changeOwnerName(ownerName);
            repository.store(account);
        } catch (OptimisticLockingException e) {
            throw new OperationCurrentlyUnavailableException();
        }
    }

    public Account getAccount(AccountId id) throws NoSuchAccountException {
        return repository.load(id).orElseThrow(NoSuchAccountException::new);
    }

    @Override
    public void transfer(BigDecimal amount, AccountId payerId, AccountId beneficiaryId)
            throws NoSuchAccountException, OperationCurrentlyUnavailableException, InsufficientFundsException {
        if (beneficiaryId == null)
            throw new NoSuchAccountException();

        if (beneficiaryId.equals(payerId))
            return;

        if (payerId != null) {
            getAccount(payerId);
            if (transferRepository.loadBalanceForAccount(payerId).compareTo(amount) < 0)
                throw new InsufficientFundsException(payerId, amount);
        }

        getAccount(beneficiaryId);

        Transfer transfer = transferRepository.prepare();
        transfer.transfer(payerId, beneficiaryId, amount, transfer.nextVersion());

        try {
            transferRepository.store(transfer);
        } catch (OptimisticLockingException e) {
            throw new OperationCurrentlyUnavailableException();
        }
    }

}
