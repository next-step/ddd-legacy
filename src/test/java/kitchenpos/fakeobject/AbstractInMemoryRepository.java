package kitchenpos.fakeobject;

import java.lang.reflect.Field;
import java.util.*;

public abstract class AbstractInMemoryRepository<ID, T> implements InMemoryRepository<ID, T> {
    protected Map<ID, T> maps = new HashMap<>();

    public Optional<T> findById(ID id) {
        return Optional.ofNullable(maps.get(id));
    }

    public T save(T t) {
        try {
            Field id1 = t.getClass().getDeclaredField("id");
            id1.setAccessible(true);

            if (Objects.isNull(id1.get(t))) {
                UUID uuid = UUID.randomUUID();
                id1.set(t, uuid);
                maps.put((ID) uuid, t);
            } else {
                maps.put((ID) id1.get(t), t);
            }

        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
        return t;
    }

    public List<T> findAll() {
        return new ArrayList(maps.values());
    }
}
