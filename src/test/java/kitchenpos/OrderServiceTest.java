package kitchenpos;

import kitchenpos.application.OrderService;
import kitchenpos.domain.*;
import kitchenpos.infra.KitchenClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import static kitchenpos.TestFixtures.createOrderRequest;
import static kitchenpos.TestFixtures.createOrderRequestWithoutOrderTable;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

class OrderServiceTest {

    private final OrderRepository orderRepository = new FakeOrderRepository();
    private final MenuRepository menuRepository = new FakeMenuRepository();
    private final OrderTableRepository orderTableRepository = new FakeOrderTableRepository();
    private final KitchenClient kitchenridersClient = new FakeKitchenClient();

    private OrderService orderService;

    private Menu menu1;
    private Menu menu2;
    private OrderLineItem orderLineItem;
    private OrderLineItem orderLineItem2;
    private List<OrderLineItem> orderLineItems = new ArrayList<>();
    private OrderTable orderTable;

    @BeforeEach
    void setUp() {

        menu1 = TestFixtures.createMenuRequest("메뉴1", true, 8000);
        menu2 = TestFixtures.createMenuRequest("메뉴2", true, 9000);
        menuRepository.save(menu1);
        menuRepository.save(menu2);

        orderTable = TestFixtures.createOrderTable("8번");
        orderTableRepository.save(orderTable);

        orderLineItem = TestFixtures.createOrderLineItemRequest(null, menu1.getId(), 1, 8000);
        orderLineItem2 = TestFixtures.createOrderLineItemRequest(null, menu2.getId(), 2, 9000);
        orderLineItems.addAll(Arrays.asList(orderLineItem, orderLineItem2));

        orderService = new OrderService(orderRepository, menuRepository, orderTableRepository, kitchenridersClient);

    }

    @DisplayName("주문을 등록할 수 있다.")
    @Test
    void createOrder() {
        // given
        Order orderRequest = createOrderRequest(OrderType.EAT_IN, orderLineItems,orderTable);

        // when
        Order result = orderService.create(orderRequest);

        // then
        assertThat(result.getId()).isNotNull();
    }

