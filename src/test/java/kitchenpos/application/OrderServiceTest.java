package kitchenpos.application;

import kitchenpos.application.fake.FakeMenuRepository;
import kitchenpos.application.fake.FakeOrderRepository;
import kitchenpos.application.fake.FakeOrderTableRepository;
import kitchenpos.domain.*;
import kitchenpos.infra.KitchenridersClient;
import kitchenpos.infra.PurgomalumClient;
import org.aspectj.weaver.ast.Or;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.client.RestTemplateBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static kitchenpos.application.Fixtures.menu;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

public class OrderServiceTest {

    private final OrderRepository orderRepository = new FakeOrderRepository();
    private final MenuRepository menuRepository = new FakeMenuRepository();
    private final OrderTableRepository orderTableRepository = new FakeOrderTableRepository();
    private final OrderService orderService = new OrderService(orderRepository, menuRepository, orderTableRepository, new KitchenridersClient());

    @Test
    @DisplayName("주문을 생성하여 저장한다.")
    void createOrder() {
        // given
        OrderTable orderTable = createOrderTable("주문 테이블");
        Order order = new Order();
        order.setOrderTable(orderTable);
        order.setOrderTableId(orderTable.getId());
        order.setOrderDateTime(LocalDateTime.now());
        order.setStatus(OrderStatus.WAITING);
        order.setType(OrderType.EAT_IN);
        order.setOrderLineItems(createOrderLineItems());

        // when
        Order saveOrder = orderService.create(order);

        // then
        assertAll(
                () -> assertThat(saveOrder.getId()).isNotNull(),
                () -> assertThat(saveOrder.getOrderTable()).isNotNull(),
                () -> assertThat(saveOrder.getStatus()).isEqualTo(OrderStatus.WAITING),
                () -> assertThat(saveOrder.getType()).isEqualTo(OrderType.EAT_IN)
        );
    }

    @Test
    @DisplayName("주문을 확인한다.")
    void acceptOrder() {
        // given
        OrderTable orderTable = createOrderTable("주문 테이블");
        Order order = createOrder(orderTable, OrderStatus.WAITING, OrderType.EAT_IN);

        // when
        Order saveOrder = orderService.accept(order.getId());

        // then
        assertThat(saveOrder.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
    }

    @Test
    @DisplayName("주문을 배송 상태로 변경한다.")
    void serveOrder() {
        // given
        OrderTable orderTable = createOrderTable("주문 테이블");
        Order order = createOrder(orderTable, OrderStatus.ACCEPTED, OrderType.EAT_IN);

        // when
        Order saveOrder = orderService.serve(order.getId());

        // then
        assertThat(saveOrder.getStatus()).isEqualTo(OrderStatus.SERVED);
    }

    @Test
    @DisplayName("주문을 배송 시작 상태로 변경한다.")
    void startDeliveryOrder() {
        // given
        OrderTable orderTable = createOrderTable("주문 테이블");
        Order order = createOrder(orderTable, OrderStatus.SERVED, OrderType.DELIVERY);

        // when
        Order saveOrder = orderService.startDelivery(order.getId());

        // then
        assertThat(saveOrder.getStatus()).isEqualTo(OrderStatus.DELIVERING);
    }

    @Test
    @DisplayName("주문을 배송 완료 상태로 변경한다.")
    void completeDelivery() {
        // given
        OrderTable orderTable = createOrderTable("주문 테이블");
        Order order = createOrder(orderTable, OrderStatus.DELIVERING, OrderType.DELIVERY);

        // when
        Order saveOrder = orderService.completeDelivery(order.getId());

        // then
        assertThat(saveOrder.getStatus()).isEqualTo(OrderStatus.DELIVERED);
    }

    @Test
    @DisplayName("주문을 주문 완료 상태로 변경한다.")
    void completeOrder() {
        // given
        OrderTable orderTable = createOrderTable("주문 테이블");
        Order order = createOrder(orderTable, OrderStatus.DELIVERED, OrderType.DELIVERY);

        // when
        Order saveOrder = orderService.complete(order.getId());

        // then
        assertThat(saveOrder.getStatus()).isEqualTo(OrderStatus.COMPLETED);
    }

    @Test
    @DisplayName("주문 리스트를 가져온다.")
    void findOrders() {
        // given
        OrderTable orderTable = createOrderTable("주문 테이블");
        OrderTable orderTable2 = createOrderTable("주문 테이블2");
        OrderTable orderTable3 = createOrderTable("주문 테이블3");
        createOrder(orderTable, OrderStatus.WAITING, OrderType.DELIVERY);
        createOrder(orderTable2, OrderStatus.ACCEPTED, OrderType.DELIVERY);
        createOrder(orderTable3, OrderStatus.COMPLETED, OrderType.DELIVERY);

        // when
        List<Order> orders = orderService.findAll();

        // then
        assertAll(
                () -> assertThat(orders).isNotEmpty(),
                () -> assertThat(orders.size()).isEqualTo(3)
        );
    }


    private OrderTable createOrderTable(String name) {
        OrderTable orderTable = new OrderTable();
        orderTable.setId(UUID.randomUUID());
        orderTable.setName(name);
        orderTable.setNumberOfGuests(0);
        orderTable.setOccupied(false);
        return orderTableRepository.save(orderTable);
    }

    private List<OrderLineItem> createOrderLineItems() {
        OrderLineItem orderLineItem = createOrderLineItem(1L);
        OrderLineItem orderLineItem2 = createOrderLineItem(2L);
        List<OrderLineItem> orderLineItems = new ArrayList<>();
        orderLineItems.add(orderLineItem);
        orderLineItems.add(orderLineItem2);
        return orderLineItems;
    }

    private OrderLineItem createOrderLineItem(Long seq) {
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setSeq(seq);
        Menu menu = menuRepository.save(menu());
        orderLineItem.setMenu(menu);
        orderLineItem.setPrice(BigDecimal.valueOf(19000));
        orderLineItem.setQuantity(2);
        orderLineItem.setMenuId(menu.getId());
        return orderLineItem;
    }
    
    private Order createOrder(OrderTable orderTable, OrderStatus orderStatus, OrderType orderType) {
        Order order = new Order();
        order.setOrderTable(orderTable);
        order.setOrderTableId(orderTable.getId());
        order.setOrderDateTime(LocalDateTime.now());
        order.setStatus(orderStatus);
        order.setType(orderType);
        order.setOrderLineItems(createOrderLineItems());
        return orderRepository.save(order);
    }
}
