package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.exception.*;
import kitchenpos.infra.FakeKitchenridersClient;
import kitchenpos.infra.Kitchenriders;
import kitchenpos.repository.InMemoryMenuRepository;
import kitchenpos.repository.InMemoryOrderRepository;
import kitchenpos.repository.InMemoryOrderTableRepository;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertAll;


@DisplayName("[주문]")
@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    private final OrderRepository orderRepository = new InMemoryOrderRepository();
    private final MenuRepository menuRepository = new InMemoryMenuRepository();
    private final OrderTableRepository orderTableRepository = new InMemoryOrderTableRepository();
    private final Kitchenriders kitchenridersClient = new FakeKitchenridersClient();

    private OrderService orderService;

    @BeforeEach
    void setUp() {
        this.orderService = new OrderService(orderRepository, menuRepository, orderTableRepository, kitchenridersClient);
    }

    @Test
    @DisplayName("주문 방식은 반드시 있어야 한다.")
    void orderTypeNotNullTest() {
        final Order order = new Order();

        AssertionsForClassTypes.assertThatThrownBy(() -> orderService.create(order))
                .isInstanceOf(OrderTypeNullException.class);
    }

    @Test
    @DisplayName("주문시 주문 메뉴는 반드시 있어야 한다.")
    void orderLineItemNotEmptyTest() {
        final Order order = createOrder(OrderType.TAKEOUT);

        AssertionsForClassTypes.assertThatThrownBy(() -> orderService.create(order))
                .isInstanceOf(OrderLineItemEmptyException.class);
    }

    @Test
    @DisplayName("조회된 메뉴의 갯수와 주문 메뉴에 포함된 메뉴의 갯수는 같아야 한다.")
    void menuSizeSameOrderLineItemSizeTest() {
        final Menu menu = createMenu();
        final OrderLineItem orderLineItem = createOrderLineItem(menu.getId(), menu);
        final OrderLineItem notExistOrderLineItem = createOrderLineItem(menu.getId(), menu);
        final Order order = createOrder(OrderType.TAKEOUT, Arrays.asList(orderLineItem, notExistOrderLineItem));

        menuRepository.save(menu);

        AssertionsForClassTypes.assertThatThrownBy(() -> orderService.create(order))
                .isInstanceOf(NotTheSameSizeException.class);
    }


    @Test
    @DisplayName("주문 방식이 `배달`, `포장` 인경우에는 주문한 메뉴의 갯수는 반드시 있어야한다.")
    void deliveryTakeoutQuantityLessThanZeroTest() {
        final Menu menu = createMenu();
        final OrderLineItem orderLineItem = createOrderLineItem(menu.getId(), menu, -1);
        final Order order = createOrder(OrderType.TAKEOUT, Collections.singletonList(orderLineItem));

        menuRepository.save(menu);

        AssertionsForClassTypes.assertThatThrownBy(() -> orderService.create(order))
                .isInstanceOf(QuantityLessThenZeroException.class);
    }

    @Test
    @DisplayName("해당 메뉴가 미노출 상태라면 주문할 수 없다.")
    void menuDisplayIsFalseTest() {
        final Menu menu = createMenu(false);
        final OrderLineItem orderLineItem = createOrderLineItem(menu.getId(), menu, 2);
        final Order order = createOrder(OrderType.TAKEOUT, Collections.singletonList(orderLineItem));

        menuRepository.save(menu);

        AssertionsForClassTypes.assertThatThrownBy(() -> orderService.create(order))
                .isInstanceOf(DisplayFalseException.class);
    }


    @Test
    @DisplayName("해당 메뉴의 가격과 주문 메뉴의 가격이 일치 하지 않으면 주문할 수 없다.")
    void menuPriceNotTheSameRequestPriceTest() {
        final Menu menu = createMenu(true, BigDecimal.valueOf(18_000L));
        final OrderLineItem orderLineItem = createOrderLineItem(menu.getId(), menu, 2, BigDecimal.valueOf(19_000L));
        final Order order = createOrder(OrderType.TAKEOUT, Arrays.asList(orderLineItem));

        menuRepository.save(menu);

        AssertionsForClassTypes.assertThatThrownBy(() -> orderService.create(order))
                .isInstanceOf(NotTheSamePriceException.class);
    }


    @Test
    @DisplayName("주문 방식이 `배달`인 경우, 배달하고자 하는 주소는 반드시 존재해야 한다. 없다면 DeliveryAddressEmptyException 발생")
    void deliveryRequiredDeliveryAddress() {
        final Menu menu = createMenu(true, BigDecimal.valueOf(18_000L));
        final OrderLineItem orderLineItem = createOrderLineItem(menu.getId(), menu, 2, BigDecimal.valueOf(18_000L));
        final Order orderRequest = createOrder(OrderType.DELIVERY, Collections.singletonList(orderLineItem));

        menuRepository.save(menu);

        AssertionsForClassTypes.assertThatThrownBy(() -> orderService.create(orderRequest))
                .isInstanceOf(DeliveryAddressEmptyException.class);
    }

    @Test
    @DisplayName("매장 식사인데, 주문 테이블 ID가 존재하지 않는 경우 OrderTableNotFoundException 발생")
    void orderTableNotFoundTest() {
        final Menu menu = createMenu(true, BigDecimal.valueOf(18_000L));
        final OrderLineItem orderLineItem = createOrderLineItem(menu.getId(), menu, 2, BigDecimal.valueOf(18_000L));
        final Order orderRequest = createOrder(OrderType.EAT_IN, UUID.randomUUID(), Collections.singletonList(orderLineItem));

        menuRepository.save(menu);

        AssertionsForClassTypes.assertThatThrownBy(() -> orderService.create(orderRequest))
                .isInstanceOf(OrderTableNotFoundException.class);
    }


    @Test
    @DisplayName("주문테이블이 비어있다면 IllegalStateException 발생")
    void orderTableIsEmptyTrueTest() {
        final OrderTable orderTable = createOrderTable(true);
        final Menu menu = createMenu(true, BigDecimal.valueOf(18_000L));
        final OrderLineItem orderLineItem = createOrderLineItem(menu.getId(), menu, 2, BigDecimal.valueOf(18_000L));
        final Order orderRequest = createOrder(OrderType.EAT_IN, orderTable.getId(), Collections.singletonList(orderLineItem));

        orderTableRepository.save(orderTable);
        menuRepository.save(menu);

        AssertionsForClassTypes.assertThatThrownBy(() -> orderService.create(orderRequest))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("주문을 할 수 있다")
    void orderTableIsEmptyTru1eTest() {

        final OrderTable orderTable = createOrderTable();
        final Menu menu = createMenu(true, BigDecimal.valueOf(18_000L));
        final OrderLineItem orderLineItem = createOrderLineItem(menu.getId(), menu, 2, BigDecimal.valueOf(18_000L));
        final Order orderRequest = createOrder(OrderType.EAT_IN, orderTable.getId(), Collections.singletonList(orderLineItem));

        orderTableRepository.save(orderTable);
        menuRepository.save(menu);

        Order actual = orderService.create(orderRequest);
        assertAll(
                () -> Assertions.assertThat(actual.getStatus()).isEqualTo(OrderStatus.WAITING),
                () -> Assertions.assertThat(actual.getType()).isEqualTo(OrderType.EAT_IN)
        );
    }

    @Test
    @DisplayName("주문 수락 시점에 주문 정보가 있어야 한다.")
    void orderAcceptExistOrderTest() {
        final Order order = createOrder(OrderType.DELIVERY);

        AssertionsForClassTypes.assertThatThrownBy(() -> orderService.accept(order.getId()))
                .isInstanceOf(NoSuchElementException.class);

    }


    @Test
    @DisplayName("주문 수락시 OrderType 상태는 `WAITING` 이어야 한다.")
    void orderAcceptStatusTest() {
        final Order order = createOrder(OrderStatus.ACCEPTED);

        orderRepository.save(order);

        AssertionsForClassTypes.assertThatThrownBy(() -> orderService.accept(order.getId()))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("주문 타입이 배달인 경우 라이더에게 주문을 요청한다.")
    void orderAcceptStatusDeliveryVerifyKitchenridersClientTest() {
        final Order order = createOrder(OrderType.DELIVERY, OrderStatus.WAITING, Collections.emptyList());

        orderRepository.save(order);

        orderService.accept(order.getId());
//        Mockito.verify(kitchenridersClient, Mockito.atLeastOnce()).requestDelivery(order.getId(), BigDecimal.valueOf(18_000L), "ABC");
    }

    @Test
    @DisplayName("주문을 수락한다")
    void acceptOrderTest() {
        final Order order = createOrder(OrderType.DELIVERY, OrderStatus.WAITING, Collections.emptyList());

        orderRepository.save(order);

        final Order actual = orderService.accept(order.getId());
        Assertions.assertThat(actual.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
    }

    @Test
    @DisplayName("주문이 접수되었을때 해당 주문이 없다면 예외 발생")
    void serveOrderNotFundTest() {
        AssertionsForClassTypes.assertThatThrownBy(() -> orderService.serve(UUID.randomUUID()))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    @DisplayName("주문이 되었을때 상태가 ACCEPTED여야 한다.")
    void serveOrderStatusTest() {
        final Order order = createOrder(OrderStatus.SERVED);

        orderRepository.save(order);

        AssertionsForClassTypes.assertThatThrownBy(() -> orderService.serve(order.getId()))
                .isInstanceOf(IllegalStateException.class);
    }


    @Test
    @DisplayName("주문이 도착하였을 경우 ")
    void serveSuccessTest() {
        final Order order = createOrder(OrderStatus.ACCEPTED);

        orderRepository.save(order);

        final Order actual = orderService.serve(order.getId());
        Assertions.assertThat(actual.getStatus()).isEqualTo(OrderStatus.SERVED);
    }

    /*
     *  배달 시작
     */

    @Test
    @DisplayName("배달을 시작한다.")
    void delivery() {
        final Order order = createOrder(OrderType.DELIVERY, OrderStatus.SERVED);

        orderRepository.save(order);

        final Order actual = orderService.startDelivery(order.getId());
        Assertions.assertThat(actual.getStatus()).isEqualTo(OrderStatus.DELIVERING);
    }


    @Test
    @DisplayName("배달을 시작시 해당 주문이 없으면 안된다.")
    void deliveryOrderNotFoundTest() {
        final Order order = createOrder();

        AssertionsForClassTypes.assertThatThrownBy(() -> orderService.startDelivery(order.getId()))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    @DisplayName("배달을 시작시 주문 타입은 DELIVERY여야 한다.")
    void deliveryOrderTypeTest() {
        final Order order = createOrder(OrderType.TAKEOUT);

        orderRepository.save(order);

        AssertionsForClassTypes.assertThatThrownBy(() -> orderService.startDelivery(order.getId()))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("배달을 시작시 주문 상태는 SERVED여야 한다.")
    void deliveryOrderStatusTest() {
        final Order order = createOrder(OrderType.DELIVERY, OrderStatus.WAITING);

        orderRepository.save(order);

        AssertionsForClassTypes.assertThatThrownBy(() -> orderService.startDelivery(order.getId()))
                .isInstanceOf(IllegalStateException.class);
    }

    /*
     *  배달 완료
     */

    @Test
    @DisplayName("배달을 완료한다.")
    void completeDeliveryTest() {
        final Order order = createOrder(OrderStatus.DELIVERING);

        orderRepository.save(order);

        final Order actual = orderService.completeDelivery(order.getId());
        Assertions.assertThat(actual.getStatus()).isEqualTo(OrderStatus.DELIVERED);
    }

    @Test
    @DisplayName("배달 완료 시점에 주문정보가 없으면 안된다.")
    void completeDeliveryOrderNotFoundTest() {
        final Order order = createOrder();

        AssertionsForClassTypes.assertThatThrownBy(() -> orderService.completeDelivery(order.getId()))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    @DisplayName("배달 완료시점의 주문 상태는 DELIVERING 여야한다.")
    void completeDeliveryOrderStatusTest() {
        final Order order = createOrder(OrderStatus.SERVED);

        orderRepository.save(order);

        AssertionsForClassTypes.assertThatThrownBy(() -> orderService.completeDelivery(order.getId()))
                .isInstanceOf(IllegalStateException.class);
    }

    /*
     *  완료
     */

    @Test
    @DisplayName("주문 완료되었다.")
    void completeTest() {
        final Order order = createOrder(OrderType.DELIVERY, OrderStatus.DELIVERED);

        orderRepository.save(order);

        final Order actual = orderService.complete(order.getId());
        Assertions.assertThat(actual.getStatus()).isEqualTo(OrderStatus.COMPLETED);
    }

    @Test
    @DisplayName("주문 타입이 배달인 경우의 주문 상태가 배달완료일때 완료처리 가능")
    void completeOrderTypeDeliveryTest() {
        final Order order = createOrder(OrderType.DELIVERY);

        orderRepository.save(order);

        AssertionsForClassTypes.assertThatThrownBy(() -> orderService.complete(order.getId()))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("주문 타입이 매장식사 or 포장의 경우 주문 상태가 SERVED(서빙)가 아니라면 Exception ")
    void completeOrderTypeTakeoutOrEatInTest() {
        final Order order = createOrder(OrderType.TAKEOUT);

        orderRepository.save(order);

        AssertionsForClassTypes.assertThatThrownBy(() -> orderService.complete(order.getId()))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("주문 타입이 매장식사인 경우 주문 테이블을 찾아 초기화한다. ")
    void completeOrderTypeEatInInitOrderTableTest() {

        final OrderTable orderTable = createOrderTable(false, 4);
        final Order order = createOrder(OrderType.EAT_IN, OrderStatus.SERVED, orderTable);

        orderTableRepository.save(orderTable);
        orderRepository.save(order);

        final Order actual = orderService.complete(order.getId());

        Assertions.assertThat(actual.getOrderTable().getNumberOfGuests()).isZero();
        Assertions.assertThat(actual.getOrderTable().isEmpty()).isTrue();
    }

    /*
     * 주문 정보 전체 조회
     * */
    @Test
    @DisplayName("주문 정보 전체 조회")
    void orderFindAllTest() {

        orderRepository.save(createOrder());
        orderRepository.save(createOrder());
        orderRepository.save(createOrder());

        final List<Order> all = orderService.findAll();
        Assertions.assertThat(all).hasSize(3);
    }

    private Order createOrder() {
        final Order order = new Order();
        order.setId(UUID.randomUUID());

        return order;
    }

    private Order createOrder(final OrderType orderType) {
        final Order order = createOrder();
        order.setType(orderType);

        return order;
    }

    private Order createOrder(final OrderType orderType, final UUID orderTableUuid) {
        final Order order = createOrder(orderType);
        order.setOrderTableId(orderTableUuid);
        return order;
    }

    private Order createOrder(final OrderType orderType, final OrderStatus orderStatus) {
        final Order order = createOrder(orderType);
        order.setStatus(orderStatus);

        return order;
    }

    private Order createOrder(final OrderType orderType, final UUID orderTableUuid, final List<OrderLineItem> orderLineItems) {
        final Order order = createOrder(orderType, orderTableUuid);
        order.setOrderLineItems(orderLineItems);
        return order;
    }

    private Order createOrder(final OrderType orderType, final OrderStatus orderStatus, final OrderTable orderTable) {
        final Order order = createOrder(orderType, orderStatus);
        order.setOrderTable(orderTable);

        return order;
    }

    private Order createOrder(final OrderType orderType, final OrderStatus orderStatus, final List<OrderLineItem> orderLineItems) {
        final Order order = createOrder(orderType, orderStatus);
        order.setOrderLineItems(orderLineItems);

        return order;
    }


    private Order createOrder(final OrderType orderType, final List<OrderLineItem> orderLineItems) {
        final Order order = new Order();
        order.setType(orderType);
        order.setOrderLineItems(orderLineItems);

        return order;
    }

    private Order createOrder(final OrderStatus orderStatus) {
        final Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setStatus(orderStatus);

        return order;
    }

    private Menu createMenu() {
        final Menu menu = new Menu();
        menu.setId(UUID.randomUUID());

        return menu;
    }

    private Menu createMenu(final boolean display) {
        final Menu menu = createMenu();
        menu.setDisplayed(display);

        return menu;
    }

    private Menu createMenu(boolean display, final BigDecimal price) {
        final Menu menu = createMenu(display);
        menu.setPrice(price);

        return menu;
    }


    private OrderLineItem createOrderLineItem(final UUID uuid, final Menu menu) {
        final OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenuId(uuid);
        orderLineItem.setMenu(menu);

        return orderLineItem;
    }

    private OrderLineItem createOrderLineItem(final UUID uuid, final Menu menu, final int quantity) {
        final OrderLineItem orderLineItem = createOrderLineItem(uuid, menu);
        orderLineItem.setQuantity(quantity);

        return orderLineItem;
    }

    private OrderLineItem createOrderLineItem(final UUID uuid, final Menu menu, final int quantity, final BigDecimal price) {
        final OrderLineItem orderLineItem = createOrderLineItem(uuid, menu, quantity);
        orderLineItem.setPrice(price);

        return orderLineItem;
    }

    private OrderTable createOrderTable() {
        final OrderTable orderTable = new OrderTable();
        orderTable.setId(UUID.randomUUID());

        return orderTable;
    }

    private OrderTable createOrderTable(final boolean isEmpty) {
        final OrderTable orderTable = createOrderTable();
        orderTable.setEmpty(isEmpty);

        return orderTable;
    }

    private OrderTable createOrderTable(final boolean isEmpty, final int numberOfGuest) {
        final OrderTable orderTable = createOrderTable(isEmpty);
        orderTable.setNumberOfGuests(numberOfGuest);

        return orderTable;
    }
}
