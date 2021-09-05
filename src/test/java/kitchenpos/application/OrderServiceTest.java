package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.utils.fixture.MenuFixture;
import kitchenpos.utils.fixture.OrderFixture;
import kitchenpos.utils.fixture.OrderTableFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertAll;

public class OrderServiceTest {
    private FakeKitchenridersClient kitchenridersClient = new FakeKitchenridersClient();
    private OrderService orderService;
    private OrderRepository orderRepository;
    private MenuRepository menuRepository;
    private OrderTableRepository orderTableRepository;

    @BeforeEach
    void setUp() {
        orderRepository = new InMemoryOrderRepository();
        menuRepository = new InMemoryMenuRepository();
        orderTableRepository = new InMemoryOrderTableRepository();
        orderService = new OrderService(orderRepository, menuRepository, orderTableRepository, kitchenridersClient);
    }

    @DisplayName("배달주문을 생성할 수 있다.")
    @Test
    void delivery_create() {
        final Menu menu = menuRepository.save(MenuFixture.기본메뉴());
        final Order saved = 주문등록(OrderFixture.배달주문(menu));

        assertAll(
                () -> assertThat(saved.getId()).isNotNull(),
                () -> assertThat(saved.getType()).isEqualTo(OrderType.DELIVERY),
                () -> assertThat(saved.getStatus()).isEqualTo(OrderStatus.WAITING),
                () -> assertThat(saved.getOrderDateTime()).isNotNull(),
                () -> assertThat(kitchenridersClient.getCallCounter()).isEqualTo(0)
        );
    }

