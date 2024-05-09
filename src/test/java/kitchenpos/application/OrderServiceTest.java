package kitchenpos.application;

import fixtures.MenuBuilder;
import fixtures.MenuGroupBuilder;
import fixtures.OrderBuilder;
import fixtures.OrderLineItemBuilder;
import fixtures.OrderTableBuilder;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import kitchenpos.domain.OrderType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class OrderServiceTest {

    @Autowired
    private OrderService orderService;
    @Autowired
    private MenuGroupRepository menuGroupRepository;
    @Autowired
    private MenuRepository menuRepository;
    @Autowired
    private OrderTableRepository orderTableRepository;


    @DisplayName("주문이 생성된다")
    @ParameterizedTest
    @EnumSource(value = OrderType.class)
    void createOrderTest(OrderType orderType) {

        assertDoesNotThrow(() -> createOrder(orderType));
    }


    @DisplayName("배달 주문은 주소 정보가 없으면 예외가 발생한다")
    @Test
    void createDeliveryOrderFailWhenAddressIsNullTest() {

        Order order = new OrderBuilder()
                .withOrderType(OrderType.DELIVERY)
                .withDeliveryAddress(null)
                .build();

        assertThrows(IllegalArgumentException.class, () -> orderService.create(order));
    }

    @DisplayName("매장 식사 주문은 주문 테이블 정보가 있어야 한다")
    @Test
    void createEatInOrderFailWhenOrderTableTest() {

        Order order = new OrderBuilder()
                .withOrderType(OrderType.EAT_IN)
                .withOrderTable(null)
                .build();

        assertThrows(IllegalArgumentException.class, () -> orderService.create(order));
    }


    @DisplayName("매장 식사 주문은 수량이 0보다 커야 한다")
    @Test
    void createEatInOrderFailWhenQuantityIsLessThanZeroTest() {

        Order created = createOrder(OrderType.EAT_IN);

        assertThat(created.getOrderLineItems()).hasSizeGreaterThan(0);
    }


    @DisplayName("주문이 생성되면 주문의 초기 상태는 WAITING이다")
    @ParameterizedTest
    @EnumSource(value = OrderType.class)
    void createOrderStatusTest(OrderType orderType) {

        Order created = createOrder(orderType);

        assertThat(created.getStatus()).isEqualTo(OrderStatus.WAITING);
    }


    @DisplayName("주문은 메뉴를 최소 1개 갖는다")
    @Test
    void createdOrderHasAtLeastOneMenuTest() {

        Order order = createOrder(OrderType.TAKEOUT);

        assertThat(order.getOrderLineItems()).hasSizeGreaterThan(0);
    }


    @DisplayName("주문이 접수되면 OrderStatus가 ACCEPTED로 변경된다")
    @Test
    void acceptTest() {

        Order created = createOrder(OrderType.TAKEOUT);
        Order acceptedOrder = orderService.accept(created.getId());

        assertThat(acceptedOrder.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
    }

    @DisplayName("주문이 서빙되면 OrderStatus가 SERVED로 변경된다")
    @Test
    void serveTest() {

        Order created = createOrder(OrderType.TAKEOUT);
        Order acceptedOrder = orderService.accept(created.getId());
        Order servedOrder = orderService.serve(acceptedOrder.getId());

        assertThat(servedOrder.getStatus()).isEqualTo(OrderStatus.SERVED);
    }

    @DisplayName("주문이 배달 시작되면 OrderStatus가 DELIVERING로 변경된다")
    @Test
    void startDeliverTest() {

        Order created = createOrder(OrderType.DELIVERY);
        Order acceptedOrder = orderService.accept(created.getId());
        Order servedOrder = orderService.serve(acceptedOrder.getId());
        Order deliveredOrder = orderService.startDelivery(servedOrder.getId());

        assertThat(deliveredOrder.getStatus()).isEqualTo(OrderStatus.DELIVERING);
    }

    @DisplayName("주문이 배달 완료되면 OrderStatus가 DELIVERED로 변경된다")
    @Test
    void completedDeliverTest() {

        Order created = createOrder(OrderType.DELIVERY);
        Order acceptedOrder = orderService.accept(created.getId());
        Order servedOrder = orderService.serve(acceptedOrder.getId());
        Order deliveredOrder = orderService.startDelivery(servedOrder.getId());
        Order completedOrder = orderService.completeDelivery(deliveredOrder.getId());

        assertThat(completedOrder.getStatus()).isEqualTo(OrderStatus.DELIVERED);
    }

    @DisplayName("주문이 완료되면 OrderStatus가 COMPLETED로 변경된다")
    @Test
    void completeTest() {

        Order created = createOrder(OrderType.TAKEOUT);
        Order acceptedOrder = orderService.accept(created.getId());
        Order servedOrder = orderService.serve(acceptedOrder.getId());
        Order completedOrder = orderService.complete(servedOrder.getId());

        assertThat(completedOrder.getStatus()).isEqualTo(OrderStatus.COMPLETED);
    }


    private Order createOrder(OrderType orderType) {

        MenuGroup menuGroup = menuGroupRepository.save(new MenuGroupBuilder().withName("한 마리 메뉴").build());

        Menu chicken = menuRepository.save(new MenuBuilder()
                .withMenuGroup(menuGroup)
                .with("치킨", BigDecimal.valueOf(10_000)).build());

        OrderLineItem orderLineItem = new OrderLineItemBuilder()
                .withMenu(chicken)
                .withPrice(BigDecimal.valueOf(10_000))
                .build();

        OrderTable orderTable = orderTableRepository.save(new OrderTableBuilder()
                .anOrderTable()
                .build());

        Order order = new OrderBuilder()
                .withOrderType(orderType)
                .withOrderLineItems(List.of(orderLineItem))
                .withOrderTable(orderTable)
                .build();

        return orderService.create(order);
    }

}
