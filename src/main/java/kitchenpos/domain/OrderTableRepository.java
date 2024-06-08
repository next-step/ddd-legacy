package kitchenpos.domain;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderTableRepository {
  OrderTable save(OrderTable orderTable);
  Optional<OrderTable> findById(UUID id);
  List<OrderTable> findAll();
}
