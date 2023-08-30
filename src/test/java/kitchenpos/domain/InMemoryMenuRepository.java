package kitchenpos.domain;

import java.util.*;
import java.util.stream.Collectors;

public class InMemoryMenuRepository implements MenuRepository {

    Map<UUID, Menu> map = new HashMap<>();

    @Override
    public Menu save(Menu entity) {
        map.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public Optional<Menu> findById(UUID uuid) {
        return Optional.ofNullable(map.get(uuid));
    }

    @Override
    public List<Menu> findAllByIdIn(List<UUID> ids) {
        return ids.stream()
                .map(it -> map.get(it))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public List<Menu> findAll() {
        return new ArrayList<>(map.values());
    }

    @Override
    public List<Menu> findAllByProductId(UUID productId) {
        return map.values().stream()
                .filter(menu -> menu.getMenuProducts()
                                .stream()
                                .anyMatch(it -> it.getProductId().equals(productId))
                ).collect(Collectors.toList());
    }
}
