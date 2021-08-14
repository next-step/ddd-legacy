package kitchenpos.application;

import static kitchenpos.application.fixture.MenuFixture.HIDED_MENU;
import static kitchenpos.application.fixture.MenuFixture.MENU1;
import static kitchenpos.application.fixture.MenuFixture.MENU2;
import static kitchenpos.application.fixture.OrderFixture.DELIVERY_ORDER_WITH_ADDRESS_REQUEST;
import static kitchenpos.application.fixture.OrderFixture.EAT_IN_NULL_ORDER_TABLE_ORDER_REQUEST;
import static kitchenpos.application.fixture.OrderFixture.EMPTY_ORDER_LINE_ITEMS_ORDER_REQUEST;
import static kitchenpos.application.fixture.OrderFixture.HIDED_MENU_ORDER_REQUEST;
import static kitchenpos.application.fixture.OrderFixture.NEGATIVE_QUANTITY_ORDER_LINE_ITEMS_ORDER_REQUEST;
import static kitchenpos.application.fixture.OrderFixture.NORMAL_ORDER;
import static kitchenpos.application.fixture.OrderFixture.NORMAL_ORDER2;
import static kitchenpos.application.fixture.OrderFixture.NORMAL_ORDER_REQUEST;
import static kitchenpos.application.fixture.OrderFixture.NULL_ORDER_LINE_ITEMS_ORDER_REQUEST;
import static kitchenpos.application.fixture.OrderFixture.NULL_TYPE_ORDER_REQUEST;
import static kitchenpos.application.fixture.OrderFixture.ORDERS;
import static kitchenpos.application.fixture.OrderFixture.ORDER_WITH_TYPE_AND_STATUS;
import static kitchenpos.application.fixture.OrderFixture.ORDER_WITH_TYPE_AND_STATUS_REQUEST;
import static kitchenpos.application.fixture.OrderFixture.WRONG_PRICE_MENU_ORDER_REQUEST;
import static kitchenpos.application.fixture.OrderTableFixture.NOT_EMPTY_TABLE;
import static kitchenpos.application.fixture.OrderTableFixture.ORDER_TABLE1;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import java.util.NoSuchElementException;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTableRepository;
import kitchenpos.domain.OrderType;
import kitchenpos.infra.KitchenridersClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

class OrderServiceTest {

    private static final int ZERO = 0;
    private static final int ONE = 1;

