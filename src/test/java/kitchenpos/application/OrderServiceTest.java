package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.fixture.OrderFixture;
import kitchenpos.infra.FakeKitchenRidersClient;
import kitchenpos.infra.Kitchenrider;
import kitchenpos.infra.KitchenridersClient;
import kitchenpos.repository.InMemoryMenuRepository;
import kitchenpos.repository.InMemoryOrderRepository;
import kitchenpos.repository.InMemoryOrderTableRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static kitchenpos.fixture.MenuFixture.menu;
import static kitchenpos.fixture.OrderFixture.eatInOrder;
import static kitchenpos.fixture.OrderTableFixture.orderTable;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;

public class OrderServiceTest {

    private OrderService orderService;
    private OrderRepository orderRepository;
    private MenuRepository menuRepository;
    private OrderTableRepository orderTableRepository;
    private KitchenridersClient kitchenridersClient;
    private UUID menuId;
    private UUID hideMenuId;
    private UUID occupiedOrderTableId;
    private UUID notOccupiedOrderTableId;

    @BeforeEach
    void beforeEach() {
        orderRepository = new InMemoryOrderRepository();
        menuRepository = new InMemoryMenuRepository();
        orderTableRepository = new InMemoryOrderTableRepository();
        kitchenridersClient = new FakeKitchenRidersClient();
        orderService = new OrderService(orderRepository, menuRepository, orderTableRepository, kitchenridersClient);
        menuId = menuRepository.save(menu()).getId();
        hideMenuId = menuRepository.save(menu(false)).getId();
        occupiedOrderTableId = orderTableRepository.save(orderTable(true)).getId();
        notOccupiedOrderTableId = orderTableRepository.save(orderTable()).getId();
    }

