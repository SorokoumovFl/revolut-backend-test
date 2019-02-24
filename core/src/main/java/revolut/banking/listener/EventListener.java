package revolut.banking.listener;

import revolut.banking.eventsourcing.Event;

import java.util.Optional;
import java.util.function.Consumer;

@FunctionalInterface
public interface EventListener {

    void handle(Event<?, ?> event);

    static void switchType(Object obj, Consumer... cases) {
        for (Consumer consumer : cases)
            consumer.accept(obj);
    }

    static <T> Consumer inCase(Class<T> clazz, Consumer<T> consumer) {
        return obj -> Optional
                .of(obj)
                .filter(clazz::isInstance)
                .map(clazz::cast)
                .ifPresent(consumer);
    }

}
