package kitchenpos.bo.mock;

import kitchenpos.dao.Interface.MenuGroupDao;
import kitchenpos.model.MenuGroup;

import java.util.*;
import java.util.stream.Collectors;

public class TestMenuGroupDao implements MenuGroupDao {

    private static final Map<Long, MenuGroup> data = new HashMap<>();

    @Override
    public MenuGroup save(MenuGroup entity) {
        data.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public Optional<MenuGroup> findById(Long id) {
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public List<MenuGroup> findAll() {
        return data.values()
                .stream()
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsById(Long id) {
        return Objects.nonNull(data.get(id));
    }
}
