package kitchenpos.order.service;

import kitchenpos.application.OrderService;
import kitchenpos.domain.*;
import kitchenpos.infra.KitchenridersClient;
import kitchenpos.menu.fixture.MenuFixture;
import kitchenpos.order.fixture.OrderFixture;
import kitchenpos.order.fixture.OrderLineItemFixture;
import kitchenpos.order.fixture.OrderTableFixture;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.AdditionalAnswers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

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
    private OrderLineItemFixture orderLineItemFixture;
    private OrderTableFixture orderTableFixture;
    private MenuFixture menuFixture;

    @BeforeEach
    void setUp() {
        orderFixture = new OrderFixture();
        orderLineItemFixture = new OrderLineItemFixture();
        orderTableFixture = new OrderTableFixture();
        menuFixture = new MenuFixture();
    }

    @Test
    @DisplayName("주문 시 주문 유형 및 항목은 반드시 존재 해야한다.")
    void create_exception_status_item_null() {
        List<Order> exceptionOrders = List.of(orderFixture.주문_유형_없는_주문, orderFixture.주문_항목_없는_주문);

        for (Order exceptionOrder : exceptionOrders) {
            Assertions.assertThatThrownBy(
                    () -> orderService.create(exceptionOrder)
            ).isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Test
    @DisplayName("손님은 식당에서 음식을 주문할 수 있다.")
    void create_eatIn() {
        Order 매장_주문 = orderFixture.매장_주문_A;
        OrderTable 주문_테이블 = orderTableFixture.손님_있는_주문_테이블;

        mockingMenuRepository(RepositoryMethod.FIND, menuFixture.메뉴_A);
        mockingMenuRepository(RepositoryMethod.FIND_ALL, menuFixture.메뉴_A);
        mockingOrderTableRepository(주문_테이블);
        mockingOrderRepository(RepositoryMethod.SAVE, 매장_주문);

        Order result = orderService.create(매장_주문);
        Assertions.assertThat(result.getOrderTable()).isEqualTo(주문_테이블);
    }

    @Test
    @DisplayName("식당 주문 시 주문 테이블이 존재해야 하며 빈 테이블일 수 없다.")
    void create_eatIn_exception_occupied() {
        Order 주문 = orderFixture.매장_주문_A;
        OrderTable 빈_테이블 = orderTableFixture.주문_테이블_A;

        mockingMenuRepository(RepositoryMethod.FIND, menuFixture.메뉴_A);
        mockingMenuRepository(RepositoryMethod.FIND_ALL, menuFixture.메뉴_A);
        mockingOrderTableRepository(빈_테이블);

        Assertions.assertThatThrownBy(
                () -> orderService.create(주문)
        ).isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("손님은 포장 주문을 할 수 있다.")
    void create_takeOut() {
        Order 포장_주문 = orderFixture.포장_주문_A;

        mockingMenuRepository(RepositoryMethod.FIND, menuFixture.메뉴_A);
        mockingMenuRepository(RepositoryMethod.FIND_ALL, menuFixture.메뉴_A);
        mockingOrderRepository(RepositoryMethod.SAVE, 포장_주문);

        Order result = orderService.create(포장_주문);
        Assertions.assertThat(result.getStatus()).isEqualTo(OrderStatus.WAITING);
    }

    @Test
    @DisplayName("포장 주문 시 주문 항목 수량이 존재해야 한다.")
    void create_takeOut_exception_quantity() {
        Order 주문_항목_없는_포장_주문 = OrderFixture.createTakeOut(List.of(orderLineItemFixture.수량_없는_주문_항목));

        Assertions.assertThatThrownBy(
                () -> orderService.create(주문_항목_없는_포장_주문)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("주문이 접수되면 손님에게 음식을 제공한다.")
    void serve() {
        Order 매장_접수_주문 = OrderFixture.createHasStatus(
                OrderType.EAT_IN, List.of(orderLineItemFixture.주문_항목_A), OrderStatus.ACCEPTED
        );
        Order 포장_접수_주문 = OrderFixture.createHasStatus(
                OrderType.TAKEOUT, List.of(orderLineItemFixture.주문_항목_A), OrderStatus.ACCEPTED
        );
        List<Order> 주문_목록 = List.of(매장_접수_주문, 포장_접수_주문);

        for (Order 주문 : 주문_목록) {
            mockingOrderRepository(RepositoryMethod.FIND, 주문);
            orderService.serve(주문.getId());
            Assertions.assertThat(주문.getStatus()).isEqualTo(OrderStatus.SERVED);
        }
    }

    @Test
    @DisplayName("손님에게 음식이 제공되면 주문 상태를 주문 완료로 변경한다.")
    void complete_takeOut() {
        Order 포장_서빙_주문 = OrderFixture.createHasStatus(
                OrderType.TAKEOUT, List.of(orderLineItemFixture.주문_항목_A), OrderStatus.SERVED
        );

        mockingOrderRepository(RepositoryMethod.FIND, 포장_서빙_주문);

        Order result = orderService.complete(포장_서빙_주문.getId());
        Assertions.assertThat(result.getStatus()).isEqualTo(OrderStatus.COMPLETED);
    }

    @Test
    @DisplayName("식당 주문 완료가 되면 매장 주인은 테이블을 정리한다.")
    void complete_eatIn() {
        Order 매장_서빙_주문 = OrderFixture.createEatInHasStatus(
                List.of(orderLineItemFixture.주문_항목_A), orderTableFixture.손님_있는_주문_테이블, OrderStatus.SERVED
        );

        mockingOrderRepository(RepositoryMethod.FIND, 매장_서빙_주문);

        orderService.complete(매장_서빙_주문.getId());
        Assertions.assertThat(매장_서빙_주문.getOrderTable().isOccupied()).isEqualTo(false);
    }

    @Test
    @DisplayName("손님은 배달 주문을 할 수 있다.")
    void create_delivery() {
        Order 배달_주문 = orderFixture.배달_주문_A;

        mockingMenuRepository(RepositoryMethod.FIND, menuFixture.메뉴_A);
        mockingMenuRepository(RepositoryMethod.FIND_ALL, menuFixture.메뉴_A);
        mockingOrderRepository(RepositoryMethod.SAVE, 배달_주문);

        Order result = orderService.create(배달_주문);
        Assertions.assertThat(result.getDeliveryAddress()).isEqualTo("행궁동");
    }

    @Test
    @DisplayName("배달 주문 시 주문 항목 수량이 존재해야 한다.")
    void create_delivery_exception_quantity() {
        Order 주문_항목_없는_배달_주문 = OrderFixture.createDelivery(List.of(orderLineItemFixture.수량_없는_주문_항목), "행궁동");

        Assertions.assertThatThrownBy(
                () -> orderService.create(주문_항목_없는_배달_주문)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("배달 주문 시 배송지 정보가 존재해야 한다.")
    void create_delivery_exception_address() {
        Order 배송지_없는_배달_주문 = OrderFixture.createDelivery(List.of(orderLineItemFixture.주문_항목_A), null);

        mockingMenuRepository(RepositoryMethod.FIND, menuFixture.메뉴_A);
        mockingMenuRepository(RepositoryMethod.FIND_ALL, menuFixture.메뉴_A);

        Assertions.assertThatThrownBy(
                () -> orderService.create(배송지_없는_배달_주문)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("라이더에게 주문 정보와 배달 주소시를 전달 후 배달을 요청하며 주문을 접수한다.")
    void accept_delivery() {
        Order 대기_주문 = OrderFixture.createHasStatus(
                OrderType.DELIVERY, List.of(orderLineItemFixture.주문_항목_A), OrderStatus.WAITING
        );

        mockingOrderRepository(RepositoryMethod.FIND, 대기_주문);
        Mockito.doNothing()
                .when(kitchenridersClient)
                .requestDelivery(Mockito.any(), Mockito.any(), Mockito.any());

        Order result = orderService.accept(대기_주문.getId());
        Assertions.assertThat(result.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
    }

    @Test
    @DisplayName("배달이 시작되면 주문의 상태를 배달 중으로 변경한다.")
    void startDelivery() {
        Order 배달_주문 = OrderFixture.createDeliveryHasStatus(
                List.of(orderLineItemFixture.주문_항목_A), "행궁동", OrderStatus.SERVED
        );

        mockingOrderRepository(RepositoryMethod.FIND, 배달_주문);

        orderService.startDelivery(배달_주문.getId());
        Assertions.assertThat(배달_주문.getStatus()).isEqualTo(OrderStatus.DELIVERING);
    }

    @Test
    @DisplayName("배달이 배달이 완료되면 주문의 상태를 배달 완료로 변경한다.")
    void completeDelivery() {
        Order 배달_주문 = OrderFixture.createDeliveryHasStatus(
                List.of(orderLineItemFixture.주문_항목_A), "행궁동", OrderStatus.DELIVERING
        );

        mockingOrderRepository(RepositoryMethod.FIND, 배달_주문);

        orderService.completeDelivery(배달_주문.getId());
        Assertions.assertThat(배달_주문.getStatus()).isEqualTo(OrderStatus.DELIVERED);
    }

    private void mockingMenuRepository(RepositoryMethod method, Menu menu) {
        if (method == RepositoryMethod.FIND) {
            Mockito.when(menuRepository.findById(Mockito.any()))
                    .thenReturn(Optional.of(menu));
        }
        if (method == RepositoryMethod.FIND_ALL) {
            Mockito.when(menuRepository.findAllByIdIn(Mockito.any()))
                    .thenReturn(List.of(menu));
        }
    }

    private void mockingOrderRepository(RepositoryMethod method, Order order) {
        if (method == RepositoryMethod.SAVE) {
            Mockito.when(orderRepository.save(Mockito.any()))
                    .then(AdditionalAnswers.returnsFirstArg());
        }
        if (method == RepositoryMethod.FIND) {
            Mockito.when(orderRepository.findById(Mockito.any()))
                    .thenReturn(Optional.of(order));
        }
    }

    private enum RepositoryMethod {
        FIND_ALL, FIND, SAVE
    }

    private void mockingOrderTableRepository(OrderTable orderTable) {
        Mockito.when(orderTableRepository.findById(Mockito.any()))
                .thenReturn(Optional.of(orderTable));
    }
}
