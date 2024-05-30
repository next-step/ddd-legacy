package kitchenpos.repository;

import java.util.*;

public class BaseInMemoryDao<T> {
    private final Map<UUID, T> store = new HashMap<>();


    public Collection<T> getAll() {
        return store.values();
    }

    public T save(T entity) {
        UUID id = UUID.randomUUID();
        try {
            // Assuming the entity class has a method getId(), this part needs adjustment based on actual entity design.
            id = (UUID) entity.getClass().getMethod("getId").invoke(entity);
            if (id == null) {
                id = UUID.randomUUID();
                entity.getClass().getMethod("setId", UUID.class).invoke(entity, id);
            }
        } catch (Exception e) {
            throw new RuntimeException("Entity must have getId and setId methods");
        }
        store.put(id, entity);
        return entity;
    }

    public Optional<T> findById(UUID id) {
        return Optional.ofNullable(store.get(id));
    }


    public List<T> findAll() {
        return new ArrayList<>(store.values());
    }

    public List<T> findAllByIdIn(List<UUID> ids) {
        List<T> entities = new ArrayList<>();
        for (UUID id : ids) {
            T entity = store.get(id);
            if (entity != null) {
                entities.add(entity);
            }
        }
        return entities;
    }
}
