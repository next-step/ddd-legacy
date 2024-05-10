package kitchenpos.order.service;

import kitchenpos.application.OrderService;
import kitchenpos.domain.*;
import kitchenpos.infra.KitchenridersClient;
import kitchenpos.menu.MenuTestHelper;
import kitchenpos.order.fixture.OrderFixture;
import kitchenpos.order.fixture.OrderTableFixture;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.AdditionalAnswers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@DisplayName("주문 서비스 테스트")
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
    @InjectMocks
    private OrderService orderService;

    private OrderFixture orderFixture;
    private OrderTableFixture orderTableFixture;
    private MenuTestHelper menuTestHelper;

    @BeforeEach
    void setUp() {
        orderFixture = new OrderFixture();
        orderTableFixture = new OrderTableFixture();
        menuTestHelper = new MenuTestHelper();
    }

    @ParameterizedTest
    @MethodSource("provideStatusAndItemException")
    @DisplayName("주문 시 주문 유형 및 항목은 반드시 존재 해야한다.")
    void create(Order order) {
        Assertions.assertThatThrownBy(
                () -> orderService.create(order)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    private static Stream<Order> provideStatusAndItemException() {
        return Stream.of(OrderFixture.주문_유형_없는_주문, OrderFixture.주문_항목_없는_주문);
    }

    @Test
    @DisplayName("손님은 식당에서 음식을 주문할 수 있다.")
    void create_eatIn() {
        Order 매장_주문 = orderFixture.매장_주문_A;
        OrderTable 주문_테이블 = OrderTableFixture.손님_있는_주문_테이블;

        Mockito.when(menuRepository.findAllByIdIn(Mockito.any()))
                .thenReturn(List.of(menuTestHelper.메뉴_A));
        Mockito.when(menuRepository.findById(Mockito.any()))
                .thenReturn(Optional.of(menuTestHelper.메뉴_A));
        Mockito.when(orderTableRepository.findById(Mockito.any()))
                .thenReturn(Optional.of(주문_테이블));
        Mockito.when(orderRepository.save(Mockito.any()))
                .then(AdditionalAnswers.returnsFirstArg());

        Order result = orderService.create(매장_주문);
        Assertions.assertThat(result.getOrderTable()).isEqualTo(주문_테이블);
    }
}
