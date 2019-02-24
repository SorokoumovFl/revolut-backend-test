package revolut.banking.persistence.command;

import revolut.banking.eventsourcing.Event;
import revolut.banking.model.AccountId;
import revolut.banking.model.Transfer;
import revolut.banking.model.TransferCommittedEvent;
import revolut.banking.model.TransferId;
import revolut.banking.persistence.EventStoreRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.UnaryOperator;


public class TransferEventStoreRepository extends EventStoreRepository<TransferId, Transfer> {

    public Transfer prepare() {
        return load(TransferId.AGGREGATE_ID).orElse(Transfer.empty());
    }

    public BigDecimal loadBalanceForAccount(AccountId id) {
        List<Event<TransferId, Transfer>> events = eventStore.loadEventStream(TransferId.AGGREGATE_ID);

        return events.stream()
                .filter(e -> e.getClass().equals(TransferCommittedEvent.class))
                .map(e -> (TransferCommittedEvent)e)
                .<Function<BigDecimal, BigDecimal>>map(e -> Objects.equals(e.getPayerId(), id)
                        ? amount -> amount.subtract(e.getAmount())
                        : (Objects.equals(e.getBeneficiaryId(), id)
                            ? amount -> amount.add(e.getAmount())
                            : UnaryOperator.identity()))
                .reduce(Function.identity(), (a, b) -> a.andThen(b))
                .apply(BigDecimal.ZERO);
    }

    @Override
    protected Transfer emptyInstance(TransferId id) {
        return Transfer.empty();
    }

}
