package revolut.banking.eventsourcing;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import revolut.banking.eventsourcing.impl.InMemoryEventStore;
import revolut.banking.exception.OptimisticLockingException;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.testng.Assert.*;

public class InMemoryEventStoreTest {

    private EventStore store;

    @BeforeMethod
    public void setUp() {
        store = new InMemoryEventStore();
    }

    @Test
    public void simpleEventStoringTest() {
        DummyAggregateId id = new DummyAggregateId("testId");
        store.store(id, 0, Arrays.asList(new DummyEvent(id, 0l, "")));

        List<Event> events = store.loadEventStream(id);
        assertEquals(events.size(), 1);
        assertEquals(events.get(0).getVersion(), 0l);

        store.store(id, 0, Arrays.asList(
                new DummyEvent(id, 1l, ""),
                new DummyEvent(id, 2l, "")
        ));

        events = store.loadEventStream(id);

        assertEquals(events.size(), 3);
    }

    @Test(expectedExceptions = OptimisticLockingException.class)
    public void optimisticLockingTest() {
        DummyAggregateId id = new DummyAggregateId("testId");

        store.store(id, 0, Arrays.asList(
                new DummyEvent(id, 0l, ""),
                new DummyEvent(id, 1l, "")
        ));

        List<Event> events = store.loadEventStream(id);
        assertEquals(events.size(), 2);

        store.store(id, 0, Arrays.asList(new DummyEvent(id, 2l, "")));
    }

    @Test
    public void separateAggregatesTest() {
        DummyAggregateId id1 = new DummyAggregateId("testId1");
        DummyAggregateId id2 = new DummyAggregateId("testId2");

        for (DummyAggregateId id : new DummyAggregateId[] {id1, id2}) {
            store.store(id, 0, Arrays.asList(
                    new DummyEvent(id, 0l, ""),
                    new DummyEvent(id, 1l, "")
            ));
            List<Event> events = store.loadEventStream(id);
            assertEquals(events.size(), 2);
        }
    }

    @Test
    public void eventListenerTest() {
        AtomicInteger eventsHandled = new AtomicInteger(0);
        DummyAggregateId id = new DummyAggregateId("testId");

        store.addListener(event -> eventsHandled.incrementAndGet());

        store.store(id, 0l, Arrays.asList(
                new DummyEvent(id, 0l, ""),
                new DummyEvent(id, 1l, "")
        ));

        assertEquals(eventsHandled.get(), 2);
    }
}
