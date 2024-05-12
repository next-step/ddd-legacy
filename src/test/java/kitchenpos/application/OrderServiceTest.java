package kitchenpos.application;

import kitchenpos.application.testFixture.OrderFixture;
import kitchenpos.application.testFixture.OrderTableFixture;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderTableRepository;
import kitchenpos.infra.KitchenridersClient;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static kitchenpos.domain.OrderStatus.SERVED;
import static kitchenpos.domain.OrderStatus.WAITING;
import static org.mockito.BDDMockito.given;

@DisplayName("주문(Order) 서비스 테스트")
@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

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

    @Test
    void create() {
    }

    @Test
    void accept() {
    }

    @Test
    void serve() {
    }

    @Test
    void startDelivery() {
    }

    @Test
    void completeDelivery() {
    }

    @Test
    void complete() {
    }

    @DisplayName("주문 전체를 조회한다.")
    @Test
    void findAll() {
        // given
        var orderTable_1번 = OrderTableFixture.newOne("1번 테이블");
        var orderTable_2번 = OrderTableFixture.newOne("2번 테이블");
        var order_1번 = OrderFixture.newOneEatIn(orderTable_1번, WAITING);
        var order_2번 = OrderFixture.newOneEatIn(orderTable_2번, SERVED);
        given(orderRepository.findAll()).willReturn(List.of(order_1번, order_2번));

        // when
        var actual = orderService.findAll();

        // then
        Assertions.assertThat(actual).containsAll(List.of(order_1번, order_2번));
    }
}
