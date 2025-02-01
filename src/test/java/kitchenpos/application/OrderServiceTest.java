package kitchenpos.application;

import kitchenpos.application.fixture.MenuFixture;
import kitchenpos.application.fixture.OrderLineItemFixture;
import kitchenpos.application.fixture.OrderFixture;
import kitchenpos.domain.*;
import kitchenpos.domain.Order;
import kitchenpos.infra.KitchenridersClient;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @InjectMocks
    OrderService orderService;
    @Mock
    OrderRepository orderRepository;
    @Mock
    MenuRepository menuRepository;
    @Mock
    OrderTableRepository orderTableRepository;
    @Mock
    KitchenridersClient kitchenridersClient;

    @Test
    @DisplayName("주문 생성시 상태는 WAITTING 으로 생성된다.")
    void createOrder() {
        Order order = OrderFixture.makeOrder(OrderType.EAT_IN, OrderLineItemFixture.DEFAULT_ORDER_LINE_ITEM);


        given(menuRepository.findAllByIdIn(any())).willReturn(List.of(MenuFixture.DEFAULT_MENU));
        given(menuRepository.findById(any())).willReturn(Optional.of(MenuFixture.DEFAULT_MENU));
        given(orderTableRepository.findById(any())).willReturn(Optional.of(OrderFixture.DEFAULT_ORDER_TABLE));

        given(orderRepository.save(any())).willReturn(order);

        Order createdOrder = orderService.create(order);
        assertAll(
                () -> assertThat(createdOrder).isNotNull(),
                () -> assertThat(createdOrder.getId()).isInstanceOf(UUID.class),
                () -> assertThat(createdOrder.getType()).isEqualTo(OrderType.EAT_IN),
                () -> assertThat(createdOrder.getStatus()).isEqualTo(OrderStatus.WAITING)
        );
    }

    
}
