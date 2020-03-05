package kitchenpos.dao;

import kitchenpos.model.Menu;

import java.util.*;
import java.util.stream.Stream;

public class TestMenuDao implements MenuDao {

    private Map<Long, Menu> menus = new HashMap<>();

    @Override
    public Menu save(Menu entity) {
        Objects.requireNonNull(entity.getId());
        Objects.requireNonNull(entity);

        menus.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public Optional<Menu> findById(Long id) {
        Objects.requireNonNull(id);
        return Optional.ofNullable(menus.get(id));
    }

    @Override
    public List<Menu> findAll() {
        return new ArrayList<Menu>(menus.values());
    }

    @Override
    public long countByIdIn(List<Long> ids) {
        return ids.stream()
                .filter(i -> Objects.nonNull(menus.get(i)))
                .count();

    }
}
