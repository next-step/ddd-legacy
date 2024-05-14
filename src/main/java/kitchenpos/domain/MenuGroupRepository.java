package kitchenpos.domain;

import jakarta.annotation.Nonnull;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MenuGroupRepository {
    @Nonnull
    MenuGroup save(@Nonnull MenuGroup menuGroup);

    @Nonnull
    List<MenuGroup> findAll();

    @Nonnull
    Optional<MenuGroup> findById(@Nonnull UUID menuGroupId);
}
