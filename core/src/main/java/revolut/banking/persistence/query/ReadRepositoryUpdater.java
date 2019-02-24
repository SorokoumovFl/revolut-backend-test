package revolut.banking.persistence.query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import revolut.banking.eventsourcing.Event;

import java.util.concurrent.BlockingQueue;
import java.util.function.Consumer;

public class ReadRepositoryUpdater implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger("Read Repository Updater");

    private final BlockingQueue<Event> eventsQueue;
    private final Consumer<Event> eventConsumer;

    private ReadRepositoryUpdater(BlockingQueue<Event> eventsQueue, Consumer<Event> eventConsumer) {
        this.eventsQueue = eventsQueue;
        this.eventConsumer = eventConsumer;
    }

    public static ReadRepositoryUpdater create(BlockingQueue<Event> eventsQueue, Consumer<Event> eventConsumer) {
        return new ReadRepositoryUpdater(eventsQueue, eventConsumer);
    }

    @Override
    public void run() {
        for (;;) {
            try {
                eventConsumer.accept(eventsQueue.take());
            } catch (InterruptedException e) {
                logger.error("Main thread was unexpectedly interrupted, halting read updater");
                Thread.currentThread().interrupt();
            }
        }
    }

}