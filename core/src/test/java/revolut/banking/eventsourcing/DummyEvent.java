package revolut.banking.eventsourcing;

public class DummyEvent extends Event<DummyAggregateId, DummyAggregate> {

    private final String message;

    protected DummyEvent(DummyAggregateId id, long version, String message) {
        super(id, version);
        this.message = message;
    }

    @Override
    public void apply(DummyAggregate aggregate) {
        aggregate.message = message;
    }

}
