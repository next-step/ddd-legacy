package kitchenpos.application;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuRepository;

public class MenuFakeRepository implements MenuRepository {

    private final Map<UUID, Menu> menus = new HashMap<>();


    @Override
    public Menu save(final Menu entity) {
        if (entity.getId() == null) {
            entity.setId(UUID.randomUUID());
        }

        menus.put(entity.getId(), entity);

        return entity;
    }

    @Override
    public Optional<Menu> findById(final UUID id) {
        return Optional.ofNullable(menus.get(id));
    }

    @Override
    public List<Menu> findAll() {
        return menus.values()
            .stream()
            .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public List<Menu> findAllByIdIn(final List<UUID> ids) {
        return ids.stream()
            .map(menus::get)
            .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public List<Menu> findAllByProductId(final UUID productId) {
        return menus.values()
            .stream()
            .filter(menu -> hasProduct(menu, productId))
            .collect(Collectors.toUnmodifiableList());
    }

    private boolean hasProduct(final Menu menu, final UUID targetProductId) {
        return menu.getMenuProducts()
            .stream()
            .anyMatch(menuProduct -> targetProductId.equals(menuProduct.getProductId()));
    }
}
