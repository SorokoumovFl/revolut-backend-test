package revolut.banking.eventsourcing;

import java.util.ArrayList;
import java.util.List;

public abstract class Aggregate<K extends AggregateId, A extends Aggregate<K, A>> {

    private K aggregateId;
    private long baseVersion;

    private List<Event<K, A>> events;

    private Aggregate(K aggregateId, List<Event<K, A>> events) {
        this.aggregateId = aggregateId;
        this.events = events;
    }

    protected Aggregate(K aggregateId) {
        this(aggregateId, new ArrayList<>());
    }

    protected void apply(Event<K,A> event) {
        this.events.add(event);
    }

    public List<Event<K, A>> getEvents() {
        return events;
    }

    public K getAggregateId() {
        return aggregateId;
    }

    public void setBaseVersion(long baseVersion) {
        this.baseVersion = baseVersion;
    }

    public long getBaseVersion() {
        return baseVersion;
    }

    public long nextVersion() {
        return baseVersion + events.size() + 1;
    }
}
