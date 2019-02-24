package revolut.banking.eventsourcing;


import revolut.banking.listener.EventListener;

import java.util.List;

public interface EventStore<E extends Event<?,?>> {

    List<E> loadEventStream(AggregateId aggregateId);

    void store(AggregateId aggregateId, long baseVersion, List<E> events);

    void addListener(EventListener listener);

}