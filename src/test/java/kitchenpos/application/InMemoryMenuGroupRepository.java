package kitchenpos.application;

import java.util.UUID;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;

public class InMemoryMenuGroupRepository extends InMemoryCrudRepository<MenuGroup, UUID> implements MenuGroupRepository {
    @Override
    UUID selectId(MenuGroup entity) {
        return entity.getId();
    }
}
