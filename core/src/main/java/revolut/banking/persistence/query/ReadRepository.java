package revolut.banking.persistence.query;

import revolut.banking.eventsourcing.Event;
import revolut.banking.listener.EventListener;
import revolut.banking.model.AccountCreatedEvent;
import revolut.banking.model.AccountId;
import revolut.banking.model.OwnerNameChangedEvent;
import revolut.banking.model.TransferCommittedEvent;
import revolut.banking.projection.AccountProjection;
import revolut.banking.projection.TransferProjection;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.LinkedBlockingQueue;

import static revolut.banking.listener.EventListener.inCase;
import static revolut.banking.listener.EventListener.switchType;

public class ReadRepository implements EventListener {

    private final Map<String, AccountProjection> accounts = new ConcurrentHashMap<>();
    private final Map<Long, TransferProjection> transfers = new ConcurrentSkipListMap<>();
    private final BlockingQueue<Event> updateQueue = new LinkedBlockingQueue<>();

    private final Thread updaterThread =
            new Thread(ReadRepositoryUpdater.create(updateQueue, this::storeEvent),"Account-Read-Updater");

    public ReadRepository() {
        updaterThread.start();
    }

    @Override
    public void handle(Event event) {
        updateQueue.add(event);
    }

    public Optional<AccountProjection> findAccount(AccountId id) {
        return Optional.ofNullable(accounts.get(id.toString()));
    }

    public Collection<TransferProjection> getTotalTransfersHistory() {
        return transfers.values();
    }

    private void storeEvent(Event event) {
        switchType(event,
                inCase(AccountCreatedEvent.class, e -> {
                    AccountProjection projection =
                            accounts.getOrDefault(event.getAggregateId().toString(), AccountProjection.empty());
                    projection.setAccountId(e.getAggregateId().toString());
                    storeAccountProjection(projection);
                }),
                inCase(OwnerNameChangedEvent.class, e -> {
                    AccountProjection projection =
                            accounts.getOrDefault(event.getAggregateId().toString(), AccountProjection.empty());
                    projection.setAccountOwner(e.getOwnerName());
                    storeAccountProjection(projection);
                }),
                inCase(TransferCommittedEvent.class, e -> {
                    if (e.getPayerId() != null) {
                        AccountProjection payerProjection =
                                accounts.getOrDefault(e.getPayerId().toString(), AccountProjection.empty());
                        payerProjection.setBalance(payerProjection.getBalance().subtract(e.getAmount()));
                        storeAccountProjection(payerProjection);
                    }

                    AccountProjection beneficiaryProjection =
                            accounts.getOrDefault(e.getBeneficiaryId().toString(), AccountProjection.empty());

                    TransferProjection transferProjection = TransferProjection.empty()
                            .setPayerId(e.getPayerId())
                            .setBeneficiaryId(e.getBeneficiaryId())
                            .setAmount(e.getAmount());

                    beneficiaryProjection.setBalance(beneficiaryProjection.getBalance().add(e.getAmount()));
                    storeAccountProjection(beneficiaryProjection);
                    storeTransferProjection(e.getVersion(), transferProjection);
                }));
    }

    private void storeAccountProjection(AccountProjection projection) {
        accounts.put(projection.getAccountId(), projection);
    }

    private void storeTransferProjection(long version, TransferProjection projection) {
        transfers.put(version, projection);
    }

}