    private final OrderRepository orderRepository = new InMemoryOrderRepository();
    private final MenuRepository menuRepository = new InmemoryMenuRepository();
    private final OrderTableRepository orderTableRepository = new InMemoryOrderTableRepository();
    private final KitchenridersClient kitchenridersClient = new KitchenridersClient();

    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderService = new OrderService(orderRepository, menuRepository, orderTableRepository, kitchenridersClient);
    }

    @DisplayName("create - 주문할 수 있다. 성공시 주문 상태는 대기")
    @Test
    void create() {
        //given
        final Order orderRequest = ORDER_WITH_TYPE_AND_STATUS_REQUEST(OrderType.EAT_IN, OrderStatus.WAITING);

        menuRepository.save(MENU1());
        menuRepository.save(MENU2());
        orderTableRepository.save(NOT_EMPTY_TABLE());

        //when
        final Order sut = orderService.create(orderRequest);

        //then
        assertAll(
            () -> assertThat(sut.getId()).isNotNull(),
            () -> assertThat(sut.getStatus()).isEqualTo(OrderStatus.WAITING),
            () -> assertThat(sut.getType()).isEqualTo(OrderType.EAT_IN)
        );
    }

    @DisplayName("create - 주문타입이 없으면 예외를 반환한다")
    @Test
    void createNoType() {
        //given
        final Order orderRequest = NULL_TYPE_ORDER_REQUEST();

        //when, then
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> orderService.create(orderRequest));
    }

    @DisplayName("create - 주문상품(orderLineItems)이 null이면 예외를 반환한다")
    @Test
    void createNullOrderLineItems() {
        //given
        final Order orderRequest = NULL_ORDER_LINE_ITEMS_ORDER_REQUEST();

        //when, then
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> orderService.create(orderRequest));
    }

    @DisplayName("create - 주문상품(orderLineItems)이 EmptyList 이면 예외를 반환한다")
    @Test
    void createEmptyOrderLineItems() {
        //given
        final Order orderRequest = EMPTY_ORDER_LINE_ITEMS_ORDER_REQUEST();

        //when, then
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> orderService.create(orderRequest));
    }

    @DisplayName("create - 주문 타입이 배달, 포장인 경우 주문 상품 수량이 하나라도 음수면 예외를 반환한다")
    @ParameterizedTest
    @EnumSource(value = OrderType.class, names = {"DELIVERY", "TAKEOUT"})
    void createNegativeQuantity(final OrderType orderType) {
        //given
        final Order orderRequest = NEGATIVE_QUANTITY_ORDER_LINE_ITEMS_ORDER_REQUEST(orderType);

        menuRepository.save(MENU1());
        menuRepository.save(MENU2());

        //when, then
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> orderService.create(orderRequest));
    }

    @DisplayName("create - 주문 상품의 메뉴가 하나라도 존재하지 않으면 예외를 반환한다")
    @Test
    void createNotExistMenu() {
        //given
        final Order orderRequest = ORDER_WITH_TYPE_AND_STATUS_REQUEST(OrderType.DELIVERY, OrderStatus.WAITING);

        //when, then
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> orderService.create(orderRequest));
    }

    @DisplayName("create - 주문 상품의 메뉴가 하나라도 노출된 상태가 아니면 예외를 반환한다")
    @Test
    void createHideMenu() {
        //given
        final Order orderRequest = HIDED_MENU_ORDER_REQUEST();

        menuRepository.save(MENU1());
        menuRepository.save(HIDED_MENU());

        //when, then
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> orderService.create(orderRequest));
    }

    @DisplayName("create - 주문 상품의 메뉴 가격과 실제 메뉴 가격이 같지 않으면 예외를 반환한다")
    @Test
    void createSamePrice() {
        //given
        final Order orderRequest = WRONG_PRICE_MENU_ORDER_REQUEST();

        menuRepository.save(MENU1());
        menuRepository.save(MENU2());

        //when, then
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> orderService.create(orderRequest));
    }

    @DisplayName("create - 주문 타입이 배달인데 배달 주소가 없을 경우 예외를 반환한다")
    @ParameterizedTest
    @NullAndEmptySource
    void createDeliveryAddress(final String address) {
        //given
        final Order orderRequest = DELIVERY_ORDER_WITH_ADDRESS_REQUEST(address);

        menuRepository.save(MENU1());
        menuRepository.save(MENU2());

        //when, then
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> orderService.create(orderRequest));
    }

    @DisplayName("create - 주문 타입이 매장 내 식사인경우 주문 테이블이 존재하지 않으면 예외를 반환한다")
    @Test
    void createNotExistTable() {
        //given
        final Order orderRequest = EAT_IN_NULL_ORDER_TABLE_ORDER_REQUEST();

        menuRepository.save(MENU1());
        menuRepository.save(MENU2());

        //when, then
        assertThatExceptionOfType(NoSuchElementException.class)
            .isThrownBy(() -> orderService.create(orderRequest));
    }

    @DisplayName("create - 주문 타입이 매장 내 식사인경우 주문 테이블에 손님이 앉은 상태가 아니라면 예외를 반환한다")
    @Test
    void createTableStatus() {
        //given
        final Order order = NORMAL_ORDER_REQUEST();

        menuRepository.save(MENU1());
        menuRepository.save(MENU2());
        orderTableRepository.save(ORDER_TABLE1());

        //when, then
        assertThatExceptionOfType(IllegalStateException.class)
            .isThrownBy(() -> orderService.create(order));
    }

    @DisplayName("accept - 주문을 승인할 수 있다. 성공시 주문 상태 승인")
    @Test
    void accept() {
        //given
        final Order order = NORMAL_ORDER();

        orderRepository.save(order);

        //when
        final Order sut = orderService.accept(order.getId());

        //then
        assertThat(sut.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
    }

    @DisplayName("accept - 주문이 존재하지 않으면 예외를 반환한다")
    @Test
    void acceptNotExistOrder() {
        //given
        final Order order = NORMAL_ORDER();

        //when, then
        assertThatExceptionOfType(NoSuchElementException.class)
            .isThrownBy(() -> orderService.accept(order.getId()));
    }

    @DisplayName("accept - 주문상태가 대기가 아니면 예외를 반환한다")
    @ParameterizedTest
    @EnumSource(value = OrderStatus.class, names = {"ACCEPTED", "SERVED", "DELIVERING", "DELIVERED", "COMPLETED"})
    void acceptWrongStatus(final OrderStatus orderStatus) {
        //given
        final Order order = ORDER_WITH_TYPE_AND_STATUS(OrderType.EAT_IN, orderStatus);

        orderRepository.save(order);

        //when, then
        assertThatExceptionOfType(IllegalStateException.class)
            .isThrownBy(() -> orderService.accept(order.getId()));
    }

    @DisplayName("serve - 서빙할 수 있다. 성공시 상태 서빙완료")
    @Test
    void serve() {
        //given
        final Order order = ORDER_WITH_TYPE_AND_STATUS(OrderType.EAT_IN, OrderStatus.ACCEPTED);

        orderRepository.save(order);

        //when
        final Order sut = orderService.serve(order.getId());

        //then
        assertThat(sut.getStatus()).isEqualTo(OrderStatus.SERVED);
    }

    @DisplayName("serve - 주문이 존재하지 않으면 예외를 반환한다")
    @Test
    void serveNotExistOrder() {
        //given
        final Order order = ORDER_WITH_TYPE_AND_STATUS(OrderType.EAT_IN, OrderStatus.ACCEPTED);

        //when, then
        assertThatExceptionOfType(NoSuchElementException.class)
            .isThrownBy(() -> orderService.serve(order.getId()));
    }

    @DisplayName("serve - 주문상태가 승인이 아니면 예외를 반환한다")
    @ParameterizedTest
    @EnumSource(value = OrderStatus.class, names = {"WAITING", "SERVED", "DELIVERING", "DELIVERED", "COMPLETED"})
    void serveWrongStatus(final OrderStatus orderStatus) {
        //given
        final Order order = ORDER_WITH_TYPE_AND_STATUS(OrderType.EAT_IN, orderStatus);

        orderRepository.save(order);

        //when, then
        assertThatExceptionOfType(IllegalStateException.class)
            .isThrownBy(() -> orderService.serve(order.getId()));
    }

    @DisplayName("startDelivery - 배달을 시작할 수 있다. 성공시 상태 배송중")
    @Test
    void startDelivery() {
        //given
        final Order order = ORDER_WITH_TYPE_AND_STATUS(OrderType.DELIVERY, OrderStatus.SERVED);

        orderRepository.save(order);

        //when
        final Order sut = orderService.startDelivery(order.getId());

        //then
        assertAll(
            () -> assertThat(sut.getId()).isEqualTo(order.getId()),
            () -> assertThat(sut.getStatus()).isEqualTo(OrderStatus.DELIVERING)
        );
    }

    @DisplayName("startDelivery - 주문이 존재하지 않으면 예외를 반환한다")
    @Test
    void startDeliveryNotExistOrder() {
        //given
        final Order order = ORDER_WITH_TYPE_AND_STATUS(OrderType.DELIVERY, OrderStatus.SERVED);

        //when, then
        assertThatExceptionOfType(NoSuchElementException.class)
            .isThrownBy(() -> orderService.startDelivery(order.getId()));
    }

    @DisplayName("startDelivery - 주문 타입이 배달이 아니면 예외를 반환한다")
    @ParameterizedTest
    @EnumSource(value = OrderType.class, names = {"EAT_IN", "TAKEOUT"})
    void startDeliveryWrongType(final OrderType orderType) {
        //given
        final Order order = ORDER_WITH_TYPE_AND_STATUS(orderType, OrderStatus.SERVED);

        orderRepository.save(order);

        //when, then
        assertThatExceptionOfType(IllegalStateException.class)
            .isThrownBy(() -> orderService.startDelivery(order.getId()));
    }

    @DisplayName("startDelivery - 주문 상태가 서빙완료가 아니면 예외를 반환한다")
    @ParameterizedTest
    @EnumSource(value = OrderStatus.class, names = {"WAITING", "ACCEPTED", "DELIVERING", "DELIVERED", "COMPLETED"})
    void startDeliveryWrongStatus(final OrderStatus orderStatus) {
        //given
        final Order order = ORDER_WITH_TYPE_AND_STATUS(OrderType.DELIVERY, orderStatus);

        orderRepository.save(order);

        //when, then
        assertThatExceptionOfType(IllegalStateException.class)
            .isThrownBy(() -> orderService.startDelivery(order.getId()));
    }

    @DisplayName("completeDelivery - 배달을 완료할 수 있다. 성공시 상태 배송완료")
    @Test
    void completeDelivery() {
        //given
        final Order order = ORDER_WITH_TYPE_AND_STATUS(OrderType.DELIVERY, OrderStatus.DELIVERING);

        orderRepository.save(order);

        //when
        final Order sut = orderService.completeDelivery(order.getId());

        //then
        assertThat(sut.getStatus()).isEqualTo(OrderStatus.DELIVERED);
    }

    @DisplayName("completeDelivery - 주문이 존재하지 않으면 예외를 반환한다")
    @Test
    void completeDeliveryNotExistOrder() {
        //given
        final Order order = ORDER_WITH_TYPE_AND_STATUS(OrderType.DELIVERY, OrderStatus.DELIVERING);

        //when, then
        assertThatExceptionOfType(NoSuchElementException.class)
            .isThrownBy(() -> orderService.completeDelivery(order.getId()));
    }

    @DisplayName("completeDelivery - 주문 타입이 배달이 아니면 예외를 반환한다")
    @ParameterizedTest
    @EnumSource(value = OrderType.class, names = {"EAT_IN", "TAKEOUT"})
    void completeDeliveryWrongType(final OrderType orderType) {
        //given
        final Order order = ORDER_WITH_TYPE_AND_STATUS(orderType, OrderStatus.DELIVERING);

        orderRepository.save(order);

        //when, then
        assertThatExceptionOfType(IllegalStateException.class)
            .isThrownBy(() -> orderService.completeDelivery(order.getId()));
    }

    @DisplayName("completeDelivery - 주문 상태가 배송중이 아니면 예외를 반환한다")
    @ParameterizedTest
    @EnumSource(value = OrderStatus.class, names = {"WAITING", "ACCEPTED", "SERVED", "DELIVERED", "COMPLETED"})
    void completeDeliveryWrongStatus(final OrderStatus orderStatus) {
        //given
        final Order order = ORDER_WITH_TYPE_AND_STATUS(OrderType.DELIVERY, orderStatus);

        orderRepository.save(order);

        //when, then
        assertThatExceptionOfType(IllegalStateException.class)
            .isThrownBy(() -> orderService.completeDelivery(order.getId()));
    }

    @DisplayName("complete - 결제 완료할 수 있다. 성공시 상태 완료")
    @ParameterizedTest
    @CsvSource({"DELIVERY,DELIVERED", "TAKEOUT,SERVED", "EAT_IN,SERVED"})
    void complete(final String typeValue, final String statusValue) {
        //given
        final Order order = ORDER_WITH_TYPE_AND_STATUS(OrderType.valueOf(typeValue), OrderStatus.valueOf(statusValue));

        orderRepository.save(order);

        //when
        final Order sut = orderService.complete(order.getId());

        //then
        assertThat(sut.getStatus()).isEqualTo(OrderStatus.COMPLETED);
    }

    @DisplayName("complete - 주문 타입이 배달인데 배송완료 상태가 아니라면 예외가 발생한다")
    @ParameterizedTest
    @EnumSource(value = OrderStatus.class, names = {"WAITING", "ACCEPTED", "SERVED", "DELIVERING", "COMPLETED"})
    void completeTypeDeliveryWrongStatus(final OrderStatus orderStatus) {
        //given
        final Order order = ORDER_WITH_TYPE_AND_STATUS(OrderType.DELIVERY, orderStatus);

        orderRepository.save(order);

        //when, then
        assertThatExceptionOfType(IllegalStateException.class)
            .isThrownBy(() -> orderService.complete(order.getId()));
    }

    @DisplayName("complete - 주문 타입이 포장인데 서빙완료 상태가 아니라면 예외가 발생한다")
    @ParameterizedTest
    @EnumSource(value = OrderStatus.class, names = {"WAITING", "ACCEPTED", "DELIVERING", "DELIVERED", "COMPLETED"})
    void completeTypeTakeoutWrongStatus(final OrderStatus orderStatus) {
        //given
        final Order order = ORDER_WITH_TYPE_AND_STATUS(OrderType.TAKEOUT, orderStatus);

        orderRepository.save(order);

        //when, then
        assertThatExceptionOfType(IllegalStateException.class)
            .isThrownBy(() -> orderService.complete(order.getId()));
    }

    @DisplayName("complete - 주문 타입이 매장 내 식사인데 서빙완료 상태가 아니라면 예외가 발생한다")
    @ParameterizedTest
    @EnumSource(value = OrderStatus.class, names = {"WAITING", "ACCEPTED", "DELIVERING", "DELIVERED", "COMPLETED"})
    void completeTypeEatInWrongStatus(final OrderStatus orderStatus) {
        //given
        final Order order = ORDER_WITH_TYPE_AND_STATUS(OrderType.EAT_IN, orderStatus);

        orderRepository.save(order);

        //when, then
        assertThatExceptionOfType(IllegalStateException.class)
            .isThrownBy(() -> orderService.complete(order.getId()));
    }

    @DisplayName("complete - 주문 타입이 매장 내 식사라면 주문 테이블이 치워진 상태여야 한다")
    @Test
    void completeTypeEatInTableShouldEmpty() {
        //given
        final Order order = ORDER_WITH_TYPE_AND_STATUS(OrderType.EAT_IN, OrderStatus.SERVED);

        orderRepository.save(order);

        //when
        final Order sut = orderService.complete(order.getId());

        //then
        assertThat(sut.getOrderTable()
            .isEmpty()).isTrue();
    }

    @DisplayName("주문 리스트를 조회할 수 있다")
    @Test
    void findAll() {
        //given
        orderRepository.save(NORMAL_ORDER());
        orderRepository.save(NORMAL_ORDER2());

        //when
        final List<Order> sut = orderService.findAll();

        //then
        assertAll(
            () -> assertThat(sut).hasSize(ORDERS().size()),
            () -> assertThat(sut.get(ZERO)
                .getType()).isEqualTo(NORMAL_ORDER().getType()),
            () -> assertThat(sut.get(ZERO)
                .getStatus()).isEqualTo(NORMAL_ORDER().getStatus()),
            () -> assertThat(sut.get(ONE)
                .getType()).isEqualTo(NORMAL_ORDER2().getType()),
            () -> assertThat(sut.get(ONE)
                .getStatus()).isEqualTo(NORMAL_ORDER2().getStatus())
        );
    }

}
