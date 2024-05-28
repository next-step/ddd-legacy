package kitchenpos.infra;

import jakarta.annotation.Nonnull;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface JpaProductRepository extends ProductRepository, JpaRepository<Product, UUID> {
    @Nonnull
    List<Product> findAllByIdIn(@Nonnull List<UUID> ids);
}
