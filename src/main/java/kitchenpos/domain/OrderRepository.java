package kitchenpos.domain;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderRepository {

    Optional<Order> findById(UUID orderId);
    boolean existsByOrderTableAndStatusNot(OrderTable orderTable, OrderStatus status);

    Order save(Order order);

    List<Order> findAll();
}

interface JpaOrderRepository extends OrderRepository, JpaRepository<Order, UUID> {

}
