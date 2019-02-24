package revolut.banking.persistence;

import java.util.Optional;

public interface Repository<K, A> {

    Optional<A> load(K id);

    void store(A aggregate);

}
