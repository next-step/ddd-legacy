package kitchenpos.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface JpaProductRepository extends ProductRepository, JpaRepository<Product, UUID> {

    @Override
    @Query("select p from Product p where p.id in :ids")
    List<Product> findAllByIdIn(List<UUID> ids);
}
