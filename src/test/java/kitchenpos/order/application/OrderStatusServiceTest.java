package kitchenpos.order.application;

import kitchenpos.order.domain.Order;
import kitchenpos.order.domain.OrderRepository;
import kitchenpos.order.domain.OrderType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static kitchenpos.order.domain.OrderFixture.orderLineItems;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
@SpringBootTest
@DisplayName("주문 상태")
class OrderStatusServiceTest {

    @Autowired
    private OrderStatusService orderStatusService;

    @Autowired
    private OrderRepository orderRepository;

    private Order order;

    @BeforeEach
    void setUp() {
        order = orderRepository.save(new Order(UUID.randomUUID(), OrderType.TAKEOUT, orderLineItems(), null, null));
    }

    @DisplayName("WAITING 상태가 아니면 접수를 받을 수 없다.")
    @Test
    void name() {
        orderStatusService.accept(order.getId());
        assertThatThrownBy(() -> orderStatusService.accept(order.getId()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("WAITING 상태만 접수가능합니다.");
    }
}
