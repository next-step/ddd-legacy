package kitchenpos.dao;

import kitchenpos.model.Menu;
import org.springframework.util.CollectionUtils;

import java.util.*;

public class TestMenuDao implements MenuDao {

    private final Map<Long, Menu> menus = new HashMap();

    @Override
    public Menu save(Menu entity) {
        menus.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public List<Menu> findAll() {
        return new ArrayList(menus.values());
    }

    @Override
    public long countByIdIn(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return 0;
        }

        return menus.keySet()
                .stream()
                .filter(key -> ids.contains(key))
                .count();
    }
}
