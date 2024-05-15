package kitchenpos.domain.testfixture;

import jakarta.persistence.Id;

import java.util.*;

public class InstancePocket<K, T> {
    Map<K, T> instancePocket;

    public InstancePocket() {
        this.instancePocket = new HashMap<>();
    }

    public T save(T instance) {
        var id = getId(instance);
        instancePocket.put((K) id, instance);
        return instance;
    }

    private <T> Object getId(T instance) {
        var idField = Arrays.stream(instance.getClass().getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(Id.class))
                .findAny()
                .orElseThrow(IllegalArgumentException::new);
        idField.setAccessible(true);
        try {
            return idField.get(instance);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException("아이디 값이 존재하지 않습니다.");
        }
    }

    public List<T> findAllByIdIn(List<K> ids) {
        return instancePocket.values()
                .stream()
                .filter(menu -> ids.contains(getId(menu)))
                .toList();
    }

    public List<T> findAll() {
        return instancePocket.values()
                .stream()
                .toList();
    }

    public Optional<T> findById(K id) {
        return Optional.ofNullable(instancePocket.get(id));
    }
}
