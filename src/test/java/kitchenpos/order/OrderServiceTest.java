package kitchenpos.order;

import kitchenpos.application.OrderService;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import kitchenpos.domain.OrderType;
import kitchenpos.infra.KitchenridersClient;
import kitchenpos.menu.MenuFixture;
import kitchenpos.ordertable.OrderTableFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static kitchenpos.domain.OrderStatus.*;
import static kitchenpos.domain.OrderType.*;
import static kitchenpos.order.OrderFixture.*;
import static kitchenpos.ordertable.OrderTableFixture.orderTable;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {
    @InjectMocks
    private OrderService orderService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private OrderTableRepository orderTableRepository;

    @Mock
    private KitchenridersClient kitchenridersClient;

    private OrderTable 주문테이블;
    private Menu 간장치킨;
    private Menu 갈릭치킨;

    @BeforeEach
    void setUp() {
        간장치킨 = MenuFixture.menu("간장치킨", 6000);
        갈릭치킨 = MenuFixture.hideMenu("양념치킨", 12000);
        주문테이블 = OrderTableFixture.orderTable();
    }

    @DisplayName("주문을 생성한다.")
    @Test
    void create() {
        OrderLineItem orderLineItem = orderLineItem(간장치킨, 1, 6000);
        Order order = order(OrderType.TAKEOUT, List.of(orderLineItem));

        when(menuRepository.findAllByIdIn(any())).thenReturn(List.of(간장치킨));
        when(menuRepository.findById(any())).thenReturn(Optional.of(간장치킨));
        when(orderRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

        Order createdOrder = orderService.create(order);

        assertAll(
                () -> assertThat(createdOrder.getStatus()),
                () -> assertThat(createdOrder.getStatus()).isEqualTo(WAITING),
                () -> assertThat(createdOrder.getType()).isEqualTo(OrderType.TAKEOUT)
        );
    }

    @DisplayName("주문 타입이 없으면 주문을 생성할 수 없다.")
    @Test
    void createWithNullOrderType() {
        OrderLineItem orderLineItem = orderLineItem(간장치킨, 1, 6000);
        Order order = order(null, List.of(orderLineItem));

        assertThatThrownBy(() -> orderService.create(order))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("주문 상품이 없으면 주문을 생성할 수 없다.")
    @Test
    void createWithNullOrderLineItems() {
        Order order = order(OrderType.TAKEOUT, Collections.emptyList());

        assertThatThrownBy(() -> orderService.create(order))
                .isInstanceOf(IllegalArgumentException.class);
    }


    @DisplayName("주문 상품의 개수가 0개 미만이면 주문을 생성할 수 없다.")
    @Test
    void createWithOrderLineItemsNegativeQuantity() {
        when(menuRepository.findAllByIdIn(any())).thenReturn(List.of(간장치킨));
        OrderLineItem orderLineItem = orderLineItem(간장치킨, -1, 6000);
        Order order = order(OrderType.TAKEOUT, List.of(orderLineItem));

        assertThatThrownBy(() -> orderService.create(order))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("보이지 않는 메뉴의 주문상품을 포함하면 주문을 생성할 수 없다.")
    @Test
    void createWithDisplayedFalseMenu() {
        OrderLineItem orderLineItem = orderLineItem(갈릭치킨, 1, 6000);
        Order order = order(OrderType.TAKEOUT, List.of(orderLineItem));

        when(menuRepository.findAllByIdIn(any())).thenReturn(List.of(갈릭치킨));
        when(menuRepository.findById(any())).thenReturn(Optional.of(갈릭치킨));

        assertThatThrownBy(() -> orderService.create(order))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("배달 주소가 없으면 배달 주문을 생성할 수 없다.")
    @Test
    void createWithNullDeliveryAddress() {
        OrderLineItem orderLineItem = orderLineItem(간장치킨, 1, 6000);
        Order order = order(OrderType.DELIVERY, List.of(orderLineItem), null);

        when(menuRepository.findAllByIdIn(any())).thenReturn(List.of(간장치킨));
        when(menuRepository.findById(any())).thenReturn(Optional.of(간장치킨));

        assertThatThrownBy(() -> orderService.create(order))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("매장식사 주문을 한다.")
    @Test
    void createEatInOrder() {
        OrderLineItem orderLineItem = orderLineItem(간장치킨, 1, 6000);
        Order order = order(EAT_IN, List.of(orderLineItem), "서울특별시");

        when(menuRepository.findAllByIdIn(any())).thenReturn(List.of(간장치킨));
        when(menuRepository.findById(any())).thenReturn(Optional.of(간장치킨));
        when(orderTableRepository.findById(any())).thenReturn(Optional.of(OrderTableFixture.orderTable(true)));
        when(orderRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

        Order eatInOrder = orderService.create(order);

        assertThat(eatInOrder.getType()).isEqualTo(EAT_IN);
    }

    @DisplayName("테이블에 앉지 않으면 매장 식사를 주문할 수 없다.")
    @Test
    void createEatInOrderNotSit() {
        OrderLineItem orderLineItem = orderLineItem(간장치킨, 1, 6000);
        Order order = order(EAT_IN, List.of(orderLineItem), "서울특별시");

        when(menuRepository.findAllByIdIn(any())).thenReturn(List.of(간장치킨));
        when(menuRepository.findById(any())).thenReturn(Optional.of(간장치킨));
        when(orderTableRepository.findById(any())).thenReturn(Optional.of(주문테이블));

        assertThatThrownBy(() -> orderService.create(order))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("주문을 접수한다.")
    @Test
    void accept() {
        Order order = OrderFixture.order(WAITING, EAT_IN, Collections.emptyList(), 주문테이블.getId());
        when(orderRepository.findById(any())).thenReturn(Optional.of(order));

        Order accept = orderService.accept(order.getId());

        assertThat(accept.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
    }

    @DisplayName("WAITING 상태가 아닌 주문은 접수할 수 없다.")
    @Test
    void acceptNotWaitingOrder() {
        Order order = order(EAT_IN, Collections.emptyList());
        when(orderRepository.findById(any())).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.accept(order.getId()))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("주문을 서빙한다.")
    @Test
    void serve() {
        Order order = OrderFixture.order(ACCEPTED, EAT_IN, Collections.emptyList(), 주문테이블.getId());
        when(orderRepository.findById(any())).thenReturn(Optional.of(order));

        Order served = orderService.serve(order.getId());

        assertThat(served.getStatus()).isEqualTo(OrderStatus.SERVED);
    }

    @DisplayName("ACCEPTED가 아닌 주문은 서빙할 수 없다.")
    @Test
    void serviceNotAcceptedOrder() {
        Order order = OrderFixture.order(WAITING, EAT_IN, Collections.emptyList(), 주문테이블.getId());
        when(orderRepository.findById(any())).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.serve(order.getId()))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("주문의 배달을 시작한다.")
    @Test
    void startDelivery() {
        Order order = OrderFixture.order(SERVED, DELIVERY, Collections.emptyList(), 주문테이블.getId());
        when(orderRepository.findById(any())).thenReturn(Optional.of(order));

        Order delivering = orderService.startDelivery(order.getId());

        assertThat(delivering.getStatus()).isEqualTo(DELIVERING);
    }

    @DisplayName("주문 타입이 DELIVERY가 아닌 주문은 배달을 시작할 수 없다.")
    @Test
    void startDeliveryNotDeliveryOrder() {
        Order order = OrderFixture.order(SERVED, EAT_IN, Collections.emptyList(), 주문테이블.getId());
        when(orderRepository.findById(any())).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.startDelivery(order.getId()))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("주문 상태가 SERVED가 아닌 주문은 배달을 시작할 수 없다.")
    @Test
    void startDeliveryNotServedOrder() {
        Order order = OrderFixture.order(ACCEPTED, DELIVERY, Collections.emptyList(), 주문테이블.getId());
        when(orderRepository.findById(any())).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.startDelivery(order.getId()))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("배달을 완료한다.")
    @Test
    void completeDelivery() {
        Order order = OrderFixture.order(DELIVERING, DELIVERY, Collections.emptyList(), 주문테이블.getId());
        when(orderRepository.findById(any())).thenReturn(Optional.of(order));

        Order delivered = orderService.completeDelivery(order.getId());

        assertThat(delivered.getStatus()).isEqualTo(DELIVERED);
    }

    @DisplayName("주문 상태가 DELIVERING이 아닌 주문은 배달완료 처리할 수 없다.")
    @Test
    void completeDeliveryNotDeliveringOrder() {
        Order order = OrderFixture.order(SERVED, DELIVERY, Collections.emptyList(), 주문테이블.getId());
        when(orderRepository.findById(any())).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.completeDelivery(order.getId()))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("주문을 완료한다.")
    @Test
    void complete() {
        Order order = OrderFixture.order(SERVED, TAKEOUT, Collections.emptyList(), 주문테이블.getId());
        when(orderRepository.findById(any())).thenReturn(Optional.of(order));

        Order complete = orderService.complete(order.getId());

        assertThat(complete.getStatus()).isEqualTo(COMPLETED);
    }

    @DisplayName("배달 주문일 때, 배달 완료된 상태가 아니라면 주문 완료를 할 수 없다.")
    @Test
    void completeNotDeliveredOrder() {
        Order order = OrderFixture.order(SERVED, DELIVERY, Collections.emptyList(), 주문테이블.getId());
        when(orderRepository.findById(any())).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.complete(order.getId()))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("주문 목록을 조회한다.")
    @Test
    void findAll() {
        Order first = OrderFixture.order(SERVED, DELIVERY, Collections.emptyList(), 주문테이블.getId());
        Order second = OrderFixture.order(SERVED, EAT_IN, Collections.emptyList(), 주문테이블.getId());
        when(orderRepository.findAll()).thenReturn(List.of(first, second));

        List<Order> orders = orderService.findAll();

        assertThat(orders).hasSize(2);
    }
}
