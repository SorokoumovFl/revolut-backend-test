package revolut.banking.eventsourcing;

public class DummyCreatedEvent extends Event<DummyAggregateId, DummyAggregate> {

    protected DummyCreatedEvent(DummyAggregateId id) {
        super(id, 0l);
    }

    @Override
    public void apply(DummyAggregate aggregate) {
    }

}
