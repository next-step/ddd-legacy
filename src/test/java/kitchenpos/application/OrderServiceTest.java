package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.infra.KitchenridersClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.util.*;

import static java.util.Collections.emptyList;
import static kitchenpos.application.MenuServiceTest.메뉴만들기;
import static kitchenpos.application.OrderTableServiceTest.주문테이블만들기;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;

public class OrderServiceTest {
    private OrderService orderService;
    private OrderRepository orderRepository = new InMemoryOrderRepository();
    private MenuRepository menuRepository = new InMemoryMenuRepository();
    private OrderTableRepository orderTableRepository = new InMemoryOrderTableRepository();
    private FakeKitchenridersClient kitchenridersClient = new FakeKitchenridersClient();
    private MenuGroupRepository menuGroupRepository = new InMemoryMenuGroupRepository();
    private ProductRepository productRepository = new InMemoryProductRepository();
    private Order deliveryOrder;
    private Order takeOutOrder;
    private Order eatInOrder;
    private List<OrderLineItem> orderLineItems;
    private OrderTable orderTable;

    @BeforeEach
    void setUp() {
        orderService = new OrderService(orderRepository, menuRepository, orderTableRepository, kitchenridersClient);
        orderLineItems = new ArrayList<>(Arrays.asList(주문항목만들기(menuRepository, menuGroupRepository, productRepository), 주문항목만들기(menuRepository, menuGroupRepository, productRepository)));
        orderTable = 주문테이블만들기(orderTableRepository);
        deliveryOrder = new Order();
        deliveryOrder.setType(OrderType.DELIVERY);
        deliveryOrder.setOrderLineItems(orderLineItems);
        deliveryOrder.setDeliveryAddress("배달주소");
        takeOutOrder = new Order();
        takeOutOrder.setType(OrderType.TAKEOUT);
        takeOutOrder.setOrderLineItems(orderLineItems);
        eatInOrder = new Order();
        eatInOrder.setType(OrderType.EAT_IN);
        eatInOrder.setOrderLineItems(orderLineItems);
        eatInOrder.setOrderTable(orderTable);
        eatInOrder.setOrderTableId(orderTable.getId());
    }

