package revolut.banking.eventsourcing;

import java.util.Objects;

public class DummyAggregateId implements AggregateId {

    private final String value;

    public DummyAggregateId(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DummyAggregateId that = (DummyAggregateId) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return super.toString();
    }
}