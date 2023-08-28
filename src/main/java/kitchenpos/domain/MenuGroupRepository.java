package kitchenpos.domain;

import java.util.List;

public interface MenuGroupRepository {

    MenuGroup save(final MenuGroup entity);

    List<MenuGroup> findAll();
}
