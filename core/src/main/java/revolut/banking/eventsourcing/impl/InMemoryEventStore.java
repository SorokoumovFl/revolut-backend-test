package revolut.banking.eventsourcing.impl;


import revolut.banking.eventsourcing.AggregateId;
import revolut.banking.eventsourcing.Event;
import revolut.banking.eventsourcing.EventStore;
import revolut.banking.exception.OptimisticLockingException;
import revolut.banking.listener.EventListener;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

/**
 * Implements in-memory event store and allows to intercept successfully stored events.
 *
 * I would rather use event-bus in case of event distribution, but I want to keep
 * dependencies list as short as possible.
 */
public class InMemoryEventStore<E extends Event<?,?>> implements EventStore<E> {

    private Map<AggregateId, List<E>> events = new ConcurrentHashMap<>();
    private Set<EventListener> listeners = new CopyOnWriteArraySet<>();

    @Override
    public synchronized List<E> loadEventStream(AggregateId aggregateId) {
        return Collections.unmodifiableList(events.getOrDefault(aggregateId, emptyList()));
    }

    @Override
    public synchronized void store(AggregateId aggregateId, long baseVersion, List<E> changes) {
        events.merge(aggregateId, changes, (oldValue, value) -> {
            if (baseVersion != oldValue.get(oldValue.size() - 1).getVersion())
                throw new OptimisticLockingException();

            return Stream.concat(oldValue.stream(), value.stream()).collect(toList());
        });

        listeners.forEach(listener -> changes.forEach(listener::handle));
    }

    @Override
    public void addListener(EventListener listener) {
        listeners.add(listener);
    }

}
