package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.dummy.DummyMenu;
import kitchenpos.dummy.DummyOrder;
import kitchenpos.dummy.DummyOrderTable;
import kitchenpos.fake.FakeRidersClient;
import kitchenpos.fake.InMemoryMenuRepository;
import kitchenpos.fake.InMemoryOrderRepository;
import kitchenpos.fake.InMemoryOrderTableRepository;
import kitchenpos.infra.RidersClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;

import static kitchenpos.dummy.DummyOrder.createOrder;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class OrderServiceTest {

    private OrderRepository orderRepository = new InMemoryOrderRepository();
    private MenuRepository menuRepository = new InMemoryMenuRepository();
    private OrderTableRepository orderTableRepository = new InMemoryOrderTableRepository();
    private RidersClient ridersClient = new FakeRidersClient();
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderService = new OrderService(orderRepository, menuRepository, orderTableRepository, ridersClient);
    }

    @DisplayName("[정상] 매장 주문을 등록한다.")
    @Test
    void create_eat_in_test() {
        OrderTable orderTable = orderTableRepository.save(DummyOrderTable.createOrderTable(true));
        Order request = createOrder(OrderType.EAT_IN, orderTable, createOrderLineItem());
        Order actual = orderService.create(request);
        assertAll(
                () -> assertNotNull(actual.getId()),
                () -> assertNotNull(actual.getOrderTable().getId()),
                () -> assertNull(actual.getDeliveryAddress()),
                () -> assertThat(actual.getStatus()).isEqualTo(OrderStatus.WAITING)
        );
    }

    @DisplayName("[정상] 배달 주문을 등록한다.")
    @Test
    void create_delivery_test() {
        Order request = createOrder(OrderType.DELIVERY, createOrderLineItem());
        Order actual = orderService.create(request);
        assertAll(
                () -> assertNotNull(actual.getId()),
                () -> assertNotNull(actual.getDeliveryAddress()),
                () -> assertThat(actual.getStatus()).isEqualTo(OrderStatus.WAITING)
        );
    }

    @DisplayName("[정상] 포장 주문을 등록한다.")
    @Test
    void create_takeout_test() {
        OrderTable orderTable = orderTableRepository.save(DummyOrderTable.createOrderTable(true));
        Order request = createOrder(OrderType.TAKEOUT, orderTable, createOrderLineItem());
        Order actual = orderService.create(request);
        assertAll(
                () -> assertNotNull(actual.getId()),
                () -> assertNull(actual.getDeliveryAddress()),
                () -> assertThat(actual.getStatus()).isEqualTo(OrderStatus.WAITING)
        );
    }

    @DisplayName("[오류 - 메뉴 없음] 매장 주문을 등록한다.")
    @Test
    void create_eat_in_not_menu_test() {
        OrderTable orderTable = orderTableRepository.save(DummyOrderTable.createOrderTable(true));
        Order request = createOrder(OrderType.EAT_IN, orderTable, null);
        assertThatThrownBy(() -> orderService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("[오류 - 주문타입 없음] 매장 주문을 등록한다.")
    @Test
    void create_eat_in_not_order_type_test() {
        OrderTable orderTable = orderTableRepository.save(DummyOrderTable.createOrderTable(true));
        Order request = createOrder(OrderType.EAT_IN, orderTable, createOrderLineItem());
        request.setType(null);
        assertThatThrownBy(() -> orderService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("[오류 - 메뉴에 없는 상품] 포장 주문을 등록한다.")
    @Test
    void create_eat_in_not_in_menu_test() {
        OrderTable orderTable = orderTableRepository.save(DummyOrderTable.createOrderTable(true));
        Menu menu1 = DummyMenu.createMenu(true);
        Menu menu2 = DummyMenu.createMenu(true);
        List<OrderLineItem> orderLineItem = DummyOrder.createOrderLineItem(menu1, menu2);
        Order request = createOrder(OrderType.EAT_IN, orderTable, orderLineItem);
        assertThatThrownBy(() -> orderService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("[오류 - 메뉴에 없는 상품] 매장 주문을 등록한다.")
    @Test
    void create_eat_in_not_menu_displayed_test() {
        OrderTable orderTable = orderTableRepository.save(DummyOrderTable.createOrderTable(true));
        Menu menu1 = DummyMenu.createMenu(false);
        Menu menu2 = DummyMenu.createMenu(true);
        List<OrderLineItem> orderLineItem = DummyOrder.createOrderLineItem(menu1, menu2);
        Order request = createOrder(OrderType.EAT_IN, orderTable, orderLineItem);
        assertThatThrownBy(() -> orderService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("[오류 - 배달주문 주소 없음] 배달 주문을 등록한다.")
    @Test
    void create_takeout_not_menu_displayed_test() {
        OrderTable orderTable = orderTableRepository.save(DummyOrderTable.createOrderTable(true));
        Order request = createOrder(OrderType.DELIVERY, orderTable, createOrderLineItem());
        request.setDeliveryAddress(null);
        assertThatThrownBy(() -> orderService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("[오류 - 메뉴 가격과 상품 가격 다름] 매장 주문을 등록한다.")
    @Test
    void create_eat_in_not_equal_menu_price_and_product_price_test() {
        OrderTable orderTable = orderTableRepository.save(DummyOrderTable.createOrderTable(true));
        Order request = createOrder(OrderType.EAT_IN, orderTable, createOrderLineItem());
        request.getOrderLineItems().get(0).setPrice(BigDecimal.valueOf(1_000L));
        assertThatThrownBy(() -> orderService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("[오류 - 매장 주문인 경우 테이블 정보 필요] 매장 주문을 등록한다.")
    @Test
    void create_takeout_not_table__test() {
        Order request = createOrder(OrderType.EAT_IN, createOrderLineItem());
        assertThatThrownBy(() -> orderService.create(request))
                .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("[정상] 조리완료 변경한다.")
    @Test
    void accept() {
        OrderTable orderTable = orderTableRepository.save(DummyOrderTable.createOrderTable(true));
        Order request = createOrder(OrderType.EAT_IN, orderTable, createOrderLineItem());
        Order actual = orderService.create(request);
        assertThat(actual.getStatus()).isEqualTo(OrderStatus.WAITING);
        orderService.accept(actual.getId());
        assertAll(
                () -> assertNotNull(actual.getId()),
                () -> assertNotNull(actual.getOrderTable().getId()),
                () -> assertThat(actual.getStatus()).isEqualTo(OrderStatus.ACCEPTED)
        );
    }

    @DisplayName("[오류 - 대기중이 아닐때] 조리완료 변경한다.")
    @ValueSource(strings = {"ACCEPTED", "SERVED", "DELIVERING", "DELIVERED", "COMPLETED"})
    @ParameterizedTest
    void accept_not_wating(OrderStatus orderStatus) {
        OrderTable orderTable = orderTableRepository.save(DummyOrderTable.createOrderTable(true));
        Order request = createOrder(OrderType.EAT_IN, orderTable, createOrderLineItem());
        Order actual = orderService.create(request);
        actual.setStatus(orderStatus);
        orderRepository.save(actual);
        assertThatThrownBy(() -> orderService.accept(actual.getId()))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("[정상] 서빙을 한다.")
    @Test
    void serve() {
        OrderTable orderTable = orderTableRepository.save(DummyOrderTable.createOrderTable(true));
        Order request = createOrder(OrderType.EAT_IN, orderTable, createOrderLineItem());
        Order actual = orderService.create(request);
        orderService.accept(actual.getId());
        orderService.serve(actual.getId());
        assertAll(
                () -> assertNotNull(actual.getId()),
                () -> assertNotNull(actual.getOrderTable().getId()),
                () -> assertThat(actual.getStatus()).isEqualTo(OrderStatus.SERVED)
        );
    }

    @DisplayName("[오류 - 조리완료가 아닐때] 서빙을 한다.")
    @ValueSource(strings = {"WAITING", "SERVED", "DELIVERING", "DELIVERED", "COMPLETED"})
    @ParameterizedTest
    void accept_not_serving(OrderStatus orderStatus) {
        OrderTable orderTable = orderTableRepository.save(DummyOrderTable.createOrderTable(true));
        Order request = createOrder(OrderType.EAT_IN, orderTable, createOrderLineItem());
        Order actual = orderService.create(request);
        orderService.accept(actual.getId());
        actual.setStatus(orderStatus);
        orderRepository.save(actual);
        assertThatThrownBy(() -> orderService.serve(actual.getId()))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("[정상] 배달을 시작한다.")
    @Test
    void startDelivery() {
        Order request = createOrder(OrderType.DELIVERY, createOrderLineItem());
        Order actual = orderService.create(request);
        orderService.accept(actual.getId());
        orderService.serve(actual.getId());
        orderService.startDelivery(actual.getId());
        assertAll(
                () -> assertNotNull(actual.getId()),
                () -> assertNotNull(actual.getDeliveryAddress()),
                () -> assertThat(actual.getStatus()).isEqualTo(OrderStatus.DELIVERING)

        );
    }

    @DisplayName("[오류 - 배달 타입이 아니면 배달중으로 변경할 수 없음] 배달을 시작한다.")
    @ValueSource(strings = {"TAKEOUT", "EAT_IN"})
    @ParameterizedTest
    void accept_not_delivery(OrderType type) {
        Order request = createOrder(OrderType.DELIVERY, createOrderLineItem());
        Order actual = orderService.create(request);
        orderService.accept(actual.getId());
        actual.setType(type);
        orderRepository.save(actual);
        assertThatThrownBy(() -> orderService.startDelivery(actual.getId()))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("[오류 - 조리완료가 아닐때] 배달을 시작 한다.")
    @ValueSource(strings = {"WAITING", "ACCEPTED", "DELIVERING", "DELIVERED", "COMPLETED"})
    @ParameterizedTest
    void accept_not_delivery(OrderStatus orderStatus) {
        Order request = createOrder(OrderType.DELIVERY, createOrderLineItem());
        Order actual = orderService.create(request);
        orderService.accept(actual.getId());
        actual.setStatus(orderStatus);
        orderRepository.save(actual);
        assertThatThrownBy(() -> orderService.startDelivery(actual.getId()))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("[정상] 배달을 완료한다.")
    @Test
    void completeDelivery() {
        OrderTable orderTable = orderTableRepository.save(DummyOrderTable.createOrderTable(true));
        Order request = createOrder(OrderType.DELIVERY, orderTable, createOrderLineItem());
        Order actual = orderService.create(request);
        orderService.accept(actual.getId());
        orderService.serve(actual.getId());
        orderService.startDelivery(actual.getId());
        orderService.completeDelivery(actual.getId());
        assertAll(
                () -> assertNotNull(actual.getId()),
                () -> assertNotNull(actual.getDeliveryAddress()),
                () -> assertThat(actual.getStatus()).isEqualTo(OrderStatus.DELIVERED)
        );
    }

    @DisplayName("[오류 - 배달중 상태가 아닐때] 배달을 완료 한다.")
    @ValueSource(strings = {"WAITING", "ACCEPTED", "SERVED", "DELIVERED", "COMPLETED"})
    @ParameterizedTest
    void completeDelivery_not_delivering(OrderStatus orderStatus) {
        Order request = createOrder(OrderType.DELIVERY, createOrderLineItem());
        Order actual = orderService.create(request);
        orderService.accept(actual.getId());
        orderService.serve(actual.getId());
        orderService.startDelivery(actual.getId());
        actual.setStatus(orderStatus);
        orderRepository.save(actual);
        assertThatThrownBy(() -> orderService.completeDelivery(actual.getId()))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("[정상] 배달 주문을 완료한다.")
    @Test
    void complete_delivery() {
        Order request = createOrder(OrderType.DELIVERY, createOrderLineItem());
        Order actual = orderService.create(request);
        orderService.accept(actual.getId());
        orderService.serve(actual.getId());
        orderService.startDelivery(actual.getId());
        orderService.completeDelivery(actual.getId());
        orderService.complete(actual.getId());
        assertAll(
                () -> assertNotNull(actual.getId()),
                () -> assertNotNull(actual.getDeliveryAddress()),
                () -> assertThat(actual.getStatus()).isEqualTo(OrderStatus.COMPLETED)
        );

    }

    @DisplayName("[정상] 매장 주문을 완료한다.")
    @Test
    void complete_eat_in() {
        OrderTable orderTable = orderTableRepository.save(DummyOrderTable.createOrderTable(true));
        Order request = createOrder(OrderType.EAT_IN, orderTable, createOrderLineItem());
        Order actual = orderService.create(request);
        orderService.accept(actual.getId());
        orderService.serve(actual.getId());
        orderService.complete(actual.getId());
        assertAll(
                () -> assertNotNull(actual.getId()),
                () -> assertNotNull(actual.getOrderTable()),
                () -> assertThat(actual.getOrderTable().isOccupied()).isFalse(),
                () -> assertThat(actual.getStatus()).isEqualTo(OrderStatus.COMPLETED)
        );
    }

    @DisplayName("[정상] 포장 주문을 완료한다.")
    @Test
    void complete_take_out() {
        OrderTable orderTable = orderTableRepository.save(DummyOrderTable.createOrderTable(true));
        Order request = createOrder(OrderType.TAKEOUT, orderTable, createOrderLineItem());
        Order actual = orderService.create(request);
        orderService.accept(actual.getId());
        orderService.serve(actual.getId());
        orderService.complete(actual.getId());
        assertAll(
                () -> assertNotNull(actual.getId()),
                () -> assertThat(actual.getStatus()).isEqualTo(OrderStatus.COMPLETED)
        );
    }

    @DisplayName("[오류 - 배달완료 상태가 아닐때] 배달 주문을 완료 한다.")
    @ValueSource(strings = {"WAITING", "ACCEPTED", "SERVED", "DELIVERING", "COMPLETED"})
    @ParameterizedTest
    void completeDelivery_not_delivered(OrderStatus orderStatus) {
        Order request = createOrder(OrderType.DELIVERY, createOrderLineItem());
        Order actual = orderService.create(request);
        orderService.accept(actual.getId());
        orderService.serve(actual.getId());
        orderService.startDelivery(actual.getId());
        orderService.completeDelivery(actual.getId());
        actual.setStatus(orderStatus);
        orderRepository.save(actual);
        assertThatThrownBy(() -> orderService.complete(actual.getId()))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("[오류 - 서빙완료 상태가 아닐때] 매장 주문을 완료 한다.")
    @ValueSource(strings = {"WAITING", "ACCEPTED", "DELIVERING", "DELIVERED", "COMPLETED"})
    @ParameterizedTest
    void complete_not_serve(OrderStatus orderStatus) {
        OrderTable orderTable = orderTableRepository.save(DummyOrderTable.createOrderTable(true));
        Order request = createOrder(OrderType.EAT_IN, orderTable, createOrderLineItem());
        Order actual = orderService.create(request);
        orderService.accept(actual.getId());
        orderService.serve(actual.getId());
        actual.setStatus(orderStatus);
        orderRepository.save(actual);
        assertThatThrownBy(() -> orderService.complete(actual.getId()))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("[정상] 주문을 조회한다.")
    @Test
    void findAll() {
        OrderTable orderTable1 = orderTableRepository.save(DummyOrderTable.createOrderTable(true));
        Order request1 = createOrder(OrderType.DELIVERY, orderTable1, createOrderLineItem());
        Order actual1 = orderService.create(request1);

        OrderTable orderTable2 = orderTableRepository.save(DummyOrderTable.createOrderTable(true));
        Order request2 = createOrder(OrderType.DELIVERY, orderTable2, createOrderLineItem());
        Order actual2 = orderService.create(request2);

        List<Order> actual = orderService.findAll();
        assertAll(
                () -> assertNotNull(actual.get(0).getId()),
                () -> assertThat(actual.size()).isSameAs(2)
        );

    }


    private List<OrderLineItem> createOrderLineItem() {
        Menu menu1 = menuRepository.save(DummyMenu.createMenu(true));
        Menu menu2 = menuRepository.save(DummyMenu.createMenu(true));
        return DummyOrder.createOrderLineItem(menu1, menu2);
    }
}