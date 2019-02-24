package revolut.banking.service.query;

import revolut.banking.exception.banking.NoSuchAccountException;
import revolut.banking.model.AccountId;
import revolut.banking.projection.AccountProjection;
import revolut.banking.persistence.query.ReadRepository;
import revolut.banking.projection.TransferProjection;

import java.util.Collection;

public class BankingQueryServiceImpl implements BankingQueryService {

    private final ReadRepository readRepository;

    public BankingQueryServiceImpl(ReadRepository readRepository) {
        this.readRepository = readRepository;
    }

    @Override
    public AccountProjection getAccount(AccountId id) throws NoSuchAccountException {
        return readRepository.findAccount(id).orElseThrow(NoSuchAccountException::new);
    }

    @Override
    public Collection<TransferProjection> getTotalTransfersHistory() {
        return readRepository.getTotalTransfersHistory();
    }

}
