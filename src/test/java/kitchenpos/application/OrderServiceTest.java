package kitchenpos.application;

import kitchenpos.builder.MenuBuilder;
import kitchenpos.builder.OrderBuilder;
import kitchenpos.builder.OrderLineItemBuilder;
import kitchenpos.builder.OrderTableBuilder;
import kitchenpos.domain.*;
import kitchenpos.infra.KitchenridersClient;
import kitchenpos.mock.MockKitchenridersClient;
import kitchenpos.mock.MockMenuRepository;
import kitchenpos.mock.MockOrderRepository;
import kitchenpos.mock.MockOrderTableRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

class OrderServiceTest {
    private OrderRepository orderRepository;
    private MenuRepository menuRepository;
    private OrderTableRepository orderTableRepository;
    private KitchenridersClient kitchenridersClient;
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        this.orderTableRepository = new MockOrderTableRepository();
        this.orderRepository = new MockOrderRepository(this.orderTableRepository);
        this.menuRepository = new MockMenuRepository();
        this.kitchenridersClient = new MockKitchenridersClient();
        this.orderService = new OrderService(
                this.orderRepository,
                this.menuRepository,
                this.orderTableRepository,
                this.kitchenridersClient
        );
    }

    @DisplayName("유형, 주문한 메뉴 정보(식별자, 가격, 수량) 목록 및 배달 주소 또는 주문 테이블의 식별자로 주문을 추가한 후, " +
            "주문을 대기 상태로, 주문 일시를 현재 시점으로 설정한다")
    @Test
    void create() {
        final Menu menu = this.menuRepository.save(MenuBuilder.aMenu().build());
        final Order expected = OrderBuilder.anOrder()
                .setType(OrderType.DELIVERY)
                .setDeliveryAddress("우리집")
                .setOrderLineItems(OrderLineItemBuilder.anOrderLineItem()
                        .setMenu(menu)
                        .setPrice(16_000L)
                        .setQuantity(1L)
                        .build())
                .build();
        final LocalDateTime beforeOrderTime = LocalDateTime.now();

        final Order actual = this.orderService.create(expected);

        assertAll(
                () -> assertThat(actual.getId()).isNotNull(),
                () -> assertThat(this.orderRepository.findById(actual.getId())).isNotNull(),
                () -> assertThat(actual.getType()).isEqualTo(expected.getType()),
                () -> assertThat(actual.getDeliveryAddress()).isEqualTo(expected.getDeliveryAddress()),
                () -> assertThat(actual.getStatus()).isEqualTo(OrderStatus.WAITING),
                () -> assertThat(actual.getOrderDateTime()).isBetween(beforeOrderTime, LocalDateTime.now())
        );
    }

    @DisplayName("유형은 필수다")
    @ParameterizedTest
    @NullSource
    void createWithoutOrderType(final OrderType orderType) {
        final Order expected = OrderBuilder.anOrder().setType(orderType).build();

        assertThatThrownBy(() -> this.orderService.create(expected))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("주문한 메뉴 정보 목록은 필수다")
    @ParameterizedTest
    @NullSource
    void createWithoutOrderLineItems(final OrderLineItem orderLineItem) {
        final Order expected = OrderBuilder.anOrder().setOrderLineItems((List<OrderLineItem>) orderLineItem).build();

        assertThatThrownBy(() -> this.orderService.create(expected))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("주문한 메뉴 정보 목록에는 하나 이상의 메뉴 정보가 있어야 한다")
    @Test
    void createWithEmptyOrderLineItems() {
        final Order expected = OrderBuilder.anOrder().setOrderLineItems(Collections.emptyList()).build();

        assertThatThrownBy(() -> this.orderService.create(expected))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("주문한 메뉴 정보 목록의 길이와 주문한 메뉴의 식별자 목록으로 조회한 메뉴 목록의 길이는 같아야 한다")
    @Test
    void createWithoutOrderLineItem() {
        final Order expected = OrderBuilder.anOrder()
                .setOrderLineItems(OrderLineItemBuilder.anOrderLineItem().build())
                .build();

        assertThatThrownBy(() -> this.orderService.create(expected))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("유형이 매장내 식사가 아니면 메뉴의 수량은 0 이상이어야 한다")
    @ParameterizedTest
    @ValueSource(longs = -1L)
    void createUnderZeroQuantityWhenOrderTypeIsEatIn(final long quantity) {
        final Menu menu = this.menuRepository.save(MenuBuilder.aMenu().build());
        final Order expected = OrderBuilder.anOrder()
                .setType(OrderType.DELIVERY)
                .setOrderLineItems(OrderLineItemBuilder.anOrderLineItem()
                        .setMenu(menu)
                        .setQuantity(quantity)
                        .build())
                .build();

        assertThatThrownBy(() -> this.orderService.create(expected))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("주문한 메뉴는 공개된 메뉴여야 한다")
    @ParameterizedTest
    @ValueSource(booleans = false)
    void createWithNoDisplayedMenu(final boolean display) {
        final Menu menu = this.menuRepository.save(MenuBuilder.aMenu().setDisplayed(display).build());
        final Order expected = OrderBuilder.anOrder()
                .setOrderLineItems(OrderLineItemBuilder.anOrderLineItem().setMenu(menu).build())
                .build();

        assertThatThrownBy(() -> this.orderService.create(expected))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("주문한 메뉴의 주문 가격은 주문한 메뉴의 식별자로 조회한 메뉴의 가격과 같아야 한다")
    @Test
    void createWithDifferentPrice() {
        final Menu menu = this.menuRepository.save(MenuBuilder.aMenu().setPrice(16_000L).build());
        final Order expected = OrderBuilder.anOrder()
                .setOrderLineItems(OrderLineItemBuilder.anOrderLineItem()
                        .setMenu(menu)
                        .setPrice(17_000L)
                        .build())
                .build();

        assertThatThrownBy(() -> this.orderService.create(expected))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("유형이 배달이면 배달 주소는 필수고, 빈 문자열이 아니어야 한다")
    @ParameterizedTest
    @NullAndEmptySource
    void createWithoutDeliveryAddressWhenOrderTypeIsDelivery(final String deliveryAddress) {
        final Menu menu = this.menuRepository.save(MenuBuilder.aMenu().build());
        final Order expected = OrderBuilder.anOrder()
                .setType(OrderType.DELIVERY)
                .setDeliveryAddress(deliveryAddress)
                .setOrderLineItems(OrderLineItemBuilder.anOrderLineItem().setMenu(menu).build())
                .build();

        assertThatThrownBy(() -> this.orderService.create(expected))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("유형이 매장내 식사면 주문 테이블의 식별자로 특정 주문 테이블을 조회할 수 있어야 한다")
    @Test
    void createWithoutOrderTableWhenOrderTypeIsEatIn() {
        final Menu menu = this.menuRepository.save(MenuBuilder.aMenu().build());
        final Order expected = OrderBuilder.anOrder()
                .setType(OrderType.EAT_IN)
                .setOrderTable(OrderTableBuilder.anOrderTable().build())
                .setOrderLineItems(OrderLineItemBuilder.anOrderLineItem().setMenu(menu).build())
                .build();

        assertThatThrownBy(() -> this.orderService.create(expected))
                .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("유형이 매장내 식사면 주문 테이블의 식별자로 조회한 주문 테이블이 공석이 아니어야 한다")
    @ParameterizedTest
    @ValueSource(booleans = true)
    void createWithEmptyOrderTableWhenOrderTypeIsEatIn(final boolean empty) {
        final Menu menu = this.menuRepository.save(MenuBuilder.aMenu().build());
        final OrderTable orderTable = this.orderTableRepository.save(OrderTableBuilder.anOrderTable()
                .setEmpty(empty)
                .build());
        final Order expected = OrderBuilder.anOrder()
                .setType(OrderType.EAT_IN)
                .setOrderTable(orderTable)
                .setOrderLineItems(OrderLineItemBuilder.anOrderLineItem().setMenu(menu).build())
                .build();

        assertThatThrownBy(() -> this.orderService.create(expected))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("특정 주문의 식별자로 주문을 접수하고, " +
            "주문의 유형이 배달이면 주문의 식별자, 주문한 메뉴의 (가격 * 수량)의 합 및 배달 주소로 배달원을 요청한다")
    @Test
    void accept() {
        final Order expected = this.orderRepository.save(OrderBuilder.anOrder().build());

        final Order actual = this.orderService.accept(expected.getId());

        assertThat(actual.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
    }

    @DisplayName("식별자로 특정 주문을 조회할 수 있어야 한다")
    @ParameterizedTest
    @NullSource
    void accept(final UUID orderId) {
        assertThatThrownBy(() -> this.orderService.accept(orderId))
                .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("주문은 대기 상태여야 한다")
    @Test
    void acceptOrderStatusNotWaiting() {
        final Order expected = this.orderRepository.save(OrderBuilder.anOrder()
                .setStatus(OrderStatus.ACCEPTED)
                .build());

        assertThatThrownBy(() -> this.orderService.accept(expected.getId()))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("특정 주문의 식별자로 주문한 메뉴(제품)를 전달한다")
    @Test
    void serve() {
        final Order expected = this.orderRepository.save(OrderBuilder.anOrder()
                .setStatus(OrderStatus.ACCEPTED)
                .build());

        final Order actual = this.orderService.serve(expected.getId());

        assertThat(actual.getStatus()).isEqualTo(OrderStatus.SERVED);
    }

    @DisplayName("식별자로 특정 주문을 조회할 수 있어야 한다")
    @ParameterizedTest
    @NullSource
    void serve(final UUID orderId) {
        assertThatThrownBy(() -> this.orderService.serve(orderId))
                .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("주문은 접수 상태여야 한다")
    @Test
    void serveOrderStatusNotAccepted() {
        final Order expected = this.orderRepository.save(OrderBuilder.anOrder()
                .setStatus(OrderStatus.WAITING)
                .build());

        assertThatThrownBy(() -> this.orderService.serve(expected.getId()))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("특정 주문의 식별자로 배달 진행을 시작한다")
    @Test
    void startDelivery() {
        final Order expected = this.orderRepository.save(OrderBuilder.anOrder()
                .setType(OrderType.DELIVERY)
                .setStatus(OrderStatus.SERVED)
                .build());

        final Order actual = this.orderService.startDelivery(expected.getId());

        assertThat(actual.getStatus()).isEqualTo(OrderStatus.DELIVERING);
    }

    @DisplayName("식별자로 특정 주문을 조회할 수 있어야 한다")
    @ParameterizedTest
    @NullSource
    void startDelivery(final UUID orderId) {
        assertThatThrownBy(() -> this.orderService.startDelivery(orderId))
                .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("주문의 유형은 배달이어야 한다")
    @Test
    void startDeliveryWithOrderTypeNotDelivery() {
        final Order expected = this.orderRepository.save(OrderBuilder.anOrder()
                .setType(OrderType.EAT_IN)
                .setStatus(OrderStatus.SERVED)
                .build());

        assertThatThrownBy(() -> this.orderService.startDelivery(expected.getId()))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("주문은 전달 상태여야 한다")
    @Test
    void startDeliveryWithOrderStatusNotServed() {
        final Order expected = this.orderRepository.save(OrderBuilder.anOrder()
                .setType(OrderType.DELIVERY)
                .setStatus(OrderStatus.WAITING)
                .build());

        assertThatThrownBy(() -> this.orderService.startDelivery(expected.getId()))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("특정 주문의 식별자로 배달을 완료한다")
    @Test
    void completeDelivery() {
        final Order expected = this.orderRepository.save(OrderBuilder.anOrder()
                .setType(OrderType.DELIVERY)
                .setStatus(OrderStatus.DELIVERING)
                .build());

        final Order actual = this.orderService.completeDelivery(expected.getId());

        assertThat(actual.getStatus()).isEqualTo(OrderStatus.DELIVERED);
    }

    @DisplayName("식별자로 특정 주문을 조회할 수 있어야 한다")
    @ParameterizedTest
    @NullSource
    void completeDelivery(final UUID orderId) {
        assertThatThrownBy(() -> this.orderService.completeDelivery(orderId))
                .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("주문의 유형은 배달이어야 한다")
    @Test
    void completeDeliveryWithOrderTypeNotDelivery() {
        final Order expected = this.orderRepository.save(OrderBuilder.anOrder()
                .setType(OrderType.EAT_IN)
                .setStatus(OrderStatus.SERVED)
                .build());

        assertThatThrownBy(() -> this.orderService.completeDelivery(expected.getId()))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("주문은 배달 진행 상태여야 한다")
    @Test
    void completeDeliveryWithOrderStatusNotDelivering() {
        final Order expected = this.orderRepository.save(OrderBuilder.anOrder()
                .setType(OrderType.DELIVERY)
                .setStatus(OrderStatus.WAITING)
                .build());

        assertThatThrownBy(() -> this.orderService.completeDelivery(expected.getId()))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("특정 주문의 식별자로 주문을 완료한다")
    @Test
    void complete() {
        final Order expected = this.orderRepository.save(OrderBuilder.anOrder()
                .setType(OrderType.DELIVERY)
                .setStatus(OrderStatus.DELIVERED)
                .build());

        final Order actual = this.orderService.complete(expected.getId());

        assertThat(actual.getStatus()).isEqualTo(OrderStatus.COMPLETED);
    }

    @DisplayName("식별자로 특정 주문을 조회할 수 있어야 한다")
    @ParameterizedTest
    @NullSource
    void complete(final UUID orderId) {
        assertThatThrownBy(() -> this.orderService.complete(orderId))
                .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("주문의 유형이 배달이면 주문은 배달 완료 상태여야 한다")
    @Test
    void completeWithOrderStatusNotDeliveredWhenOrderTypeDelivery() {
        final Order expected = this.orderRepository.save(OrderBuilder.anOrder()
                .setType(OrderType.DELIVERY)
                .setStatus(OrderStatus.WAITING)
                .build());

        assertThatThrownBy(() -> this.orderService.complete(expected.getId()))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("주문의 유형이 포장 또는 매장내 식사면 주문은 전달 상태여야 한다")
    @ParameterizedTest
    @MethodSource("provideOrderTypeTakeOutOrEatIn")
    void completeWithOrderStatusNotServedWhenOrderTypeTakeOutOrEatIn(final OrderType orderType) {
        final Order expected = this.orderRepository.save(OrderBuilder.anOrder()
                .setType(orderType)
                .setStatus(OrderStatus.WAITING)
                .build());

        assertThatThrownBy(() -> this.orderService.complete(expected.getId()))
                .isInstanceOf(IllegalStateException.class);
    }

    private static Stream<Arguments> provideOrderTypeTakeOutOrEatIn() {
        return Stream.of(Arguments.of(OrderType.TAKEOUT), Arguments.of(OrderType.EAT_IN));
    }

    @DisplayName("주문의 유형이 매장내 식사면 주문 테이블의 접객 인원을 0으로 바꾼다")
    @Test
    void completeWithOrderTableNumberOfGuestsToZeroWhenOrderTypeEatIn() {
        final OrderTable orderTable = this.orderTableRepository.save(OrderTableBuilder.anOrderTable()
                .setNumberOfGuests(1)
                .setEmpty(false)
                .build());
        final Order expected = this.orderRepository.save(OrderBuilder.anOrder()
                .setType(OrderType.EAT_IN)
                .setStatus(OrderStatus.SERVED)
                .setOrderTable(orderTable)
                .build());

        this.orderService.complete(expected.getId());
        final OrderTable actual = this.orderTableRepository.findById(expected.getOrderTableId()).get();

        assertThat(actual.getNumberOfGuests()).isZero();
    }

    @DisplayName("주문의 유형이 매장내 식사면 주문 테이블을 공석으로 비운다")
    @Test
    void completeWithOrderTableToBeEmptyWhenOrderTypeEatIn() {
        final OrderTable orderTable = this.orderTableRepository.save(OrderTableBuilder.anOrderTable()
                .setNumberOfGuests(1)
                .setEmpty(false)
                .build());
        final Order expected = this.orderRepository.save(OrderBuilder.anOrder()
                .setType(OrderType.EAT_IN)
                .setStatus(OrderStatus.SERVED)
                .setOrderTable(orderTable)
                .build());

        this.orderService.complete(expected.getId());
        final OrderTable actual = this.orderTableRepository.findById(expected.getOrderTableId()).get();

        assertThat(actual.isEmpty()).isTrue();
    }

    @DisplayName("주문 전체 목록을 조회한다")
    @Test
    void findAll() {
        final int expected = 2;

        IntStream.range(0, expected)
                .forEach(index -> this.orderRepository.save(OrderBuilder.anOrder().build()));

        assertThat(this.orderService.findAll()).hasSize(expected);
    }
}
