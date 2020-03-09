package kitchenpos.dao;

import kitchenpos.model.Menu;
import kitchenpos.support.MenuBuilder;

import java.util.*;

public class InMemoryMenuDao implements MenuDao{

    private final Map<Long, Menu> entities = new HashMap<>();

    @Override
    public Menu save(Menu entity) {
        Menu menu = new MenuBuilder()
            .id(entity.getId())
            .name(entity.getName())
            .menuProducts(entity.getMenuProducts() == null ? new ArrayList<>() : new ArrayList<>(entity.getMenuProducts()))
            .menuGroupId(entity.getMenuGroupId())
            .price(entity.getPrice())
            .build();
        entities.put(menu.getId(), menu);
        return menu;
    }

    @Override
    public Optional<Menu> findById(Long id) {
        return Optional.ofNullable(entities.get(id));
    }

    @Override
    public List<Menu> findAll() {
        return new ArrayList<>(entities.values());
    }

    @Override
    public long countByIdIn(List<Long> ids) {
        return ids.stream().filter(id -> entities.containsKey(id)).count();
    }
}
