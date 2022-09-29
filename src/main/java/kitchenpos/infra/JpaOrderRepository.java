package kitchenpos.infra;

import kitchenpos.domain.*;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.RepositoryDefinition;

import java.util.UUID;

@RepositoryDefinition(domainClass = Order.class, idClass = UUID.class)
public interface JpaOrderRepository extends OrderRepository, JpaSpecificationExecutor<Order> {
    boolean existsByOrderTableAndStatusNot(OrderTable orderTable, OrderStatus status);
}