    @DisplayName("배달주문은 배달주소를 포함해야한다.")
    @Test
    void delivery_create_address() {
        final Menu menu = menuRepository.save(MenuFixture.기본메뉴());
        final Order deliveryOrder = OrderFixture.배달주문(menu);
        deliveryOrder.setDeliveryAddress(null);

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> 주문등록(deliveryOrder));
    }

    @DisplayName("포장주문을 생성할 수 있다.")
    @Test
    void takeOut_create() {
        final Menu menu = menuRepository.save(MenuFixture.기본메뉴());
        final Order saved = 주문등록(OrderFixture.포장주문(menu));

        assertAll(
                () -> assertThat(saved.getId()).isNotNull(),
                () -> assertThat(saved.getType()).isEqualTo(OrderType.TAKEOUT),
                () -> assertThat(saved.getStatus()).isEqualTo(OrderStatus.WAITING),
                () -> assertThat(saved.getOrderDateTime()).isNotNull()
        );
    }

    @DisplayName("매장 식사주문을 생성할 수 있다.")
    @Test
    void eatIn_create() {
        final Menu menu = menuRepository.save(MenuFixture.기본메뉴());
        final OrderTable orderTable = orderTableRepository.save(OrderTableFixture.앉은테이블());
        final Order saved = 주문등록(OrderFixture.매장주문(menu, orderTable));

        assertAll(
                () -> assertThat(saved.getId()).isNotNull(),
                () -> assertThat(saved.getType()).isEqualTo(OrderType.EAT_IN),
                () -> assertThat(saved.getStatus()).isEqualTo(OrderStatus.WAITING),
                () -> assertThat(saved.getOrderDateTime()).isNotNull()
        );
    }

    @DisplayName("테이블이 비어있는 경우 매장식사 주문을 받을수 없다.")
    @Test
    void eatIn_create_table() {
        final Menu menu = menuRepository.save(MenuFixture.기본메뉴());
        final OrderTable orderTable = orderTableRepository.save(OrderTableFixture.주문테이블());
        final Order eatInOrder = OrderFixture.매장주문(menu, orderTable);

        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> 주문등록(eatInOrder));
    }

    @DisplayName("주문 방법을 선택하지 않으면 주문할 수 없다.")
    @NullSource
    @ParameterizedTest
    void create_type(OrderType type){
        final Menu menu = menuRepository.save(MenuFixture.기본메뉴());
        final Order order = OrderFixture.주문(menu);
        order.setType(type);

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> 주문등록(order));
    }

    @DisplayName("주문상품이 비어있는 경우 IllegalArgumentException을 반환한다.")
    @Test
    void create_orderLineItem() {
        final Menu menu = menuRepository.save(MenuFixture.기본메뉴());
        final Order deliveryOrder = OrderFixture.배달주문(menu);
        deliveryOrder.setOrderLineItems(emptyList());

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> 주문등록(deliveryOrder));
    }

    @DisplayName("매장 식사주문은 수량이 0보다 적을 수 있다.")
    @ValueSource(strings = "-1")
    @ParameterizedTest
    void eatIn_create(int quantity) {
        final Menu menu = menuRepository.save(MenuFixture.기본메뉴());
        final OrderTable orderTable = orderTableRepository.save(OrderTableFixture.앉은테이블());
        final Order eatInOrder = OrderFixture.매장주문(menu, orderTable);
        eatInOrder.getOrderLineItems().get(0).setQuantity(quantity);

        final Order expected = 주문등록(eatInOrder);

        assertThat(expected.getId()).isNotNull();
    }

    @DisplayName("배달주문은 수량이 0보다 작을 수 없다.")
    @ValueSource(strings = "-1")
    @ParameterizedTest
    void delivery_create(int quantity) {
        final Menu menu = menuRepository.save(MenuFixture.기본메뉴());
        final Order deliveryOrder = OrderFixture.배달주문(menu);
        deliveryOrder.getOrderLineItems().get(0).setQuantity(quantity);

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> 주문등록(deliveryOrder));
    }

    @DisplayName("포장주문은 수량이 0보다 작을 수 없다.")
    @ValueSource(strings = "-1")
    @ParameterizedTest
    void takeOut_create(int quantity) {
        final Menu menu = menuRepository.save(MenuFixture.기본메뉴());
        final Order takeOutOrder = OrderFixture.포장주문(menu);
        takeOutOrder.getOrderLineItems().get(0).setQuantity(quantity);

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> 주문등록(takeOutOrder));
    }

    @DisplayName("주문항목은 메뉴와 가격이 동일해야한다.")
    @ValueSource(strings = {"3000000000"})
    @ParameterizedTest
    void create_menu(BigDecimal price) {
        final Menu menu = menuRepository.save(MenuFixture.기본메뉴());
        final Order deliveryOrder = OrderFixture.배달주문(menu);
        deliveryOrder.getOrderLineItems().get(0).setPrice(price);

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> 주문등록(deliveryOrder));
    }

    @DisplayName("대기 상태의 주문을 수락할 수 있다.")
    @Test
    void accept() {
        final Menu menu = menuRepository.save(MenuFixture.기본메뉴());
        final OrderTable orderTable = orderTableRepository.save(OrderTableFixture.앉은테이블());
        final Order deliveryOrder = OrderFixture.배달주문(menu);
        final Order takeOutOrder = OrderFixture.포장주문(menu);
        final Order eatInOrder = OrderFixture.매장주문(menu, orderTable);

        final Order delivery = 주문수락(orderRepository.save(deliveryOrder));
        final Order takeOut = 주문수락(orderRepository.save(takeOutOrder));
        final Order eatIn = 주문수락(orderRepository.save(eatInOrder));

        assertAll(
                () -> assertThat(delivery.getStatus()).isEqualTo(OrderStatus.ACCEPTED),
                () -> assertThat(kitchenridersClient.getCallCounter()).isEqualTo(1)
        );

        assertThat(takeOut.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
        assertThat(eatIn.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
    }

    @DisplayName("수락 상태의 주문을 메뉴 제공상태로 변경할 수 있다.")
    @Test
    void serve() {
        final Menu menu = menuRepository.save(MenuFixture.기본메뉴());
        final OrderTable orderTable = orderTableRepository.save(OrderTableFixture.앉은테이블());
        final Order deliveryOrder = OrderFixture.배달주문(menu);
        final Order takeOutOrder = OrderFixture.포장주문(menu);
        final Order eatInOrder = OrderFixture.매장주문(menu, orderTable);
        deliveryOrder.setStatus(OrderStatus.ACCEPTED);
        takeOutOrder.setStatus(OrderStatus.ACCEPTED);
        eatInOrder.setStatus(OrderStatus.ACCEPTED);

        final Order delivery = 주문메뉴제공(orderRepository.save(deliveryOrder));
        final Order takeOut = 주문메뉴제공(orderRepository.save(takeOutOrder));
        final Order eatIn = 주문메뉴제공(orderRepository.save(eatInOrder));

        assertThat(delivery.getStatus()).isEqualTo(OrderStatus.SERVED);
        assertThat(takeOut.getStatus()).isEqualTo(OrderStatus.SERVED);
        assertThat(eatIn.getStatus()).isEqualTo(OrderStatus.SERVED);
    }

    @DisplayName("메뉴가 제공된 배달 주문만 배달을 시작할 수 있다.")
    @Test
    void startDelivery() {
        final Menu menu = menuRepository.save(MenuFixture.기본메뉴());
        final OrderTable orderTable = orderTableRepository.save(OrderTableFixture.앉은테이블());
        final Order deliveryOrder = OrderFixture.배달주문(menu);
        final Order takeOutOrder = OrderFixture.포장주문(menu);
        final Order eatInOrder = OrderFixture.매장주문(menu, orderTable);
        deliveryOrder.setStatus(OrderStatus.SERVED);
        takeOutOrder.setStatus(OrderStatus.SERVED);
        eatInOrder.setStatus(OrderStatus.SERVED);

        final Order delivery = 배달시작(orderRepository.save(deliveryOrder));

        assertThat(delivery.getStatus()).isEqualTo(OrderStatus.DELIVERING);


        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> 배달시작(orderRepository.save(takeOutOrder)));

        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> 배달시작(orderRepository.save(eatInOrder)));
    }

    @DisplayName("배달 중인 배달 주문만 배달 완료할 수 있다.")
    @Test
    void completeDelivery() {
        final Menu menu = menuRepository.save(MenuFixture.기본메뉴());
        final OrderTable orderTable = orderTableRepository.save(OrderTableFixture.앉은테이블());
        final Order deliveryOrder = OrderFixture.배달주문(menu);
        final Order takeOutOrder = OrderFixture.포장주문(menu);
        final Order eatInOrder = OrderFixture.매장주문(menu, orderTable);
        deliveryOrder.setStatus(OrderStatus.DELIVERING);
        takeOutOrder.setStatus(OrderStatus.SERVED);
        eatInOrder.setStatus(OrderStatus.SERVED);

        final Order delivery = 배달완료(orderRepository.save(deliveryOrder));

        assertThat(delivery.getStatus()).isEqualTo(OrderStatus.DELIVERED);


        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> 배달완료(orderRepository.save(takeOutOrder)));

        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> 배달완료(orderRepository.save(eatInOrder)));
    }

    @DisplayName("주문을 해결할 수 있다.")
    @Test
    void complete() {
        final Menu menu = menuRepository.save(MenuFixture.기본메뉴());
        final OrderTable orderTable = orderTableRepository.save(OrderTableFixture.앉은테이블());
        final Order deliveryOrder = OrderFixture.배달주문(menu);
        final Order takeOutOrder = OrderFixture.포장주문(menu);
        final Order eatInOrder = OrderFixture.매장주문(menu, orderTable);
        deliveryOrder.setStatus(OrderStatus.DELIVERED);
        takeOutOrder.setStatus(OrderStatus.SERVED);
        eatInOrder.setStatus(OrderStatus.SERVED);

        final Order delivery = 주문해결(orderRepository.save(deliveryOrder));
        final Order takeOut = 주문해결(orderRepository.save(takeOutOrder));
        final Order eatIn = 주문해결(orderRepository.save(eatInOrder));

        assertThat(delivery.getStatus()).isEqualTo(OrderStatus.COMPLETED);
        assertThat(takeOut.getStatus()).isEqualTo(OrderStatus.COMPLETED);
        assertThat(eatIn.getStatus()).isEqualTo(OrderStatus.COMPLETED);
    }

    private Order 주문등록(Order request) {
        return orderService.create(request);
    }

    private Order 주문수락(Order order) {
        return orderService.accept(order.getId());
    }

    private Order 주문메뉴제공(Order order) {
        return orderService.serve(order.getId());
    }

    private Order 배달시작(Order order) {
        return orderService.startDelivery(order.getId());
    }

    private Order 배달완료(Order order) {
        return orderService.completeDelivery(order.getId());
    }

    private Order 주문해결(Order order) {
        return orderService.complete(order.getId());
    }

}
