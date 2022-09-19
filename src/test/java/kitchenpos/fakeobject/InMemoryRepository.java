package kitchenpos.fakeobject;


import java.util.List;
import java.util.Optional;

public interface InMemoryRepository<ID, T> {
    Optional<T> findById(ID id);

    T save(T t);

    List<T> findAll();
}
