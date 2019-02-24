package revolut.banking.persistence.command;

import revolut.banking.eventsourcing.DummyAggregate;
import revolut.banking.eventsourcing.DummyAggregateId;
import revolut.banking.persistence.EventStoreRepository;

public class DummyEventStoreRepository extends EventStoreRepository<DummyAggregateId, DummyAggregate> {

    @Override
    protected DummyAggregate emptyInstance(DummyAggregateId id) {
        return new DummyAggregate(id);
    }

}
