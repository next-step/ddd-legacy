package kitchenpos.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Repository
public interface OrderTableRepository {
    OrderTable save(OrderTable orderTable);
    Optional<OrderTable> findById(UUID id);
    List<OrderTable> findAll();
}