    @Test
    @DisplayName("주문 목록을 조회할 수 있다")
    void findOrders() {
        // given
        orderRepository.save(OrderFixture.takeOutOrder(OrderStatus.ACCEPTED));

        // when
        final List<Order> result = orderService.findAll();

        // then
        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("주문을 받을 수 있다")
    void acceptOrder() {
        // given
        final UUID orderId = orderRepository.save(OrderFixture.takeOutOrder(OrderStatus.WAITING)).getId();

        // when
        final Order result = orderService.accept(orderId);

        // then
        assertThat(result.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
    }

    @ParameterizedTest
    @NullSource
    @DisplayName("주문이 존재하지 않으면 주문을 받을 수 없다")
    void notAcceptOrderByExistOrder(final UUID input) {
        assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(() ->
                orderService.accept(input)
        );
    }

    @Test
    @DisplayName("주문 상태가 기다리는 중이 아니라면 주문을 받을 수 없다")
    void notAcceptOrderByOrderStatusNotWaiting() {
        // given
        final UUID orderId = orderRepository.save(OrderFixture.takeOutOrder(OrderStatus.COMPLETED)).getId();

        // then
        assertThatIllegalStateException().isThrownBy(() ->
                orderService.accept(orderId)
        );
    }

    @Test
    @DisplayName("배달 주문 수락 시 배달정보(주문 번호, 가격, 주소)를 배달 기사에게 전달한다")
    void acceptDeliveryOrder() {
        // given
        final Order order = orderRepository.save(OrderFixture.deliveryOrder(OrderStatus.WAITING, "집주소"));
        final FakeKitchenRidersClient kitchenridersClient = (FakeKitchenRidersClient) this.kitchenridersClient;

        // when
        orderService.accept(order.getId());
        final Kitchenrider kitchenRider = kitchenridersClient.findKitchenRider(order.getId());

        // then
        assertAll(
                () -> assertThat(kitchenRider.getOrderId()).isEqualTo(order.getId()),
                () -> assertThat(kitchenRider.getAmount()).isEqualTo(getAmount(order)),
                () -> assertThat(kitchenRider.getDeliveryAddress()).isEqualTo(order.getDeliveryAddress())
        );
    }

    private BigDecimal getAmount(final Order order) {
        return order.getOrderLineItems().stream().map(orderLineItem ->
                orderLineItem.getPrice().multiply(BigDecimal.valueOf(orderLineItem.getQuantity()))
        ).reduce(BigDecimal.ZERO, BigDecimal::add);
    }


    @Test
    @DisplayName("배달 주문을 할 수 있다")
    void createDeliveryOrder() {
        // given
        final Order request = createDeliveryOrderRequest();

        // when
        final Order result = orderService.create(request);

        // then
        assertAll(
                () -> assertThat(result.getId()).isNotNull(),
                () -> assertThat(result.getType()).isEqualTo(request.getType()),
                () -> assertThat(result.getStatus()).isEqualTo(OrderStatus.WAITING),
                () -> assertThat(result.getDeliveryAddress()).isEqualTo(request.getDeliveryAddress()),
                () -> assertThat(result.getOrderDateTime()).isNotNull(),
                () -> assertThat(result.getOrderLineItems()).hasSize(1)
        );
    }

    @Test
    @DisplayName("배달 주문을 한 경우 주문한 수량이 0 미만인 경우에는 주문을 할 수 없다")
    void notCreateDeliveryOrder() {
        // given
        final Order request = createDeliveryOrderRequest(-1);

        // then
        assertThatIllegalArgumentException().isThrownBy(() ->
                orderService.create(request)
        );
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("배달지가 빈칸이면 주문을 할 수 없다")
    void notCreateDeliveryOrderByNotDeliveryAddress(final String input) {
        // given
        final Order request = createDeliveryOrderRequest(input);

        // then
        assertThatIllegalArgumentException().isThrownBy(() ->
                orderService.create(request)
        );
    }

    @Test
    @DisplayName("테이크 아웃 주문을 할 수 있다")
    void createTakeOutOrder() {
        // given
        final Order request = createTakeOutOrderRequest();

        // when
        final Order result = orderService.create(request);

        // then
        assertAll(
                () -> assertThat(result.getId()).isNotNull(),
                () -> assertThat(result.getType()).isEqualTo(request.getType()),
                () -> assertThat(result.getStatus()).isEqualTo(OrderStatus.WAITING),
                () -> assertThat(result.getDeliveryAddress()).isEqualTo(request.getDeliveryAddress()),
                () -> assertThat(result.getOrderDateTime()).isNotNull(),
                () -> assertThat(result.getOrderLineItems()).hasSize(1)
        );
    }

    @Test
    @DisplayName("테이크 아웃을 한 경우 주문한 수량이 0 미만인 경우에는 주문을 할 수 없다")
    void notCreateTakeOutOrder() {
        // given
        final Order request = createTakeOutOrderRequest(-1);

        // then
        assertThatIllegalArgumentException().isThrownBy(() ->
                orderService.create(request)
        );
    }

    @Test
    @DisplayName("매장 식사 주문을 할 수 있다")
    void createEatInOrder() {
        // given
        final Order request = createEatInOrderRequest(occupiedOrderTableId);

        // when
        final Order result = orderService.create(request);

        // then
        assertAll(
                () -> assertThat(result.getId()).isNotNull(),
                () -> assertThat(result.getType()).isEqualTo(request.getType()),
                () -> assertThat(result.getStatus()).isEqualTo(OrderStatus.WAITING),
                () -> assertThat(result.getOrderTable().getId()).isEqualTo(request.getOrderTableId()),
                () -> assertThat(result.getOrderDateTime()).isNotNull(),
                () -> assertThat(result.getOrderLineItems()).hasSize(1)
        );
    }

    @ParameterizedTest
    @NullSource
    @DisplayName("매장 식사인 경우 존재하지 않는 테이블에서 주문을 할 수 없다")
    void notCreateEatInOrderByNotExistTable(final UUID input) {
        // given
        final Order request = createEatInOrderRequest(input);

        // then
        assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(() ->
                orderService.create(request)
        );
    }

    @Test
    @DisplayName("테이블이 비어있다면 주문을 할 수 없다")
    void notCreateEatInOrderByNotOccupiedOrderTable() {
        // given
        final Order request = createEatInOrderRequest(notOccupiedOrderTableId);

        // then
        assertThatIllegalStateException().isThrownBy(() ->
                orderService.create(request)
        );
    }

    @ParameterizedTest
    @NullSource
    @DisplayName("주문 타입이 없다면 주문을 할 수 없다")
    void notCreateOrderNotOrderType(final OrderType input) {
        // given
        final Order request = createOrderRequest(input);

        // then
        assertThatIllegalArgumentException().isThrownBy(() ->
                orderService.create(request)
        );
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("주문한 메뉴가 없거나, 비어있다면 주문을 할 수 없다")
    void notCreateOrderNotOrderLineItem(final List<OrderLineItem> input) {
        // given
        final Order request = createOrderRequest(OrderType.TAKEOUT, input);

        // then
        assertThatIllegalArgumentException().isThrownBy(() ->
                orderService.create(request)
        );
    }

    @Test
    @DisplayName("주문한 메뉴가 비공개 되어있다면 주문을 할 수 없다")
    void notCreateOrderByHideMenu() {
        // given
        final Order request = createOrderRequest(
                OrderType.TAKEOUT,
                List.of(createOrderLineRequest(hideMenuId))
        );

        // then
        assertThatIllegalStateException().isThrownBy(() ->
                orderService.create(request)
        );
    }

    @Test
    @DisplayName("주문한 메뉴의 가격과 등록 된 메뉴의 가격이 다른 경우 주문을 할 수 없다")
    void notCreateOrderByMenuMatchedPrice() {
        // given
        final Order request = createOrderRequest(
                OrderType.TAKEOUT,
                List.of(createOrderLineRequest(19_000L))
        );

        // then
        assertThatIllegalArgumentException().isThrownBy(() ->
                orderService.create(request)
        );
    }

    @Test
    @DisplayName("주문한 메뉴와 주문한 메뉴의 목록 갯수와 일치하지 않으면 주문을 할 수 없다")
    void notCreateOrderNotMatchedMenu() {
        // given
        final Order request = createOrderRequest(
                OrderType.TAKEOUT,
                List.of(createOrderLineRequest((UUID) null))
        );

        // then
        assertThatIllegalArgumentException().isThrownBy(() ->
                orderService.create(request)
        );
    }

    @Test
    @DisplayName("주문에 대해 음식을 내어줄 수 있다다")
    void serve() {
        // given
        final UUID orderId = orderRepository.save(OrderFixture.takeOutOrder(OrderStatus.ACCEPTED)).getId();

        // when
        Order result = orderService.serve(orderId);

        // then
        assertAll(
                () -> assertThat(result.getId()).isEqualTo(orderId),
                () -> assertThat(result.getStatus()).isEqualTo(OrderStatus.SERVED)
        );
    }

    @Test
    @DisplayName("주문 상태가 받은 상태가 아니라면 음식을 내어줄 수 없다")
    void notServeByNotOrderStatusAccepted() {
        // given
        final UUID orderId = orderRepository.save(OrderFixture.takeOutOrder(OrderStatus.WAITING)).getId();

        // then
        assertThatIllegalStateException().isThrownBy(
                () -> orderService.serve(orderId)
        );
    }

    @Test
    @DisplayName("배달 주문을 시작을 할 수 있다")
    void startDelivery() {
        // given
        final UUID orderId = orderRepository.save(OrderFixture.deliveryOrder(OrderStatus.SERVED, "집주소")).getId();

        // when
        final Order result = orderService.startDelivery(orderId);

        // then
        assertThat(result.getStatus()).isEqualTo(OrderStatus.DELIVERING);

    }

    @Test
    @DisplayName("주문이 존재하지 않으면 배달 주문을 시작할 수 없다")
    void notStartDeliveryByOrderIsNull() {
        // given
        final UUID orderId = orderRepository.save(OrderFixture.takeOutOrder(OrderStatus.SERVED)).getId();

        // then
        assertThatIllegalStateException().isThrownBy(() ->
                orderService.startDelivery(orderId)
        );
    }

    @Test
    @DisplayName("주문 타입이 배달이 아닌 경우 배달 주문을 시작할 수 없다")
    void notStartDeliveryByOrderTypeIsNotDelievery() {
        // given
        final UUID orderId = orderRepository.save(OrderFixture.takeOutOrder(OrderStatus.SERVED)).getId();

        // then
        assertThatIllegalStateException().isThrownBy(() ->
                orderService.startDelivery(orderId)
        );
    }

    @Test
    @DisplayName("주문 상태가 음식이 제공되지 않은 상태면 배달 시작을 할 수 없다.")
    void notStartDeliveryByOrderStatusNotServed() {
        // given
        final UUID orderId = orderRepository.save(OrderFixture.deliveryOrder(OrderStatus.WAITING, "집주소")).getId();

        // then
        assertThatIllegalStateException().isThrownBy(() ->
                orderService.startDelivery(orderId)
        );
    }

    @Test
    @DisplayName("주문에 대해 배달을 완료할 수 있다")
    void completeDelivery() {
        // given
        final UUID orderId = orderRepository.save(OrderFixture.deliveryOrder(OrderStatus.DELIVERING, "집주소")).getId();

        // when
        final Order result = orderService.completeDelivery(orderId);

        // then
        assertThat(result.getStatus()).isEqualTo(OrderStatus.DELIVERED);
    }

    @ParameterizedTest
    @NullSource
    @DisplayName("주문이 존재하지 않으면 배달을 완료할 수 없다")
    void notCompleteDeliveryByOrderIsNull(final UUID input) {
        // then
        assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(() ->
                orderService.completeDelivery(input)
        );
    }

    @Test
    @DisplayName("주문 상태가 배달 중이 아니라면 배달을 완료할 수 없다")
    void notCompleteDeliveryByOrderTypeIsNotDelivery() {
        // given
        final UUID orderId = orderRepository.save(OrderFixture.deliveryOrder(OrderStatus.WAITING, "집주소")).getId();

        // then
        assertThatIllegalStateException().isThrownBy(() ->
                orderService.completeDelivery(orderId)
        );
    }

    @Test
    @DisplayName("배달 주문을 완료한다")
    void completeByDeliveryOrder() {
        // given
        final UUID orderId = orderRepository.save(OrderFixture.deliveryOrder(OrderStatus.DELIVERED, "집주소")).getId();

        // when
        Order result = orderService.complete(orderId);

        // then
        assertThat(result.getStatus()).isEqualTo(OrderStatus.COMPLETED);
    }

    @Test
    @DisplayName("주문 타입이 배달인 경우, 주문 상태가 배달 완료가 아닌 경우에는 주문을 완료할 수 없다")
    void notCompleteByDeliveryOrderByOrderStatusNotDelivered() {
        // given
        final UUID orderId = orderRepository.save(OrderFixture.deliveryOrder(OrderStatus.WAITING, "집주소")).getId();

        // then
        assertThatIllegalStateException().isThrownBy(
                () -> orderService.complete(orderId)
        );
    }

    @Test
    @DisplayName("테이크 아웃 주문을 완료한다")
    void completeByTakeOutOrder() {
        // given
        final UUID orderId = orderRepository.save(OrderFixture.takeOutOrder(OrderStatus.SERVED)).getId();

        // when
        Order result = orderService.complete(orderId);

        // then
        assertAll(
                () -> assertThat(result.getStatus()).isEqualTo(OrderStatus.COMPLETED),
                () -> assertThat(result.getOrderTable()).isNull()
        );
    }

    @Test
    @DisplayName("테이크 아웃 주문 상태가 음식 제공이 아니라면 주문을 완료할 수 없다")
    void notCompleteByTakeOutOrderByOrderStatusNotServed() {
        // given
        final UUID orderId = orderRepository.save(OrderFixture.takeOutOrder(OrderStatus.WAITING)).getId();

        // then
        assertThatIllegalStateException().isThrownBy(
                () -> orderService.complete(orderId)
        );
    }


    @Test
    @DisplayName("매장식사 주문을 완료한다")
    void completeByTakeInOrder() {
        // given
        final UUID orderId = orderRepository.save(eatInOrder(OrderStatus.SERVED, orderTable())).getId();

        // when
        Order result = orderService.complete(orderId);

        // then
        assertAll(
                () -> assertThat(result.getStatus()).isEqualTo(OrderStatus.COMPLETED),
                () -> assertThat(result.getOrderTable().getNumberOfGuests()).isZero(),
                () -> assertThat(result.getOrderTable().isOccupied()).isFalse()
        );
    }

    @Test
    @DisplayName("매장식사 주문 상태가 음식 제공이 아니라면 주문을 완료할 수 없다")
    void notCompleteByTakeInOrderByOrderStatusNotServed() {
        // given
        final UUID orderId = orderRepository.save(eatInOrder(OrderStatus.WAITING, orderTable())).getId();

        // then
        assertThatIllegalStateException().isThrownBy(
                () -> orderService.complete(orderId)
        );
    }

    @ParameterizedTest
    @NullSource
    @DisplayName("주문이 존재하지 않으면 배달 주문을 시작할 수 없다")
    void notCompleteIsNotOrderId(final UUID input) {
        // then
        assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(() ->
                orderService.complete(input)
        );
    }


    private Order createOrderRequest(final OrderType orderType) {
        return createOrderRequest(orderType, List.of(
                createOrderLineRequest()
        ));
    }

    private Order createOrderRequest(final OrderType orderType, final List<OrderLineItem> orderLineItems) {
        final Order order = new Order();
        order.setType(orderType);
        order.setOrderLineItems(orderLineItems);
        return order;
    }


    private Order createEatInOrderRequest(final UUID orderTableId) {
        final Order order = new Order();
        order.setType(OrderType.EAT_IN);
        order.setOrderLineItems(
                List.of(
                        createOrderLineRequest()
                )
        );
        order.setOrderTableId(orderTableId);
        return order;
    }

    private Order createTakeOutOrderRequest() {
        return createDeliveryOrderRequest(1);
    }

    private Order createTakeOutOrderRequest(final int quantity) {
        final Order order = new Order();
        order.setType(OrderType.TAKEOUT);
        order.setOrderLineItems(List.of(
                createOrderLineRequest(quantity)
        ));
        return order;
    }


    private Order createDeliveryOrderRequest() {
        return createDeliveryOrderRequest(1, "집주소");
    }

    private Order createDeliveryOrderRequest(final int quantity) {
        return createDeliveryOrderRequest(quantity, "집주소");
    }

    private Order createDeliveryOrderRequest(final String deliveryAddress) {
        return createDeliveryOrderRequest(1, deliveryAddress);
    }

    private Order createDeliveryOrderRequest(final int quantity, final String deliveryAddress) {
        final Order order = new Order();
        order.setType(OrderType.DELIVERY);
        order.setOrderLineItems(List.of(
                createOrderLineRequest(quantity)
        ));
        order.setDeliveryAddress(deliveryAddress);
        return order;
    }

    private OrderLineItem createOrderLineRequest() {
        return createOrderLineRequest(1, menuId, 20_000L);
    }

    private OrderLineItem createOrderLineRequest(final UUID menuId) {
        return createOrderLineRequest(1, menuId, 20_000L);
    }

    private OrderLineItem createOrderLineRequest(final Long price) {
        return createOrderLineRequest(1, menuId, price);
    }

    private OrderLineItem createOrderLineRequest(final int quantity) {
        return createOrderLineRequest(quantity, menuId, 20_000L);
    }

    private OrderLineItem createOrderLineRequest(final int quantity, final UUID menuId, final Long price) {
        final OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setQuantity(quantity);
        orderLineItem.setPrice(BigDecimal.valueOf(price));
        orderLineItem.setMenuId(menuId);
        return orderLineItem;
    }
}
