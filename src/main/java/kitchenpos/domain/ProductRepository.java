package kitchenpos.domain;

import jakarta.annotation.Nonnull;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository {
    @Nonnull
    List<Product> findAllByIdIn(@Nonnull List<UUID> list);

    @Nonnull
    Optional<Product> findById(@Nonnull UUID productId);

    @Nonnull
    List<Product> findAll();

    @Nonnull
    Product save(@Nonnull Product product);
}
