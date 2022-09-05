package kitchenpos.application;

import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTableRepository;
import kitchenpos.domain.OrderType;
import kitchenpos.infra.KitchenridersClient;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public abstract class OrderServiceTestSupport {
    protected static Order createOrderBy(OrderType type, OrderStatus status) {
        final var order = new Order();
        order.setType(type);
        order.setStatus(status);
        return order;
    }

    protected static Order createOrderOfType(OrderType type) {
        final var order = new Order();
        order.setType(type);
        return order;
    }

    protected static Order createOrderWithStatus(OrderStatus status) {
        final var order = new Order();
        order.setStatus(status);
        return order;
    }

    @Mock
    protected OrderRepository orderRepository;
    @Mock
    protected MenuRepository menuRepository;
    @Mock
    protected OrderTableRepository orderTableRepository;
    @Mock
    protected KitchenridersClient kitchenridersClient;

    @InjectMocks
    protected OrderService testService;
}
