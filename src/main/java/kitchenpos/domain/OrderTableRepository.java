package kitchenpos.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderTableRepository {
    OrderTable save(OrderTable entity);
    List<OrderTable> findAll();
    Optional<OrderTable> findById(UUID uuid);
}

interface JpaOrderTableRepository extends OrderTableRepository, JpaRepository<OrderTable, UUID> {

}
