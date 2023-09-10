package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.fixture.*;
import kitchenpos.util.ServiceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

public class OrderServiceTest extends ServiceTest {

    private final ProductRepository productRepository;
    private final OrderTableRepository orderTableRepository;
    private final MenuGroupRepository menuGroupRepository;
    private final MenuRepository menuRepository;
    private final OrderRepository orderRepository;
    private final OrderService orderService;

    private Product product;
    private MenuGroup menuGroup;
    private Menu menu;
    private Menu isDispledMenu;
    private OrderTable orderTable;
    private OrderLineItem orderLineItem;

    public OrderServiceTest(final ProductRepository productRepository,
                            final OrderTableRepository orderTableRepository,
                            final MenuGroupRepository menuGroupRepository,
                            final MenuRepository menuRepository,
                            final OrderRepository orderRepository,
                            final OrderService orderService) {
        this.productRepository = productRepository;
        this.orderTableRepository = orderTableRepository;
        this.menuGroupRepository = menuGroupRepository;
        this.menuRepository = menuRepository;
        this.orderRepository = orderRepository;
        this.orderService = orderService;
    }

    @BeforeEach
    void setup() {
        this.product = productRepository.save(ProductFixure.create("양념치킨", 1000));
        this.menuGroup = menuGroupRepository.save(MenuGroupFixture.create());
        this.menu = menuRepository.save(MenuFixture.create(menuGroup, product));
        this.isDispledMenu = menuRepository.save(MenuFixture.create(menuGroup, product, false));
        this.orderTable = orderTableRepository.save(OrderTableFixture.create(1, true));
        this.orderLineItem = OrderLineFixture.create(menu);
    }


    @DisplayName("주문을 한다.")
    @ParameterizedTest
    @MethodSource("orderTypes")
    void create(OrderType orderType) {
        Order order = OrderFixture.create(orderType, List.of(orderLineItem), orderTable);

        Order creteOrder = orderService.create(order);

        assertThat(order.getType()).isEqualTo(creteOrder.getType());
    }

    private static Stream<OrderType> orderTypes() {
        return Stream.of(OrderType.EAT_IN, OrderType.DELIVERY, OrderType.TAKEOUT);
    }

