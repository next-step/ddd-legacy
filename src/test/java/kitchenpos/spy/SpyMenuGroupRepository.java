package kitchenpos.spy;

import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;

import java.util.UUID;

public interface SpyMenuGroupRepository extends MenuGroupRepository {

    default <T extends MenuGroup> T save(T menuGroup) {
        menuGroup.setId(UUID.randomUUID());
        return menuGroup;
    }
}
