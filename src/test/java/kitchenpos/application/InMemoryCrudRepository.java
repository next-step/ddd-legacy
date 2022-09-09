package kitchenpos.application;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.data.repository.CrudRepository;

abstract class InMemoryCrudRepository<T, ID> implements CrudRepository<T, ID> {
    protected final Map<ID, T> storage = new HashMap<>();

    abstract ID selectId(T entity);

    @Override
    public <S extends T> S save(S entity) {
        storage.put(selectId(entity), entity);
        return entity;
    }

    @Override
    public <S extends T> Iterable<S> saveAll(Iterable<S> entities) {
        entities.forEach(this::save);
        return entities;
    }

    @Override
    public Optional<T> findById(ID id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public boolean existsById(ID id) {
        return storage.containsKey(id);
    }

    @Override
    public List<T> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public Iterable<T> findAllById(Iterable<ID> uuids) {
        var ids = new ArrayList<>();
        uuids.forEach(ids::add);

        return ids.stream()
                .map(storage::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public long count() {
        return storage.size();
    }

    @Override
    public void deleteById(ID id) {
        storage.remove(id);
    }

    @Override
    public void delete(T entity) {
        storage.remove(selectId(entity));
    }

    @Override
    public void deleteAllById(Iterable<? extends ID> ids) {
        ids.forEach(storage::remove);
    }

    @Override
    public void deleteAll(Iterable<? extends T> entities) {
        entities.forEach((e) -> storage.remove(selectId(e)));
    }

    @Override
    public void deleteAll() {
        storage.clear();
    }
}