    @DisplayName("주문타입을 넣어주지 않을 경우 에러를 반환한다.")
    @Test
    void createEmptyNameException() {
        Order order = OrderFixture.create(null, List.of(orderLineItem), orderTable);

        assertThatThrownBy(() -> orderService.create(order))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("주문내역에 아무것도 없으면 에러를 반환한다.")
    @Test
    void createEmptyOrderLineTItemReqest() {
        Order order = OrderFixture.create(OrderType.DELIVERY, Collections.emptyList(), orderTable);

        assertThatThrownBy(() -> orderService.create(order))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("매장 주문이 아닐 경우, 에러를 반환한다.")
    @Test
    void isNegativeQuantityCreate() {
        OrderLineItem orderLineItem = OrderLineFixture.create(menu, -1);
        Order order = OrderFixture.create(OrderType.DELIVERY, List.of(orderLineItem), orderTable);

        assertThatThrownBy(() -> orderService.create(order))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴가 비노출 상태이면, 등록되지 않는다.")
    @Test
    void isDisplayedMenuException() {
        OrderLineItem orderLineItem = OrderLineFixture.create(isDispledMenu, 1);
        Order order = OrderFixture.create(OrderType.DELIVERY, List.of(orderLineItem), orderTable);

        assertThatThrownBy(() -> orderService.create(order))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("메뉴에 등록된 가격과 가격표에 가격이 다르면 에러를 반환한다.")
    @Test
    void isNotEqualsPriceException() {
        OrderLineItem orderLineItem = OrderLineFixture.create(menu, 1, new BigDecimal(100));
        Order order = OrderFixture.create(OrderType.DELIVERY, List.of(orderLineItem), orderTable);

        assertThatThrownBy(() -> orderService.create(order))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("배달 주문일 경우, 배달 주소가 없으면 에러를 반환한다.")
    @Test
    void isEmptyDeliveryAddress() {
        OrderLineItem orderLineItem = OrderLineFixture.create(menu, 1);

        Order order = OrderFixture.create(OrderType.DELIVERY, List.of(orderLineItem), orderTable, null);

        assertThatThrownBy(() -> orderService.create(order))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("매장 식사일 경우, 주문 테이블이 착석되지 않은 상태이면 에러를 반환한다.")
    @Test
    void isOccupiedException() {
        OrderTable orderTable = orderTableRepository.save(OrderTableFixture.create(1, false));
        OrderLineItem orderLineItem = OrderLineFixture.create(menu, 1);

        Order order = OrderFixture.create(OrderType.EAT_IN, List.of(orderLineItem), orderTable);

        assertThatThrownBy(() -> orderService.create(order))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("주문을 받는다.")
    @ParameterizedTest
    @MethodSource("orderTypes")
    void accept(OrderType orderType) {
        Order order = orderRepository.save(OrderFixture.create(orderType, List.of(orderLineItem), orderTable));
        Order createOrder = orderService.accept(order.getId());

        assertThat(createOrder.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
    }


    @DisplayName("주문 대기 상태가 아니면, 에러를 반환한다.")
    @Test
    void isNotWattingStatusException() {
        Order order = orderRepository.save(OrderFixture.create(OrderType.DELIVERY, List.of(orderLineItem), orderTable, null, OrderStatus.ACCEPTED));

        assertThatThrownBy(() -> orderService.accept(order.getId()))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("서빙을 한다.")
    @Test
    void serve() {
        Order order = orderRepository.save(OrderFixture.create(OrderType.EAT_IN, List.of(orderLineItem), orderTable, null, OrderStatus.ACCEPTED));

        Order createOrder = orderService.serve(order.getId());

        assertThat(createOrder.getStatus()).isEqualTo(OrderStatus.SERVED);
    }

    @DisplayName("주문 승인 상태가 아니면, 에러를 반환한다.")
    @Test
    void isNotAcceptedException() {
        Order order = orderRepository.save(OrderFixture.create(OrderType.DELIVERY, List.of(orderLineItem), orderTable, null, OrderStatus.WAITING));

        assertThatThrownBy(() -> orderService.serve(order.getId()))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("배달 시작한다.")
    @Test
    void startDelivery() {
        Order order = orderRepository.save(OrderFixture.create(OrderType.DELIVERY, List.of(orderLineItem), orderTable, null, OrderStatus.SERVED));

        Order deliveryOrder = orderService.startDelivery(order.getId());

        assertThat(deliveryOrder.getStatus()).isEqualTo(OrderStatus.DELIVERING);
    }

    @DisplayName("배달 주문이 아니면, 배달을 할 수 없다.")
    @Test
    void isEqualsDeliveryException() {
        Order order = orderRepository.save(OrderFixture.create(OrderType.EAT_IN, List.of(orderLineItem), orderTable, null, OrderStatus.SERVED));

        assertThatThrownBy(() -> orderService.startDelivery(order.getId()))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("상태가 서빙된 상태가 아니면 배달을 시작할 수 없다.")
    @Test
    void isEqualsServedException() {
        Order order = orderRepository.save(OrderFixture.create(OrderType.DELIVERY, List.of(orderLineItem), orderTable, null, OrderStatus.WAITING));

        assertThatThrownBy(() -> orderService.startDelivery(order.getId()))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("배달이 완료된다.")
    @Test
    void completeDelivery() {
        Order order = orderRepository.save(OrderFixture.create(OrderType.DELIVERY, List.of(orderLineItem), orderTable, null, OrderStatus.DELIVERING));
        Order deliveryOrder = orderService.completeDelivery(order.getId());

        assertThat(deliveryOrder.getStatus()).isEqualTo(OrderStatus.DELIVERED);
    }

    @DisplayName("배달주문이 완료된다.")
    @Test
    void deliveryComplete() {
        Order order = orderRepository.save(OrderFixture.create(OrderType.DELIVERY, List.of(orderLineItem), orderTable, null, OrderStatus.DELIVERED));

        Order deliveryOrder = orderService.complete(order.getId());

        assertThat(deliveryOrder.getStatus()).isEqualTo(OrderStatus.COMPLETED);
    }

    @DisplayName("매장주문시 완료시, 테이블도 정리된다.")
    @Test
    void eaiInComplete() {
        Order order = orderRepository.save(OrderFixture.create(OrderType.EAT_IN, List.of(orderLineItem), orderTable, null, OrderStatus.SERVED));

        Order eaiInOrder = orderService.complete(order.getId());
        OrderTable eaiOrdeTable = orderTableRepository.findById(eaiInOrder.getOrderTable().getId()).get();

        assertAll(
                () -> assertThat(eaiInOrder.getStatus()).isEqualTo(OrderStatus.COMPLETED),
                () -> assertThat(eaiOrdeTable.getNumberOfGuests()).isZero(),
                () -> assertThat(eaiOrdeTable.isOccupied()).isFalse()
        );
    }

}