    @DisplayName("배달주문을 생성할 수 있다.")
    @Test
    void delivery_create() {
        final Order saved = 주문등록(deliveryOrder);

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
        deliveryOrder.setDeliveryAddress(null);

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> 주문등록(deliveryOrder));
    }

    @DisplayName("포장주문을 생성할 수 있다.")
    @Test
    void takeOut_create() {
        final Order saved = 주문등록(takeOutOrder);

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
        final Order saved = 주문등록(eatInOrder);

        assertAll(
                () -> assertThat(saved.getId()).isNotNull(),
                () -> assertThat(saved.getType()).isEqualTo(OrderType.EAT_IN),
                () -> assertThat(saved.getStatus()).isEqualTo(OrderStatus.WAITING),
                () -> assertThat(saved.getOrderDateTime()).isNotNull()
        );
    }

    @DisplayName("매장 식사시엔 비어있지 않은 테이블 정보를 지녀야한다.")
    @Test
    void eatIn_create_table() {
        OrderTable newTable = new OrderTable();
        newTable.setId(UUID.randomUUID());
        newTable.setEmpty(true);
        orderTableRepository.save(newTable);
        eatInOrder.setOrderTableId(newTable.getId());

        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> 주문등록(eatInOrder));
    }

    @DisplayName("주문 방법을 선택하지 않으면 주문할 수 없다.")
    @Test
    void create_type(){
        deliveryOrder.setType(null);
        takeOutOrder.setType(null);
        eatInOrder.setType(null);

        assertAll(
                () -> assertThatExceptionOfType(IllegalArgumentException.class)
                        .isThrownBy(() -> 주문등록(deliveryOrder)),
                () -> assertThatExceptionOfType(IllegalArgumentException.class)
                        .isThrownBy(() -> 주문등록(takeOutOrder)),
                () -> assertThatExceptionOfType(IllegalArgumentException.class)
                        .isThrownBy(() -> 주문등록(eatInOrder))
        );
    }

    @DisplayName("주문시 한 가지 이상의 비어있지 않은 주문항목이 필요하다.")
    @Test
    void create_orderLineItem() {
        deliveryOrder.setOrderLineItems(emptyList());

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> 주문등록(deliveryOrder));
    }

    @DisplayName("매장 식사주문은 수량이 0보다 적을 수 있다.")
    @ValueSource(strings = "-1")
    @ParameterizedTest
    void eatIn_create(int quantity) {
        orderLineItems.get(0).setQuantity(quantity);

        assertThat(주문등록(eatInOrder).getId()).isNotNull();
    }

    @DisplayName("포장주문과 배달주문은 수량이 0보다 작을 수 없다.")
    @ValueSource(strings = "-1")
    @ParameterizedTest
    void deliveryAndTakeOut_create(int quantity) {
        orderLineItems.get(0).setQuantity(quantity);

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> 주문등록(deliveryOrder));
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> 주문등록(takeOutOrder));
    }

    @DisplayName("주문항목은 메뉴와 가격이 동일해야한다.")
    @ValueSource(strings = {"3000000000"})
    @ParameterizedTest
    void create_menu(BigDecimal price) {
        orderLineItems.get(0).setPrice(price);

        assertAll(
                () -> assertThatExceptionOfType(IllegalArgumentException.class)
                        .isThrownBy(() -> 주문등록(deliveryOrder)),
                () -> assertThatExceptionOfType(IllegalArgumentException.class)
                        .isThrownBy(() -> 주문등록(takeOutOrder)),
                () -> assertThatExceptionOfType(IllegalArgumentException.class)
                        .isThrownBy(() -> 주문등록(eatInOrder))
        );
    }

    @DisplayName("대기 상태의 주문을 수락할 수 있다.")
    @Test
    void accept() {
        final Order delivery = 주문수락(주문등록(deliveryOrder));
        final Order takeOut = 주문수락(주문등록(takeOutOrder));
        final Order eatIn = 주문수락(주문등록(eatInOrder));

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
        deliveryOrder.setStatus(OrderStatus.ACCEPTED);
        takeOutOrder.setStatus(OrderStatus.ACCEPTED);
        eatInOrder.setStatus(OrderStatus.ACCEPTED);

        final Order delivery = 주문메뉴제공(주문만들기(orderRepository, deliveryOrder));
        final Order takeOut = 주문메뉴제공(주문만들기(orderRepository, takeOutOrder));
        final Order eatIn = 주문메뉴제공(주문만들기(orderRepository, eatInOrder));

        assertThat(delivery.getStatus()).isEqualTo(OrderStatus.SERVED);
        assertThat(takeOut.getStatus()).isEqualTo(OrderStatus.SERVED);
        assertThat(eatIn.getStatus()).isEqualTo(OrderStatus.SERVED);
    }

    @DisplayName("메뉴가 제공된 배달 주문만 배달을 시작할 수 있다.")
    @Test
    void startDelivery() {
        deliveryOrder.setStatus(OrderStatus.SERVED);
        takeOutOrder.setStatus(OrderStatus.SERVED);
        eatInOrder.setStatus(OrderStatus.SERVED);

        final Order delivery = 배달시작(주문만들기(orderRepository, deliveryOrder));

        assertThat(delivery.getStatus()).isEqualTo(OrderStatus.DELIVERING);


        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> 배달시작(주문만들기(orderRepository, takeOutOrder)));

        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> 배달시작(주문만들기(orderRepository, eatInOrder)));
    }

    @DisplayName("배달 중인 배달 주문만 배달 완료할 수 있다.")
    @Test
    void completeDelivery() {
        deliveryOrder.setStatus(OrderStatus.DELIVERING);
        takeOutOrder.setStatus(OrderStatus.SERVED);
        eatInOrder.setStatus(OrderStatus.SERVED);

        final Order delivery = 배달완료(주문만들기(orderRepository, deliveryOrder));

        assertThat(delivery.getStatus()).isEqualTo(OrderStatus.DELIVERED);


        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> 배달완료(주문만들기(orderRepository, takeOutOrder)));

        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> 배달완료(주문만들기(orderRepository, eatInOrder)));
    }

    @DisplayName("주문을 해결할 수 있다.")
    @Test
    void complete() {
        deliveryOrder.setStatus(OrderStatus.DELIVERED);
        takeOutOrder.setStatus(OrderStatus.SERVED);
        eatInOrder.setStatus(OrderStatus.SERVED);

        final Order delivery = 주문해결(주문만들기(orderRepository, deliveryOrder));
        final Order takeOut = 주문해결(주문만들기(orderRepository, takeOutOrder));
        final Order eatIn = 주문해결(주문만들기(orderRepository, eatInOrder));

        assertThat(delivery.getStatus()).isEqualTo(OrderStatus.COMPLETED);
        assertThat(takeOut.getStatus()).isEqualTo(OrderStatus.COMPLETED);
        assertThat(eatIn.getStatus()).isEqualTo(OrderStatus.COMPLETED);
    }


    Order 주문등록(Order request) {
        return orderService.create(request);
    }

    Order 주문수락(Order order) {
        return orderService.accept(order.getId());
    }

    Order 주문메뉴제공(Order order) {
        return orderService.serve(order.getId());
    }

    Order 배달시작(Order order) {
        return orderService.startDelivery(order.getId());
    }

    Order 배달완료(Order order) {
        return orderService.completeDelivery(order.getId());
    }

    Order 주문해결(Order order) {
        return orderService.complete(order.getId());
    }

    public static OrderLineItem 주문항목만들기(MenuRepository menuRepository, MenuGroupRepository menuGroupRepository, ProductRepository productRepository) {
        Menu menu = 메뉴만들기(menuRepository, menuGroupRepository, productRepository);
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenu(menu);
        orderLineItem.setMenuId(menu.getId());
        orderLineItem.setQuantity(2);
        orderLineItem.setPrice(menu.getPrice());
        return orderLineItem;
    }

    public static Order 주문만들기(OrderRepository orderRepository, Order order) {
        order.setId(UUID.randomUUID());
        return orderRepository.save(order);
    }

}
