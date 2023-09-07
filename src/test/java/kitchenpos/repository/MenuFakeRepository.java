package kitchenpos.repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuRepository;

public class MenuFakeRepository implements MenuRepository {

    private final List<Menu> menus = new ArrayList<>();

    @Override
    public List<Menu> findAllByIdIn(List<UUID> ids) {
        return menus.stream()
            .filter(it -> ids.contains(it.getId()))
            .collect(Collectors.toList());
    }

    @Override
    public List<Menu> findAllByProductId(UUID productId) {
        return menus.stream()
            .filter(menu -> menu.getMenuProducts().stream().anyMatch(menuProduct -> menuProduct.getProductId().equals(productId)))
            .collect(Collectors.toList());
    }

    @Override
    public List<Menu> findAll() {
        return Collections.unmodifiableList(menus);
    }

    @Override
    public Menu save(Menu entity) {
        boolean isExist = menus.stream()
            .anyMatch(it -> entity.getId().equals(it.getId()));
        if (isExist) {
            throw new IllegalArgumentException();
        }

        menus.add(entity);
        return entity;
    }

    @Override
    public Optional<Menu> findById(UUID uuid) {
        return menus.stream()
            .filter(it -> uuid.equals(it.getId()))
            .findAny();
    }
}
