package kitchenpos.dao;

import java.util.List;
import java.util.Optional;

import kitchenpos.model.Menu;

public interface MenuDao {
    Menu save(Menu entity);

    Optional<Menu> findById(Long id);

    List<Menu> findAll();

    long countByIdIn(List<Long> ids);
}
