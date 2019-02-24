package revolut.banking.model;

import revolut.banking.eventsourcing.AggregateId;

/**
    All transfers are grouped within a single aggregate.
    Sure, it could severely limit the eventual throughput of the system,
    but let's leave it that way for the sake of simplicity.

    Total load can also be reduced by using snapshots, but
    again, let's keep it simple.
 */

public enum  TransferId implements AggregateId {

    AGGREGATE_ID

}
