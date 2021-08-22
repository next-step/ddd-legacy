package kitchenpos.infra;

import kitchenpos.domain.Order;
import kitchenpos.domain.OrderRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

interface JpaOrderRepository extends OrderRepository, JpaRepository<Order, UUID> {

}
