package kitchenpos.spy;

import kitchenpos.domain.Order;
import kitchenpos.domain.OrderRepository;

import java.util.UUID;

public interface SpyOrderRepository extends OrderRepository {

    default <T extends Order> T save(T order) {
        order.setId(UUID.randomUUID());
        return order;
    }
}