    @DisplayName("주문은 `배달`, `테이크아웃`, `매장안 식사`중 하나여야 한다.")
    @Test
    void orderTypeEmpty() {
        // given
        Order orderRequest = createOrderRequest(null, orderLineItems);

        // when - then
        assertThatThrownBy(() -> orderService.create(orderRequest))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("주문 아이템은 비어있을 수 없다.")
    @Test
    void orderItemEmpty() {
        // given
        Order orderRequest = createOrderRequest(OrderType.EAT_IN, new ArrayList<>());

        // when - then
        assertThatThrownBy(() -> orderService.create(orderRequest))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴들의 갯수와 주문 아이템들의 갯수는 같아야 한다.")
    @Test
    void sameCount() {
        // given
        OrderLineItem orderLineItem3 = TestFixtures.createOrderLineItemRequest(null, menu2.getId(), 2, 9000);
        orderLineItems.add(orderLineItem3);
        Order orderRequest = createOrderRequest(OrderType.EAT_IN, orderLineItems);

        // when - then
        assertThatThrownBy(() -> orderService.create(orderRequest))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("주문 타입이 `매장안 식사`가 아니라면 주문 아이템의 수량은 0보다 커야 한다.")
    @Test
    void itemCountGreaterThanZero() {
        // given
        Menu menu3 = TestFixtures.createMenuRequest("메뉴3", true, 9000);
        menuRepository.save(menu3);
        OrderLineItem orderLineItem3 = TestFixtures.createOrderLineItemRequest(null, menu3.getId(), -3, 9000);
        orderLineItems.add(orderLineItem3);

        Order orderRequest = createOrderRequest(OrderType.DELIVERY, orderLineItems);

        // when - then
        assertThatThrownBy(() -> orderService.create(orderRequest))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("주문을 등록할 때는 주문의 메뉴들은 미리 등록된 상태여야 한다.")
    @Test
    void menuAlreadyRegistered() {
        // given
        Menu menu3 = TestFixtures.createMenuRequest("메뉴3", true, 9000);
        OrderLineItem orderLineItem3 = TestFixtures.createOrderLineItemRequest(null, menu3.getId(), -3, 9000);
        orderLineItems.add(orderLineItem3);

        Order orderRequest = createOrderRequest(OrderType.DELIVERY, orderLineItems);

        // when - then
        assertThatThrownBy(() -> orderService.create(orderRequest))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("주문을 등록시 주문의 메뉴들은 표시되어 보여지는 상태여야 한다.")
    @Test
    void orderMenuDisplayed() {
        // given
        Menu menu3 = TestFixtures.createMenuRequest("메뉴3", true, 9000);
        menuRepository.save(menu3);
        OrderLineItem orderLineItem3 = TestFixtures.createOrderLineItemRequest(null, menu3.getId(), 2, 9000);
        menu3.setDisplayed(false);
        orderLineItems.add(orderLineItem3);

        Order orderRequest = createOrderRequest(OrderType.DELIVERY, orderLineItems);

        // when - then
        assertThatThrownBy(() -> orderService.create(orderRequest))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("주문을 등록 시 메뉴의 가격과 주문 아이템의 가격은 같아야 한다.")
    @Test
    void menuOrderHasSamePrice() {
        // given
        Menu menu3 = TestFixtures.createMenuRequest("메뉴3", true, 9000);
        menuRepository.save(menu3);
        OrderLineItem orderLineItem3 = TestFixtures.createOrderLineItemRequest(null, menu3.getId(), 2, 8000);
        orderLineItems.add(orderLineItem3);

        Order orderRequest = createOrderRequest(OrderType.DELIVERY, orderLineItems);

        // when - then
        assertThatThrownBy(() -> orderService.create(orderRequest))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("주문 등록시 주문 타입이 `배달`이라면 주소는 비어있을 수 없다.")
    @Test
    void deliveryEmpty() {
        // given
        Order orderRequest = createOrderRequest(OrderType.DELIVERY, orderLineItems);
        orderRequest.setDeliveryAddress(null);

        // when - then
        assertThatThrownBy(() -> orderService.create(orderRequest))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("주문 등록시 주문 타입이 `매장안 식사` 라면 주문한 테이블은 미리 등록된 상태여야 한다.")
    @Test
    void tableAlreadyRegistered() {
        // given
        Order orderRequest = createOrderRequestWithoutOrderTable(OrderType.EAT_IN, orderLineItems);

        // when - then
        assertThatThrownBy(() -> orderService.create(orderRequest))
                .isInstanceOf(NoSuchElementException.class);
    }



    @DisplayName("주문 등록시 주문 타입이 `매장안 식사`시 빈 테이블에는 주문을 등록할 수 없다.")
    @Test
    void emptyTableOrderNotRegistered() {
        // given
        orderTable.setEmpty(true);
        Order orderRequest = createOrderRequest(OrderType.EAT_IN, orderLineItems, orderTable);

        // when - then
        assertThatThrownBy(() -> orderService.create(orderRequest))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("주문 등록시 주문이 등록되면 주문 상태는 `주문대기` 상태로 변경되어야 한다.")
    @Test
    void orderStatusChanged() {
        // given
        Order orderRequest = createOrderRequest(OrderType.EAT_IN, orderLineItems, orderTable);

        // when
        Order result = orderService.create(orderRequest);

        // then
        assertThat(result.getStatus()).isEqualTo(OrderStatus.WAITING);
    }

    @DisplayName("주문 상태를 `수락`으로 변경할 수 있다.")
    @Test
    void orderStatusCanChange() {
        // given
        Order order = createOrderRequest(OrderType.EAT_IN, orderLineItems);
        order.setStatus(OrderStatus.WAITING);
        orderRepository.save(order);

        // when
        Order result = orderService.accept(order.getId());

        // then
        assertThat(result.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
    }

    @DisplayName("주문 수락시 주문은 미리 등록된 상태여야 한다.")
    @Test
    void orderRegisteredWhenOrderAccepted() {
        // given
        Order order = createOrderRequest(OrderType.EAT_IN, orderLineItems);
        order.setStatus(OrderStatus.WAITING);

        // when - then
        assertThatThrownBy(() -> orderService.accept(order.getId()))
                .isInstanceOf(NoSuchElementException.class);

    }

    @DisplayName("수락 시 주문의 상태는 `주문대기` 상태여야 한다.")
    @Test
    void orderStatusWaitingWhenOrderAccepted() {
        // given
        Order order = createOrderRequest(OrderType.EAT_IN, orderLineItems);
        order.setStatus(OrderStatus.SERVED);
        orderRepository.save(order);


        // when - then
        assertThatThrownBy(() -> orderService.accept(order.getId()))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("주문 상태를 `서빙완료`로 변경할 수 있다.")
    @Test
    void orderStatusToServed() {
        // given
        Order order = createOrderRequest(OrderType.EAT_IN, orderLineItems);
        order.setStatus(OrderStatus.ACCEPTED);
        orderRepository.save(order);


        // when - then
        Order result = orderService.serve(order.getId());

        assertThat(result.getStatus()).isEqualTo(OrderStatus.SERVED);

    }

    @DisplayName("주문 상태를 `서빙완료`로 변경시 주문은 미리 등록된 상태여야 한다.")
    @Test
    void orderRegisteredWhenToServed() {
        // given
        Order order = createOrderRequest(OrderType.EAT_IN, orderLineItems);
        order.setStatus(OrderStatus.ACCEPTED);


        // when - then
        assertThatThrownBy(() -> orderService.serve(order.getId()))
                .isInstanceOf(NoSuchElementException.class);

    }

    @DisplayName("주문 상태를 `서빙완료`로 변경시 주문의 상태는 `주문수락` 상태여야 한다.")
    @Test
    void orderStatusWhenToServed() {
        // given
        Order order = createOrderRequest(OrderType.EAT_IN, orderLineItems);
        order.setStatus(OrderStatus.WAITING);
        orderRepository.save(order);


        // when - then
        assertThatThrownBy(() -> orderService.serve(order.getId()))
                .isInstanceOf(IllegalStateException.class);

    }

    @DisplayName("주문 상태를 `배달시작` 으로 변경할 수 있다.")
    @Test
    void orderStatusToDeliveryStart() {
        // given
        Order order = createOrderRequest(OrderType.DELIVERY, orderLineItems);
        order.setStatus(OrderStatus.SERVED);
        orderRepository.save(order);

        // when
        Order result = orderService.startDelivery(order.getId());

        // then
        assertThat(result.getStatus()).isEqualTo(OrderStatus.DELIVERING);
    }

    @DisplayName("주문 상태를 `배달시작` 으로 변경할 시 주문은 미리 등록된 상태여야 한다.")
    @Test
    void orderAlreadyRegisteredWhenDeliveryStart() {
        // given
        Order order = createOrderRequest(OrderType.DELIVERY, orderLineItems);
        order.setStatus(OrderStatus.SERVED);

        // when - then
        assertThatThrownBy(() -> orderService.startDelivery(order.getId()))
                .isInstanceOf(NoSuchElementException.class);

    }

    @DisplayName("주문 상태를 `배달시작` 으로 변경할 시 주문 타입은 `배달`이여야 한다.")
    @Test
    void orderTypeWhenDeliveryStart() {

        // given
        Order order = createOrderRequest(OrderType.EAT_IN, orderLineItems);
        order.setStatus(OrderStatus.SERVED);
        orderRepository.save(order);

        // when - then
        assertThatThrownBy(() -> orderService.startDelivery(order.getId()))
                .isInstanceOf(IllegalStateException.class);

    }

    @DisplayName("주문 상태를 `배달시작` 으로 변경할 시 주문 상태는 `서빙완료` 상태여야 한다.")
    @Test
    void orderStatusWhenDeliveryStart() {

        // given
        Order order = createOrderRequest(OrderType.DELIVERY, orderLineItems);
        order.setStatus(OrderStatus.WAITING);
        orderRepository.save(order);

        // when - then
        assertThatThrownBy(() -> orderService.startDelivery(order.getId()))
                .isInstanceOf(IllegalStateException.class);

    }

    @DisplayName("주문 상태를 `배달완료`로 변경할 수 있다.")
    @Test
    void orderStatusWhenDeliveryComplete() {
        // given
        Order order = createOrderRequest(OrderType.DELIVERY, orderLineItems);
        order.setStatus(OrderStatus.DELIVERING);
        orderRepository.save(order);

        // when
        Order result = orderService.completeDelivery(order.getId());

        // then
        assertThat(result.getStatus()).isEqualTo(OrderStatus.DELIVERED);

    }

    @DisplayName("주문 상태를 `배달완료`로 변경시 주문은 미리 등록된 상태여야 한다.")
    @Test
    void orderAlreadyRegisteredWhenDeliveryComplete() {
        // given
        Order order = createOrderRequest(OrderType.DELIVERY, orderLineItems);
        order.setStatus(OrderStatus.DELIVERING);

        // when - then
        assertThatThrownBy(() -> orderService.completeDelivery(order.getId()))
                .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("주문 상태를 `배달완료`로 변경시 주문 상태는 `배달중` 상태여야 한다.")
    @Test
    void orderStatusDeliveringWhenDeliveryComplete() {
        // given
        Order order = createOrderRequest(OrderType.DELIVERY, orderLineItems);
        order.setStatus(OrderStatus.SERVED);
        orderRepository.save(order);

        // when - then
        assertThatThrownBy(() -> orderService.completeDelivery(order.getId()))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("주문 상태를 `완료`로 변경할 수 있다.")
    @Test
    void orderStatusToCompleted() {
        // given
        Order order = createOrderRequest(OrderType.DELIVERY, orderLineItems);
        order.setStatus(OrderStatus.DELIVERED);
        orderRepository.save(order);

        // when
        Order result = orderService.complete(order.getId());

        // then
        assertThat(result.getStatus()).isEqualTo(OrderStatus.COMPLETED);
    }

    @DisplayName("주문 상태를 `완료`로 변경시 주문은 미리 등록된 상태여야 한다.")
    @Test
    void orderAlreadyRegisteredWhenCompleted() {
        // given
        Order order = createOrderRequest(OrderType.DELIVERY, orderLineItems);
        order.setStatus(OrderStatus.DELIVERED);

        // when - then
        assertThatThrownBy(() -> orderService.complete(order.getId()))
                .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("주문 상태를 `완료`로 변경시 주문 타입이 배달이라면 주문 상태는 `배달완료` 상태 이여야 한다.")
    @Test
    void orderStatusWhenCompleted() {
        // given
        Order order = createOrderRequest(OrderType.DELIVERY, orderLineItems);
        order.setStatus(OrderStatus.DELIVERING);
        orderRepository.save(order);

        // when - then
        assertThatThrownBy(() -> orderService.complete(order.getId()))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("주문 상태를 `완료`로 변경시 주문 타입이 배달이 아니라면 주문 상태는 `서빙완료` 상태 이여야 한다.")
    @Test
    void orderTypeNotDeliveredWhenCompleted() {
        // given
        Order order = createOrderRequest(OrderType.TAKEOUT, orderLineItems);
        order.setStatus(OrderStatus.SERVED);
        orderRepository.save(order);

        // when
        Order result = orderService.complete(order.getId());

        // then
        assertThat(result.getStatus()).isEqualTo(OrderStatus.COMPLETED);
    }

    @DisplayName("주문 상태를 `완료`로 변경시 주문 타입이 매장내 식사라면 완료 시 주문 테이블은 비어지며 손님 수는 0으로 변경된다.")
    @Test
    void orderTableEmptyWhenCompleted() {
        // given
        Order order = createOrderRequest(OrderType.EAT_IN, orderLineItems, orderTable);
        order.setStatus(OrderStatus.SERVED);
        orderRepository.save(order);

        // when
        Order result = orderService.complete(order.getId());

        // then
        assertAll(
                () -> assertThat(result.getStatus()).isEqualTo(OrderStatus.COMPLETED),
                () -> assertThat(result.getOrderTable().isEmpty()).isTrue(),
                () -> assertThat(result.getOrderTable().getNumberOfGuests()).isEqualTo(0)
        );

    }

    @DisplayName("주문의 목록을 조회할 수 있다.")
    @Test
    void findAll() {
        // given
        Order order = createOrderRequest(OrderType.EAT_IN, orderLineItems, orderTable);
        orderRepository.save(order);

        // when
        List<Order> orders = orderService.findAll();

        // then
        assertThat(orders.size()).isEqualTo(1);

    }

}