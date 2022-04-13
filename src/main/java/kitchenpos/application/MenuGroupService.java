package kitchenpos.application;

import java.util.List;
import kitchenpos.domain.MenuGroup;

public interface MenuGroupService {

    MenuGroup create(final MenuGroup request);

    List<MenuGroup> findAll();

}
