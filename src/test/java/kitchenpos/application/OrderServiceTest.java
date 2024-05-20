package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.infra.KitchenridersClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static kitchenpos.MoneyConstants.만원;
import static kitchenpos.fixture.MenuFixture.createMenu;
import static kitchenpos.fixture.OrderFixture.createOrder;
import static kitchenpos.fixture.OrderTableFixture.createSittingTable;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private MenuRepository menuRepository;
    @Mock
    private OrderTableRepository orderTableRepository;
    @Mock
    private KitchenridersClient kitchenridersClient;

    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderService = new OrderService(orderRepository, menuRepository, orderTableRepository, kitchenridersClient);
    }

    @Nested
    @DisplayName("주문 접수")
    class Waiting {

        @Nested
        @DisplayName("매장 주문")
        class EatIn {

            @Test
            @DisplayName("매장주문을 접수받을 수 있다.")
            void success() {
                final var menu = createMenu("치킨", 만원);
                final var orderTable = createSittingTable(2);
                final var order = createOrder(OrderType.EAT_IN, menu, orderTable);

                final var response = createOrder(OrderType.EAT_IN, menu, orderTable);

                given(menuRepository.findAllByIdIn(any())).willReturn(List.of(menu));
                given(menuRepository.findById(any())).willReturn(Optional.of(menu));
                given(orderTableRepository.findById(order.getOrderTableId())).willReturn(Optional.of(orderTable));
                given(orderRepository.save(any())).willReturn(response);

                Order actual = orderService.create(order);

                assertThat(actual).isNotNull();
            }

            @Test
            void name() {
            }

        }
    }
}
