package kitchenpos.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import kitchenpos.domain.InMemoryMenuGroupRepository;
import kitchenpos.domain.InMemoryMenuRepository;
import kitchenpos.domain.InMemoryOrderRepository;
import kitchenpos.domain.InMemoryOrderTableRepository;
import kitchenpos.domain.InMemoryProductRepository;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import kitchenpos.domain.OrderType;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.infra.FakeKitchenridersClient;
import kitchenpos.infra.KitchenridersClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.EnumSource.Mode;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

public class OrderServiceTest {

    private OrderRepository orderRepository;
    private MenuRepository menuRepository;
    private OrderTableRepository orderTableRepository;
    private KitchenridersClient kitchenridersClient;
    private OrderService orderService;

    private MenuGroupRepository menuGroupRepository;
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        orderRepository = new InMemoryOrderRepository();
        menuRepository = new InMemoryMenuRepository();
        orderTableRepository = new InMemoryOrderTableRepository();
        kitchenridersClient = new FakeKitchenridersClient();
        orderService = new OrderService(
            orderRepository,
            menuRepository,
            orderTableRepository,
            kitchenridersClient
        );

        menuGroupRepository = new InMemoryMenuGroupRepository();
        productRepository = new InMemoryProductRepository();
    }

    @DisplayName("주문을 요청할 수 있다.")
    @ParameterizedTest(name = "{displayName} [{index}] {argumentsWithNames}")
    @EnumSource(value = OrderType.class, names = {"DELIVERY"})
    void create_Delivery(final OrderType type) {
        // given
        final Order expected = createOrderRequest(type);
        expected.setDeliveryAddress("서울시 송파구 위례성대로 2");

        // when
        final Order actual = orderService.create(expected);

        // then
        assertThat(actual).isNotNull();
        assertAll(
            () -> assertThat(actual.getId()).isNotNull(),
            () -> assertThat(actual.getType()).isEqualTo(expected.getType()),
            () -> assertThat(actual.getStatus()).isEqualTo(OrderStatus.WAITING),
            () -> assertThat(actual.getOrderDateTime()).isNotNull(),
            () -> assertThat(actual.getOrderLineItems()).hasSize(
                expected.getOrderLineItems().size()
            ),
            () -> assertThat(actual.getDeliveryAddress()).isEqualTo(expected.getDeliveryAddress()),
            () -> assertThat(actual.getOrderTable()).isNull()
        );
    }

    @DisplayName("주문을 요청할 수 있다.")
    @ParameterizedTest(name = "{displayName} [{index}] {argumentsWithNames}")
    @EnumSource(value = OrderType.class, names = {"TAKEOUT"})
    void create_TakeOut(final OrderType type) {
        // given
        final Order expected = createOrderRequest(type);

        // when
        final Order actual = orderService.create(expected);

        // then
        assertThat(actual).isNotNull();
        assertAll(
            () -> assertThat(actual.getId()).isNotNull(),
            () -> assertThat(actual.getType()).isEqualTo(expected.getType()),
            () -> assertThat(actual.getStatus()).isEqualTo(OrderStatus.WAITING),
            () -> assertThat(actual.getOrderDateTime()).isNotNull(),
            () -> assertThat(actual.getOrderLineItems()).hasSize(
                expected.getOrderLineItems().size()
            ),
            () -> assertThat(actual.getDeliveryAddress()).isNull(),
            () -> assertThat(actual.getOrderTable()).isNull()
        );
    }

    @DisplayName("주문을 요청할 수 있다.")
    @ParameterizedTest(name = "{displayName} [{index}] {argumentsWithNames}")
    @EnumSource(value = OrderType.class, names = {"EAT_IN"})
    void create_EatIn(final OrderType type) {
        // given
        final Order expected = createOrderRequest(type);
        final OrderTable orderTable = createOrderTable();
        expected.setOrderTable(orderTable);
        expected.setOrderTableId(orderTable.getId());

        // when
        final Order actual = orderService.create(expected);

        // then
        assertThat(actual).isNotNull();
        assertAll(
            () -> assertThat(actual.getId()).isNotNull(),
            () -> assertThat(actual.getType()).isEqualTo(expected.getType()),
            () -> assertThat(actual.getStatus()).isEqualTo(OrderStatus.WAITING),
            () -> assertThat(actual.getOrderDateTime()).isNotNull(),
            () -> assertThat(actual.getOrderLineItems()).hasSize(
                expected.getOrderLineItems().size()
            ),
            () -> assertThat(actual.getDeliveryAddress()).isNull(),
            () -> assertThat(actual.getOrderTable().getId()).isEqualTo(
                actual.getOrderTable().getId()
            )
        );
    }

    @DisplayName("주문의 종류가 올바르지 않으면 요청할 수 없다.")
    @ParameterizedTest(name = "{displayName} [{index}] {argumentsWithNames}")
    @NullSource
    void create_InvalidType(final OrderType type) {
        // given
        final Order expected = createOrderRequest(type);

        // when
        // then
        assertThatThrownBy(() -> orderService.create(expected))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("주문 상품이 올바르지 않으면 요청할 수 없다.")
    @ParameterizedTest(name = "{displayName} [{index}] {argumentsWithNames}")
    @NullAndEmptySource
    void create_InvalidOrderLineItems(final List<OrderLineItem> orderLineItems) {
        // given
        final Order expected = createOrderRequest(OrderType.DELIVERY);
        expected.setOrderLineItems(orderLineItems);

        // when
        // then
        assertThatThrownBy(() -> orderService.create(expected))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("주문의 상품이 올바르지 않으면 요청할 수 없다. 동일한 메뉴의 요청은 수량으로 조정한다.")
    @Test
    void create_InvalidOrderLineItems_DuplicateMenu() {
        // given
        final Order expected = createOrderRequest(OrderType.DELIVERY);
        final List<OrderLineItem> orderLineItems = new ArrayList<>(createOrderLineItems());
        orderLineItems.addAll(createOrderLineItems());
        expected.setOrderLineItems(orderLineItems);

        // when
        // then
        assertThatThrownBy(() -> orderService.create(expected))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("주문의 상품이 올바르지 않으면 요청할 수 없다. 배달 주문 상품의 수량이 0보다 작을 수 없다.")
    @ParameterizedTest(name = "{displayName} [{index}] {argumentsWithNames}")
    @ValueSource(longs = -1)
    void create_InvalidOrderLineItems_InvalidQuantity(final long quantity) {
        // given
        final Order expected = createOrderRequest(OrderType.DELIVERY);
        expected.getOrderLineItems().get(0).setQuantity(quantity);

        // when
        // then
        assertThatThrownBy(() -> orderService.create(expected))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("주문의 상품이 올바르지 않으면 요청할 수 없다. 포장 주문 상품의 수량이 0보다 작을 수 없다.")
    @ParameterizedTest(name = "{displayName} [{index}] {argumentsWithNames}")
    @ValueSource(longs = -1)
    void create_InvalidOrderLineItems_InvalidQuantity2(final long quantity) {
        // given
        final Order expected = createOrderRequest(OrderType.TAKEOUT);
        expected.getOrderLineItems().get(0).setQuantity(quantity);

        // when
        // then
        assertThatThrownBy(() -> orderService.create(expected))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("등록되지 않은 메뉴를 주문 요청할 수 없다.")
    @ParameterizedTest(name = "{displayName} [{index}] {argumentsWithNames}")
    @ValueSource(strings = "00000000-000-0000-0000-000000000000")
    @NullSource
    void create_InvalidOrderLineItems_UnregisteredMenu(UUID id) {
        // Unreachable code
    }

    @DisplayName("공개되지 않은 메뉴를 주문 요청할 수 없다.")
    @Test
    void create_InvalidOrderLineItems_InvalidMenu() {
        // given
        final Order expected = createOrderRequest(OrderType.DELIVERY);
        expected.getOrderLineItems().get(0).getMenu().setDisplayed(false);

        // when
        // then
        assertThatThrownBy(() -> orderService.create(expected))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("주문 가격이 올바르지 않으면 요청할 수 없다. 메뉴의 가격은 주문 상품의 가격과 같아야 한다.")
    @Test
    void create_InvalidOrderLineItems_InvalidMenu2() {
        // given
        final Order expected = createOrderRequest(OrderType.DELIVERY);
        expected.getOrderLineItems().get(0).getMenu().setPrice(BigDecimal.valueOf(18000L));

        // when
        // then
        assertThatThrownBy(() -> orderService.create(expected))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("배달 주문의 경우, 주소가 올바르지 않으면 주문 요청할 수 없다.")
    @ParameterizedTest(name = "{displayName} [{index}] {argumentsWithNames}")
    @NullAndEmptySource
    void create_InvalidDeliveryAddress(final String deliveryAddress) {
        // given
        final Order expected = createOrderRequest(OrderType.DELIVERY);
        expected.setDeliveryAddress(deliveryAddress);

        // when
        // then
        assertThatThrownBy(() -> orderService.create(expected))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("매장 주문의 경우, 등록되지 않은 주문테이블에서 주문 요청할 수 없다.")
    @ParameterizedTest(name = "{displayName} [{index}] {argumentsWithNames}")
    @ValueSource(strings = "00000000-000-0000-0000-000000000000")
    @NullSource
    void create_InvalidOrderTable(final UUID id) {
        // given
        final Order expected = createOrderRequest(OrderType.EAT_IN);
        expected.setOrderTableId(id);

        // when
        // then
        assertThatThrownBy(() -> orderService.create(expected))
            .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("매장 주문의 경우, 회수된 주문테이블에서 주문 요청할 수 없다.")
    @Test
    void create_InvalidOrderTable2() {
        // given
        final Order expected = createOrderRequest(OrderType.EAT_IN);
        final OrderTable orderTable = createOrderTable();
        orderTable.setEmpty(true);
        expected.setOrderTable(orderTable);
        expected.setOrderTableId(orderTable.getId());

        // when
        // then
        assertThatThrownBy(() -> orderService.create(expected))
            .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("주문을 수락할 수 있다.")
    @Test
    void accept() {
        // given
        final Order original = createOrder(OrderType.DELIVERY, OrderStatus.WAITING);

        // when
        final Order actual = orderService.accept(original.getId());

        // then
        assertThat(actual).isNotNull();
        assertAll(
            () -> assertThat(actual.getId()).isEqualTo(original.getId()),
            () -> assertThat(actual.getType()).isEqualTo(original.getType()),
            () -> assertThat(actual.getStatus()).isEqualTo(OrderStatus.ACCEPTED),
            () -> assertThat(actual.getOrderDateTime()).isEqualTo(original.getOrderDateTime()),
            () -> assertThat(actual.getOrderLineItems()).hasSize(
                original.getOrderLineItems().size()
            ),
            () -> assertThat(actual.getDeliveryAddress()).isEqualTo(original.getDeliveryAddress()),
            () -> assertThat(actual.getOrderTable()).isNull()
        );
    }

    @DisplayName("요청되지 않은 주문을 수락할 수 없다.")
    @ParameterizedTest(name = "{displayName} [{index}] {argumentsWithNames}")
    @ValueSource(strings = "00000000-000-0000-0000-000000000000")
    @NullSource
    void accept_UnknownOrder(UUID id) {
        assertThatThrownBy(() -> orderService.accept(id))
            .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("주문이 대기중이 아니면 수락할 수 없다.")
    @ParameterizedTest(name = "{displayName} [{index}] {argumentsWithNames}")
    @EnumSource(value = OrderStatus.class, names = {"WAITING"}, mode = Mode.EXCLUDE)
    void accept_InvalidStatus(final OrderStatus status) {
        // given
        final Order original = createOrder(OrderType.DELIVERY, status);

        // when
        // then
        assertThatThrownBy(() -> orderService.accept(original.getId()))
            .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("주문을 제공할 수 있다.")
    @Test
    void serve() {
        // given
        final Order original = createOrder(OrderType.DELIVERY, OrderStatus.ACCEPTED);

        // when
        final Order actual = orderService.serve(original.getId());

        // then
        assertThat(actual).isNotNull();
        assertAll(
            () -> assertThat(actual.getId()).isEqualTo(original.getId()),
            () -> assertThat(actual.getType()).isEqualTo(original.getType()),
            () -> assertThat(actual.getStatus()).isEqualTo(OrderStatus.SERVED),
            () -> assertThat(actual.getOrderDateTime()).isEqualTo(original.getOrderDateTime()),
            () -> assertThat(actual.getOrderLineItems()).hasSize(
                original.getOrderLineItems().size()
            ),
            () -> assertThat(actual.getDeliveryAddress()).isEqualTo(original.getDeliveryAddress()),
            () -> assertThat(actual.getOrderTable()).isNull()
        );
    }

    @DisplayName("요청되지 않은 주문을 제공할 수 없다.")
    @ParameterizedTest(name = "{displayName} [{index}] {argumentsWithNames}")
    @ValueSource(strings = "00000000-000-0000-0000-000000000000")
    @NullSource
    void serve_UnknownOrder(UUID id) {
        assertThatThrownBy(() -> orderService.accept(id))
            .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("주문의 상태가 '수락됨'이 아니면 제공할 수 없다.")
    @ParameterizedTest(name = "{displayName} [{index}] {argumentsWithNames}")
    @EnumSource(value = OrderStatus.class, names = {"ACCEPTED"}, mode = Mode.EXCLUDE)
    void serve_InvalidStatus(final OrderStatus status) {
        // given
        final Order original = createOrder(OrderType.DELIVERY, status);

        // when
        // then
        assertThatThrownBy(() -> orderService.serve(original.getId()))
            .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("주문 배달을 시작할 수 있다.")
    @Test
    void startDelivery() {
        // given
        final Order original = createOrder(OrderType.DELIVERY, OrderStatus.SERVED);

        // when
        final Order actual = orderService.startDelivery(original.getId());

        // then
        assertThat(actual).isNotNull();
        assertAll(
            () -> assertThat(actual.getId()).isEqualTo(original.getId()),
            () -> assertThat(actual.getType()).isEqualTo(original.getType()),
            () -> assertThat(actual.getStatus()).isEqualTo(OrderStatus.DELIVERING),
            () -> assertThat(actual.getOrderDateTime()).isEqualTo(original.getOrderDateTime()),
            () -> assertThat(actual.getOrderLineItems()).hasSize(
                original.getOrderLineItems().size()
            ),
            () -> assertThat(actual.getDeliveryAddress()).isEqualTo(original.getDeliveryAddress()),
            () -> assertThat(actual.getOrderTable()).isNull()
        );
    }

    @DisplayName("요청되지 않은 주문의 배달을 시작할 수 없다.")
    @ParameterizedTest(name = "{displayName} [{index}] {argumentsWithNames}")
    @ValueSource(strings = "00000000-000-0000-0000-000000000000")
    @NullSource
    void startDelivery_UnknownOrder(UUID id) {
        assertThatThrownBy(() -> orderService.accept(id))
            .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("주문의 종류가 올바르지 않으면 배달할 수 없다. 배달 주문만 배달 가능하다.")
    @ParameterizedTest(name = "{displayName} [{index}] {argumentsWithNames}")
    @EnumSource(value = OrderType.class, names = {"DELIVERY"}, mode = Mode.EXCLUDE)
    void startDelivery_InvalidType(final OrderType type) {
        // given
        final Order original = createOrder(type, OrderStatus.SERVED);

        // when
        assertThatThrownBy(() -> orderService.startDelivery(original.getId()))
            .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("주문의 상태가 올바르지 않으면 배달을 완료할 수 없다. 주문 상태가 '제공됨'일 때 배달을 시작할 수 있다.")
    @ParameterizedTest(name = "{displayName} [{index}] {argumentsWithNames}")
    @EnumSource(value = OrderStatus.class, names = {"SERVED"}, mode = Mode.EXCLUDE)
    void startDelivery_InvalidStatus(final OrderStatus status) {
        // given
        final Order original = createOrder(OrderType.DELIVERY, status);

        // when
        // then
        assertThatThrownBy(() -> orderService.startDelivery(original.getId()))
            .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("주문 배달을 완료할 수 있다.")
    @Test
    void completeDelivery() {
        // given
        final Order original = createOrder(OrderType.DELIVERY, OrderStatus.DELIVERING);

        // when
        final Order actual = orderService.completeDelivery(original.getId());

        // then
        assertThat(actual).isNotNull();
        assertAll(
            () -> assertThat(actual.getId()).isEqualTo(original.getId()),
            () -> assertThat(actual.getType()).isEqualTo(original.getType()),
            () -> assertThat(actual.getStatus()).isEqualTo(OrderStatus.DELIVERED),
            () -> assertThat(actual.getOrderDateTime()).isEqualTo(original.getOrderDateTime()),
            () -> assertThat(actual.getOrderLineItems()).hasSize(
                original.getOrderLineItems().size()
            ),
            () -> assertThat(actual.getDeliveryAddress()).isEqualTo(original.getDeliveryAddress()),
            () -> assertThat(actual.getOrderTable()).isNull()
        );
    }

    @DisplayName("요청되지 않은 주문의 배달을 완료할 수 없다.")
    @ParameterizedTest(name = "{displayName} [{index}] {argumentsWithNames}")
    @ValueSource(strings = "00000000-000-0000-0000-000000000000")
    @NullSource
    void completeDelivery_UnknownOrder(UUID id) {
        assertThatThrownBy(() -> orderService.accept(id))
            .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("주문의 종류가 올바르지 않으면 배달할 수 없다. 배달 주문만 배달 가능하다.")
    @ParameterizedTest(name = "{displayName} [{index}] {argumentsWithNames}")
    @EnumSource(value = OrderType.class, names = {"DELIVERY"}, mode = Mode.EXCLUDE)
    void completeDelivery_InvalidType(final OrderType type) {
        // given
        final Order original = createOrder(type, OrderStatus.SERVED);

        // when
        assertThatThrownBy(() -> orderService.completeDelivery(original.getId()))
            .isInstanceOf(IllegalStateException.class);
    }


    @DisplayName("주문의 상태가 올바르지 않으면 배달을 완료할 수 없다. 주문 상태가 '배달중'일 때 배달을 완료할 수 있다.")
    @ParameterizedTest(name = "{displayName} [{index}] {argumentsWithNames}")
    @EnumSource(value = OrderStatus.class, names = {"DELIVERING"}, mode = Mode.EXCLUDE)
    void completeDelivery_InvalidStatus(final OrderStatus status) {
        // given
        final Order original = createOrder(OrderType.DELIVERY, status);

        // when
        // then
        assertThatThrownBy(() -> orderService.completeDelivery(original.getId()))
            .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("주문을 완료할 수 있다.")
    @ParameterizedTest(name = "{displayName} [{index}] {argumentsWithNames}")
    @EnumSource(value = OrderType.class, names = "DELIVERY")
    void complete_Delivery(final OrderType type) {
        // given
        final Order original = createOrder(type, OrderStatus.DELIVERED);

        // when
        final Order actual = orderService.complete(original.getId());

        // then
        assertThat(actual).isNotNull();
        assertAll(
            () -> assertThat(actual.getId()).isEqualTo(original.getId()),
            () -> assertThat(actual.getType()).isEqualTo(original.getType()),
            () -> assertThat(actual.getStatus()).isEqualTo(OrderStatus.COMPLETED),
            () -> assertThat(actual.getOrderDateTime()).isEqualTo(original.getOrderDateTime()),
            () -> assertThat(actual.getOrderLineItems()).hasSize(
                original.getOrderLineItems().size()
            ),
            () -> assertThat(actual.getDeliveryAddress()).isEqualTo(original.getDeliveryAddress()),
            () -> assertThat(actual.getOrderTable()).isNull()
        );
    }

    @DisplayName("주문을 완료할 수 있다.")
    @ParameterizedTest(name = "{displayName} [{index}] {argumentsWithNames}")
    @EnumSource(value = OrderType.class, names = {"TAKEOUT"})
    void complete_TakeOut(final OrderType type) {
        // given
        final Order original = createOrder(type, OrderStatus.SERVED);

        // when
        final Order actual = orderService.complete(original.getId());

        // then
        assertThat(actual).isNotNull();
        assertAll(
            () -> assertThat(actual.getId()).isEqualTo(original.getId()),
            () -> assertThat(actual.getType()).isEqualTo(original.getType()),
            () -> assertThat(actual.getStatus()).isEqualTo(OrderStatus.COMPLETED),
            () -> assertThat(actual.getOrderDateTime()).isEqualTo(original.getOrderDateTime()),
            () -> assertThat(actual.getOrderLineItems()).hasSize(
                original.getOrderLineItems().size()
            ),
            () -> assertThat(actual.getDeliveryAddress()).isNull(),
            () -> assertThat(actual.getOrderTable()).isNull()
        );
    }

    @DisplayName("주문을 완료할 수 있다.")
    @ParameterizedTest(name = "{displayName} [{index}] {argumentsWithNames}")
    @EnumSource(value = OrderType.class, names = {"EAT_IN"})
    void complete_EatIn(final OrderType type) {
        // given
        final Order original = createOrder(type, OrderStatus.SERVED);
        final OrderTable ordertable = createOrderTable();
        original.setOrderTable(ordertable);

        // when
        final Order actual = orderService.complete(original.getId());

        // then
        assertThat(actual).isNotNull();
        assertAll(
            () -> assertThat(actual.getId()).isEqualTo(original.getId()),
            () -> assertThat(actual.getType()).isEqualTo(original.getType()),
            () -> assertThat(actual.getStatus()).isEqualTo(OrderStatus.COMPLETED),
            () -> assertThat(actual.getOrderDateTime()).isEqualTo(original.getOrderDateTime()),
            () -> assertThat(actual.getOrderLineItems()).hasSize(
                original.getOrderLineItems().size()
            ),
            () -> assertThat(actual.getDeliveryAddress()).isNull()

        );
        assertAll(
            () -> assertThat(actual.getOrderTable().getId()).isEqualTo(
                original.getOrderTable().getId()
            ),
            () -> assertThat(actual.getOrderTable().getName()).isEqualTo(ordertable.getName()),
            () -> assertThat(actual.getOrderTable().getNumberOfGuests()).isEqualTo(0),
            () -> assertThat(actual.getOrderTable().isEmpty()).isTrue()
        );
    }

    @DisplayName("요청되지 않은 주문을 완료할 수 없다.")
    @ParameterizedTest(name = "{displayName} [{index}] {argumentsWithNames}")
    @ValueSource(strings = "00000000-000-0000-0000-000000000000")
    @NullSource
    void complete_UnknownOrder(UUID id) {
        assertThatThrownBy(() -> orderService.accept(id))
            .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("주문의 종류와 상태가 올바르지 않으면 완료할 수 없다. 배달 주문의 상태가 '배달됨'일 때 완료할 수 있다.")
    @ParameterizedTest(name = "{displayName} [{index}] {argumentsWithNames}")
    @EnumSource(value = OrderStatus.class, names = {"DELIVERED"}, mode = Mode.EXCLUDE)
    void complete_InvalidType_Delivery(final OrderStatus status) {
        // given
        final Order original = createOrder(OrderType.DELIVERY, status);

        // when
        // then
        assertThatThrownBy(() -> orderService.complete(original.getId()))
            .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("주문의 종류와 상태가 올바르지 않으면 완료할 수 없다. 포장 주문의 상태가 '제공됨'일 때 완료할 수 있다.")
    @ParameterizedTest(name = "{displayName} [{index}] {argumentsWithNames}")
    @EnumSource(value = OrderStatus.class, names = {"SERVED"}, mode = Mode.EXCLUDE)
    void complete_InvalidType_TakeOut(final OrderStatus status) {
        // given
        final Order original = createOrder(OrderType.TAKEOUT, status);

        // when
        // then
        assertThatThrownBy(() -> orderService.complete(original.getId()))
            .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("주문의 종류와 상태가 올바르지 않으면 완료할 수 없다. 매장 주문의 상태가 '제공됨'일 때 완료할 수 있다.")
    @ParameterizedTest(name = "{displayName} [{index}] {argumentsWithNames}")
    @EnumSource(value = OrderStatus.class, names = {"SERVED"}, mode = Mode.EXCLUDE)
    void complete_InvalidType_EatIn(final OrderStatus status) {
        // given
        final Order original = createOrder(OrderType.EAT_IN, status);

        // when
        // then
        assertThatThrownBy(() -> orderService.complete(original.getId()))
            .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("주문 목록을 조회할 수 있다.")
    @Test
    void findAll() {
        // given
        createOrder(OrderType.DELIVERY, OrderStatus.WAITING);
        createOrder(OrderType.DELIVERY, OrderStatus.COMPLETED);
        createOrder(OrderType.TAKEOUT, OrderStatus.WAITING);
        createOrder(OrderType.TAKEOUT, OrderStatus.COMPLETED);
        createOrder(OrderType.EAT_IN, OrderStatus.WAITING);
        createOrder(OrderType.EAT_IN, OrderStatus.COMPLETED);

        // when
        List<Order> actual = orderService.findAll();

        // then
        assertThat(actual).isNotNull();
        assertThat(actual).hasSize(6);
    }
    // ------------------------------ end findAll ------------------------------

    private Order createOrder(OrderType type, OrderStatus status) {
        final Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setType(type);
        order.setStatus(status);
        order.setOrderLineItems(createOrderLineItems());
        return orderRepository.save(order);
    }

    private Order createOrderRequest(OrderType type) {
        final Order order = new Order();
        order.setType(type);
        order.setOrderLineItems(createOrderLineItems());
        return order;
    }

    List<Menu> createMenus() {
        final MenuGroup menuGroupRequest = new MenuGroup();
        menuGroupRequest.setId(UUID.randomUUID());
        menuGroupRequest.setName("한마리메뉴");
        MenuGroup menuGroup = menuGroupRepository.save(menuGroupRequest);

        final Product product1Request = new Product();
        product1Request.setId(UUID.randomUUID());
        product1Request.setName("후라이드");
        product1Request.setPrice(BigDecimal.valueOf(16000L));
        final Product product1 = productRepository.save(product1Request);

        final Product product2Request = new Product();
        product2Request.setId(UUID.randomUUID());
        product2Request.setName("양념치킨");
        product2Request.setPrice(BigDecimal.valueOf(16000L));
        final Product product2 = productRepository.save(product2Request);

        final MenuProduct menuProductRequest1 = new MenuProduct();
        menuProductRequest1.setSeq(1L);
        menuProductRequest1.setProduct(product1);
        menuProductRequest1.setQuantity(1);
        menuProductRequest1.setProductId(product1.getId());

        final MenuProduct menuProductRequest2 = new MenuProduct();
        menuProductRequest2.setSeq(2L);
        menuProductRequest2.setProduct(product2);
        menuProductRequest2.setQuantity(1);
        menuProductRequest2.setProductId(product2.getId());

        final Menu menuRequest1 = new Menu();
        menuRequest1.setId(UUID.randomUUID());
        menuRequest1.setName("후라이드");
        menuRequest1.setPrice(BigDecimal.valueOf(16000L));
        menuRequest1.setMenuGroup(menuGroup);
        menuRequest1.setDisplayed(true);
        menuRequest1.setMenuProducts(Arrays.asList(menuProductRequest1));
        final Menu menu1 = menuRepository.save(menuRequest1);

        final Menu menuRequest2 = new Menu();
        menuRequest2.setId(UUID.randomUUID());
        menuRequest2.setName("양념치킨");
        menuRequest2.setPrice(BigDecimal.valueOf(16000L));
        menuRequest2.setMenuGroup(menuGroup);
        menuRequest2.setDisplayed(true);
        menuRequest2.setMenuProducts(Arrays.asList(menuProductRequest2));
        final Menu menu2 = menuRepository.save(menuRequest2);

        return Arrays.asList(menu1, menu2);
    }

    OrderTable createOrderTable() {
        final OrderTable orderTableRequest = new OrderTable();
        orderTableRequest.setId(UUID.randomUUID());
        orderTableRequest.setName("1번");
        orderTableRequest.setNumberOfGuests(4);
        orderTableRequest.setEmpty(false);
        return orderTableRepository.save(orderTableRequest);
    }

    List<OrderLineItem> createOrderLineItems() {
        final List<Menu> menus = createMenus();
        final Menu menu1 = menus.get(0);
        final Menu menu2 = menus.get(1);

        OrderLineItem orderLineItem1 = new OrderLineItem();
        orderLineItem1.setSeq(1L);
        orderLineItem1.setMenu(menu1);
        orderLineItem1.setQuantity(1);
        orderLineItem1.setMenuId(menu1.getId());
        orderLineItem1.setPrice(BigDecimal.valueOf(16000L));

        OrderLineItem orderLineItem2 = new OrderLineItem();
        orderLineItem2.setSeq(2L);
        orderLineItem2.setMenu(menu2);
        orderLineItem2.setQuantity(1);
        orderLineItem2.setMenuId(menu2.getId());
        orderLineItem2.setPrice(BigDecimal.valueOf(16000L));

        return Arrays.asList(orderLineItem1, orderLineItem2);
    }
}
