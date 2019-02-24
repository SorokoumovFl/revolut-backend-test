package revolut.banking.persistence.command;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import revolut.banking.eventsourcing.DummyAggregate;
import revolut.banking.eventsourcing.DummyAggregateId;
import revolut.banking.persistence.EventStoreRepository;

import static org.testng.Assert.*;

public class EventStoreRepositoryTest {

    private EventStoreRepository<DummyAggregateId, DummyAggregate> repository;

    @BeforeMethod
    public void setUp() {
        repository = new DummyEventStoreRepository();
    }

    @Test
    public void storeTest() {
        DummyAggregate aggregate = DummyAggregate.empty();
        repository.store(aggregate);

        assertTrue(repository.load(aggregate.getAggregateId()).isPresent());
        assertEquals(repository.load(aggregate.getAggregateId()).get().getAggregateId(), aggregate.getAggregateId());
    }

    @Test
    public void applyTest() {
        String msg1 = "War does not determine who is right";
        String msg2 = "Only who is left";

        DummyAggregate aggregate = DummyAggregate.empty();
        aggregate.changeMessage(msg1);
        repository.store(aggregate);
        assertEquals(repository.load(aggregate.getAggregateId()).get().getMessage(), msg1);

        DummyAggregate completelyNewInstance = repository.load(aggregate.getAggregateId()).get();
        completelyNewInstance.changeMessage(msg2);
        repository.store(completelyNewInstance);
        assertEquals(repository.load(completelyNewInstance.getAggregateId()).get().getMessage(), msg2);

    }

}
