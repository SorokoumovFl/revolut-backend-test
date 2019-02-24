package revolut.banking.eventsourcing;


public abstract class Event<K extends AggregateId, A extends Aggregate<K, A>> {

    private final K aggregateId;
    private final long version;

    protected Event(K aggregateId, long version) {
        this.aggregateId = aggregateId;
        this.version = version;
    }

    public K getAggregateId() {
        return aggregateId;
    }

    public long getVersion() {
        return version;
    }

    public abstract void apply(A aggregate);

    public void applyOn(A aggregate) {
        aggregate.setBaseVersion(getVersion());
        apply(aggregate);
    }

}
