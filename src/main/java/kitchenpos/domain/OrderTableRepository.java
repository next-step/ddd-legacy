package kitchenpos.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderTableRepository {
    Optional<OrderTable> findById(UUID orderTableId);

    OrderTable save(OrderTable orderTable);

    List<OrderTable> findAll();
}

interface JpaOrderTableRepository extends OrderTableRepository, JpaRepository<OrderTable, UUID> {

}