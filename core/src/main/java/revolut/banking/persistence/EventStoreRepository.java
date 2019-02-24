package revolut.banking.persistence;

import revolut.banking.eventsourcing.Aggregate;
import revolut.banking.eventsourcing.AggregateId;
import revolut.banking.eventsourcing.Event;
import revolut.banking.eventsourcing.EventStore;
import revolut.banking.eventsourcing.impl.InMemoryEventStore;
import revolut.banking.listener.EventListener;

import java.util.List;
import java.util.Optional;

public abstract class EventStoreRepository<K extends AggregateId, A extends Aggregate<K, A>> implements Repository<K, A> {

    protected final EventStore<Event<K, A>> eventStore = new InMemoryEventStore();

    @Override
    public Optional<A> load(K id) {
        final List<Event<K, A>> events = eventStore.loadEventStream(id);
        if (events.isEmpty())
            return Optional.empty();

        final A aggregate = emptyInstance(id);
        events.forEach(event -> event.applyOn(aggregate));
        return Optional.of(aggregate);
    }

    @Override
    public void store(A aggregate) {
        eventStore.store(aggregate.getAggregateId(), aggregate.getBaseVersion(), aggregate.getEvents());
    }

    public void addListener(EventListener listener) {
        eventStore.addListener(listener);
    }

    protected abstract A emptyInstance(K id);

}
