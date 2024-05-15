package kitchenpos.testfixture;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuRepository;

import java.util.*;
import java.util.stream.Collectors;

public class InMemoryMenuRepository implements MenuRepository {
    private final Map<UUID, Menu> menus = new HashMap<>();

    @Override
    public Menu save(Menu entity) {
        menus.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public List<Menu> findAll() {
        return new ArrayList<>(menus.values());
    }

    @Override
    public Optional<Menu> findById(UUID id) {
        return Optional.ofNullable(menus.get(id));
    }

    @Override
    public List<Menu> findAllByIdIn(List<UUID> ids) {
        return ids.stream()
                .map(menus::get)
                .filter(Objects::nonNull)
                .toList();
    }

    @Override
    public List<Menu> findAllByProductId(UUID productId) {
        return menus.values()
                .stream()
                .filter(menu-> menu.getMenuProducts()
                        .stream()
                        .anyMatch(menuProduct -> menuProduct.getProductId().equals(productId))).toList();
    }
}
