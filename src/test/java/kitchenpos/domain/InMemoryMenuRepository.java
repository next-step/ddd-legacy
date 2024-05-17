package kitchenpos.domain;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class InMemoryMenuRepository implements MenuRepository {

    private final Map<UUID, Menu> map = new LinkedHashMap<>();

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
                .map(map::get)
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
                ).toList();
    }
}
