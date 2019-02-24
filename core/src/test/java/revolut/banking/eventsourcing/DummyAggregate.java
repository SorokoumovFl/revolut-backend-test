package revolut.banking.eventsourcing;

import java.util.UUID;

public class DummyAggregate extends Aggregate<DummyAggregateId, DummyAggregate> {

    protected String message;

    public DummyAggregate(DummyAggregateId aggregateId) {
        super(aggregateId);
    }

    private DummyAggregate() {
        super(new DummyAggregateId(UUID.randomUUID().toString()));
        this.apply(new DummyCreatedEvent(this.getAggregateId()));
    }

    public void changeMessage(String message) {
        this.message = message;
        this.apply(new DummyEvent(this.getAggregateId(), nextVersion(), message));
    }

    public String getMessage() {
        return message;
    }

    public static DummyAggregate empty() {
        return new DummyAggregate();
    }
}
