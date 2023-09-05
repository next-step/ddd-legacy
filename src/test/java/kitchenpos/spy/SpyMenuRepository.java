package kitchenpos.spy;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuRepository;

import java.util.UUID;

public interface SpyMenuRepository extends MenuRepository {

    default <T extends Menu> T save(T menu) {
        menu.setId(UUID.randomUUID());
        return menu;
    }
}
