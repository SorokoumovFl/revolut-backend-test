package revolut.banking.service.query;

import revolut.banking.exception.banking.NoSuchAccountException;
import revolut.banking.model.AccountId;
import revolut.banking.projection.AccountProjection;
import revolut.banking.projection.TransferProjection;

import java.util.Collection;

public interface BankingQueryService {

    AccountProjection getAccount(AccountId id) throws NoSuchAccountException;

    Collection<TransferProjection> getTotalTransfersHistory();

}
