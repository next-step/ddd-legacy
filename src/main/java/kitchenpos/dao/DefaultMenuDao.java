package kitchenpos.dao;

import kitchenpos.model.Menu;

import java.util.List;
import java.util.Optional;

public interface DefaultMenuDao {
    Menu save(Menu entity);

    Optional<Menu> findById(Long id);

    List<Menu> findAll();

    long countByIdIn(List<Long> ids);
}
