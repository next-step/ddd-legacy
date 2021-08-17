package kitchenpos.application;

import kitchenpos.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderTableRepository orderTableRepository;
    @Autowired
    private MenuRepository menuRepository;
    @Autowired
    private OrderRepository orderRepository;

    private Order inOrder;
    private Order deliveryOrder;
    private Order takeoutOrder;
    private OrderTable orderTable;
    private OrderLineItem orderLineItem;

    @BeforeEach
    void setUp() {
        orderTable = orderTableRepository.findById(UUID.fromString("8d710043-29b6-420e-8452-233f5a035520"))
                .orElseThrow(NoSuchElementException::new);
        Menu menu = menuRepository.findById(UUID.fromString("f59b1e1c-b145-440a-aa6f-6095a0e2d63b"))
                .orElseThrow(NoSuchElementException::new);
        orderLineItem = new OrderLineItem();
        orderLineItem.setMenu(menu);
        orderLineItem.setMenuId(menu.getId());
        orderLineItem.setQuantity(1);
        orderLineItem.setPrice(BigDecimal.valueOf(16000));

        orderTable.setEmpty(false);
        orderTable.setNumberOfGuests(1);

        inOrder = new Order();
        inOrder.setId(UUID.randomUUID());
        inOrder.setOrderDateTime(LocalDateTime.now());
        inOrder.setOrderTable(orderTable);
        inOrder.setOrderTableId(orderTable.getId());
        inOrder.setOrderLineItems(Collections.singletonList(orderLineItem));
        inOrder.setStatus(OrderStatus.WAITING);
        inOrder.setType(OrderType.EAT_IN);

        deliveryOrder = new Order();
        deliveryOrder.setId(UUID.randomUUID());
        deliveryOrder.setOrderDateTime(LocalDateTime.now());
        deliveryOrder.setOrderLineItems(Collections.singletonList(orderLineItem));
        deliveryOrder.setStatus(OrderStatus.WAITING);
        deliveryOrder.setType(OrderType.DELIVERY);
        deliveryOrder.setDeliveryAddress("서울시 강남구");

        takeoutOrder = new Order();
        takeoutOrder.setId(UUID.randomUUID());
        takeoutOrder.setOrderDateTime(LocalDateTime.now());
        takeoutOrder.setOrderLineItems(Collections.singletonList(orderLineItem));
        takeoutOrder.setStatus(OrderStatus.WAITING);
        takeoutOrder.setType(OrderType.TAKEOUT);
    }

    @DisplayName("매장주문을 등록한다.")
    @Test
    void inOrderCreateTest() {
        Order actual = orderService.create(inOrder);
        assertThat(actual).isNotNull();
        assertThat(actual.getType()).isEqualTo(OrderType.EAT_IN);
        assertThat(actual.getOrderTable()).isNotNull();
    }

    @DisplayName("배달주문을 등록한다.")
    @Test
    void deliveryOrderCreateTest() {
        Order actual = orderService.create(deliveryOrder);
        assertThat(actual).isNotNull();
        assertThat(actual.getType()).isEqualTo(OrderType.DELIVERY);
        assertThat(actual.getDeliveryAddress()).isEqualTo("서울시 강남구");
    }

    @DisplayName("노출되는 메뉴만 주문가능하다.")
    @Test
    void hideMenuOrderTest() {
        inOrder.getOrderLineItems()
                .get(0)
                .getMenu()
                .setDisplayed(false);
        assertThatThrownBy(() -> orderService.create(inOrder))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("주문등록시 주문타입을 입력해야한다.")
    @Test
    void orderTypeNullTest() {
        inOrder.setType(null);
        assertThatThrownBy(() -> orderService.create(inOrder))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("배달 주문시 배달지 주소입력은 필수이다.")
    @NullAndEmptySource
    @ParameterizedTest
    void deliveryAddressEmptyTest(String address) {
        deliveryOrder.setDeliveryAddress(address);
        assertThatThrownBy(() -> orderService.create(deliveryOrder))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("매장주문등록시 주문테이블은 사용중이어야 한다.")
    @Test
    void inOrderOrderTableEmptyTest() {
        orderTable.setEmpty(true);
        OrderTable emptyTable = orderTableRepository.save(orderTable);
        inOrder.setOrderTable(emptyTable);
        inOrder.setOrderTableId(emptyTable.getId());
        assertThatThrownBy(() -> orderService.create(inOrder))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("주문등록시 주문내역(orderLineItem)은 필수이다.")
    @NullAndEmptySource
    @ParameterizedTest
    void nullAndEmptyOrderLineItemTest(List<OrderLineItem> orderLineItems) {
        inOrder.setOrderLineItems(orderLineItems);
        assertThatThrownBy(() -> orderService.create(inOrder))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("주문등록시 메뉴등록이 되어있지 않은 메뉴는 주문내역(orderLineItem)에 포함될 수 없다.")
    @Test
    void invalidMenuInOrderLineItemTest() {
        Menu menu = new Menu();
        menu.setId(UUID.randomUUID());
        orderLineItem.setMenu(menu);
        orderLineItem.setMenuId(menu.getId());
        inOrder.setOrderLineItems(Collections.singletonList(orderLineItem));
        assertThatThrownBy(() -> orderService.create(inOrder))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("배달주문시 주문내역(orderLineItem)의 수량은 0보다 작을수 없다.")
    @Test
    void deliveryOrderLineItemMinusQuantityTest() {
        deliveryOrder.getOrderLineItems()
                .forEach(item -> item.setQuantity(-1));
        assertThatThrownBy(() -> orderService.create(deliveryOrder))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("포장주문시 주문내역(orderLineItem)의 수량은 0보다 작을수 없다.")
    @Test
    void takeoutOrderLineItemMinusQuantityTest() {
        takeoutOrder.getOrderLineItems()
                .forEach(item -> item.setQuantity(-1));
        assertThatThrownBy(() -> orderService.create(takeoutOrder))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("주문등록시 메뉴의 가격과 주문내역(orderLineItem)의 가격은 같아야 한다.")
    @Test
    void menuPriceAndOrderLineItemPriceTest() {
        // inOrder의 menu의 가격은 16000원
        orderLineItem.setPrice(BigDecimal.valueOf(10000));
        assertThatThrownBy(() -> orderService.create(inOrder))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("주문의 상태를 접수(accept)상태로 변경한다.")
    @Test
    void acceptTest() {
        Order waitingOrder = orderRepository.findById(UUID.fromString("69d78f38-3bff-457c-bb72-26319c985fd8"))
                .orElse(null);
        assertThat(waitingOrder).isNotNull();
        Order actual = orderService.accept(waitingOrder.getId());
        assertThat(actual.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
    }

    @DisplayName("대기상태가 아닌 주문을 접수상태로 변경할 수 없다.")
    @Test
    void acceptStatusAcceptTest() {
        Order waitingOrder = orderRepository.findById(UUID.fromString("69d78f38-3bff-457c-bb72-26319c985fd8"))
                .orElse(null);
        assertThat(waitingOrder).isNotNull();
        UUID orderId = waitingOrder.getId();
        waitingOrder.setStatus(OrderStatus.ACCEPTED);
        assertThatThrownBy(() -> orderService.accept(orderId))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("접수상태의 주문을 제공(serve)상태로 변경한다.")
    @Test
    void serveTest() {
        Order waitingOrder = orderRepository.findById(UUID.fromString("69d78f38-3bff-457c-bb72-26319c985fd8"))
                .orElse(null);
        assertThat(waitingOrder).isNotNull();
        waitingOrder.setStatus(OrderStatus.ACCEPTED);
        Order actual = orderService.serve(waitingOrder.getId());
        assertThat(actual.getStatus()).isEqualTo(OrderStatus.SERVED);
    }

    @DisplayName("배달주문의 상태를 배달시작으로 변경한다.")
    @Test
    void deliveryOrderStartDeliveryTest() {
        Order order = orderRepository.save(deliveryOrder);
        order.setStatus(OrderStatus.SERVED);
        Order actual = orderService.startDelivery(order.getId());
        assertThat(actual.getStatus()).isEqualTo(OrderStatus.DELIVERING);
    }

    @DisplayName("매장주문은 배달시작상태로 변경할 수 없다.")
    @Test
    void inOrderStartDeliveryTest() {
        Order order = orderRepository.save(inOrder);
        order.setStatus(OrderStatus.SERVED);
        UUID orderId = order.getId();
        assertThatThrownBy(() -> orderService.startDelivery(orderId))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("배달주문의 상태를 배달완료로 변경한다.")
    @Test
    void completeDeliveryTest() {
        Order order = orderRepository.save(deliveryOrder);
        order.setStatus(OrderStatus.DELIVERING);
        Order actual = orderService.completeDelivery(order.getId());
        assertThat(actual.getStatus()).isEqualTo(OrderStatus.DELIVERED);
    }

    @DisplayName("배달주문의 상태를 완료로 변경한다.")
    @Test
    void deliveryOrderCompleteTest() {
        Order order = orderRepository.save(deliveryOrder);
        order.setStatus(OrderStatus.DELIVERED);
        Order actual = orderService.complete(order.getId());
        assertThat(actual.getStatus()).isEqualTo(OrderStatus.COMPLETED);
    }

    @DisplayName("매장주문의 상태를 완료로 변경한다.")
    @Test
    void inOrderCompleteTest() {
        Order order = orderRepository.save(inOrder);
        order.setStatus(OrderStatus.SERVED);
        Order actual = orderService.complete(order.getId());
        assertThat(actual.getStatus()).isEqualTo(OrderStatus.COMPLETED);
    }

    @DisplayName("포장주문의 상태를 완료로 변경한다.")
    @Test
    void takeoutOrderCompleteTest() {
        Order order = orderRepository.save(takeoutOrder);
        order.setStatus(OrderStatus.SERVED);
        Order actual = orderService.complete(order.getId());
        assertThat(actual.getStatus()).isEqualTo(OrderStatus.COMPLETED);
    }

    @DisplayName("주문목록을 조회한다.")
    @Test
    void findAllTest() {
        List<Order> orders = orderService.findAll();
        assertThat(orders.size()).isEqualTo(3);
    }
}