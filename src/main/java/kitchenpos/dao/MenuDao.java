package kitchenpos.dao;

import kitchenpos.model.Menu;

import java.util.List;

public interface MenuDao {

    Menu save(Menu entity);

    List<Menu> findAll();

    long countByIdIn(List<Long> ids);
}
