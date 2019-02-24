package revolut.banking.model;

import com.fasterxml.jackson.annotation.JsonValue;
import revolut.banking.eventsourcing.AggregateId;

import java.util.Objects;
import java.util.UUID;


public final class AccountId implements AggregateId {

    private final String value;

    private AccountId(String value) {
        this.value = value;
    }

    public static AccountId generate() {
        return new AccountId(UUID.randomUUID().toString());
    }

    public static AccountId from(String id) {
        return new AccountId(id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AccountId accountId = (AccountId) o;

        return Objects.equals(value, accountId.value);
    }

    @Override
    public int hashCode() {
        return value != null ? value.hashCode() : 0;
    }

    @Override
    @JsonValue
    public String toString() {
        return value;
    }
}
