package kitchenpos.bo.mock;

import kitchenpos.dao.MenuDao;
import kitchenpos.model.Menu;

import java.util.*;
import java.util.stream.Collectors;

public class TestMenuDao implements MenuDao {
    private static final Map<Long, Menu> data = new HashMap<>();

    @Override
    public Menu save(Menu entity) {
        data.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public Optional<Menu> findById(Long id) {
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public List<Menu> findAll() {
        return data.values()
                .stream()
                .collect(Collectors.toList());
    }

    @Override
    public long countByIdIn(List<Long> ids) {
        return ids.stream()
                .filter(id -> Objects.nonNull(data.get(id)))
                .count();
    }
}
