package kitchenpos.domain;

import java.util.*;
import java.util.stream.Collectors;

public class InMemoryMenuRepository implements MenuRepository {

    Map<UUID, Menu> menus;

    public InMemoryMenuRepository() {
        menus = new HashMap<>();
    }

    @Override
    public List<Menu> findAllByProductId(UUID productId) {
        return Collections.emptyList();
    }

    @Override
    public Menu save(Menu menu) {
        menu.setId(UUID.randomUUID());
        menus.put(menu.getId(), menu);
        return menu;
    }

    @Override
    public Optional<Menu> findById(UUID uuid) {
        return Optional.ofNullable(menus.get(uuid));
    }

    @Override
    public List<Menu> findAll() {
        return new ArrayList<>(menus.values());
    }

    @Override
    public List<Menu> findAllById(List<UUID> uuids) {
        return menus.keySet().stream()
                .filter(uuids::contains)
                .map(uuid -> menus.get(uuid))
                .collect(Collectors.toList());
    }
}
