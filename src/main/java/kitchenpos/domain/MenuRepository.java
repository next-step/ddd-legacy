package kitchenpos.domain;

import jakarta.annotation.Nonnull;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MenuRepository {
    @Nonnull
    Menu save(@Nonnull Menu menu);

    @Nonnull
    List<Menu> findAll();

    @Nonnull
    List<Menu> findAllByIdIn(@Nonnull List<UUID> list);

    @Nonnull
    List<Menu> findAllByProductId(@Nonnull UUID productId);

    @Nonnull
    Optional<Menu> findById(@Nonnull UUID menuId);
}
