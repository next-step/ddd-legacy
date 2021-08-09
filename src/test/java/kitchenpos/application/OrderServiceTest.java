package kitchenpos.application;

import static kitchenpos.application.fixture.MenuFixture.HIDED_MENU;
import static kitchenpos.application.fixture.MenuFixture.HIDED_MENUS;
import static kitchenpos.application.fixture.MenuFixture.MENU1;
import static kitchenpos.application.fixture.MenuFixture.MENU2;
import static kitchenpos.application.fixture.MenuFixture.MENUS;
import static kitchenpos.application.fixture.OrderFixture.DELIVERY_ORDER_WITH_ADDRESS;
import static kitchenpos.application.fixture.OrderFixture.EAT_IN_NULL_ORDER_TABLE_ORDER;
import static kitchenpos.application.fixture.OrderFixture.EMPTY_ORDER_LINE_ITEMS_ORDER;
import static kitchenpos.application.fixture.OrderFixture.HIDED_MENU_ORDER;
import static kitchenpos.application.fixture.OrderFixture.NEGATIVE_QUANTITY_ORDER_LINE_ITEMS_ORDER;
import static kitchenpos.application.fixture.OrderFixture.NORMAL_ORDER;
import static kitchenpos.application.fixture.OrderFixture.NORMAL_ORDER2;
import static kitchenpos.application.fixture.OrderFixture.NULL_ORDER_LINE_ITEMS_ORDER;
import static kitchenpos.application.fixture.OrderFixture.NULL_TYPE_ORDER;
import static kitchenpos.application.fixture.OrderFixture.ORDERS;
import static kitchenpos.application.fixture.OrderFixture.ORDER_WITH_TYPE_AND_STATUS;
import static kitchenpos.application.fixture.OrderFixture.WRONG_PRICE_MENU_ORDER;
import static kitchenpos.application.fixture.OrderTableFixture.NOT_EMPTY_TABLE;
import static kitchenpos.application.fixture.OrderTableFixture.ORDER_TABLE1;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
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
import org.mockito.InjectMocks;
import org.mockito.Mock;

class OrderServiceTest extends MockTest {

    private static final int ZERO = 0;
    private static final int ONE = 1;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private OrderTableRepository orderTableRepository;

    @Mock
    private KitchenridersClient kitchenridersClient;

    @InjectMocks
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderService = new OrderService(orderRepository, menuRepository, orderTableRepository, kitchenridersClient);
    }

    @DisplayName("create - 주문할 수 있다. 성공시 주문 상태는 대기")
    @Test
    void create() {
        //given
        final Order order = ORDER_WITH_TYPE_AND_STATUS(OrderType.EAT_IN, OrderStatus.WAITING);

        given(menuRepository.findAllById(any())).willReturn(MENUS());
        given(menuRepository.findById(any())).willReturn(Optional.of(MENU1()))
            .willReturn(Optional.of(MENU2()));
        given(orderTableRepository.findById(any())).willReturn(Optional.of(NOT_EMPTY_TABLE()));
        given(orderRepository.save(any())).willReturn(order);

        //when
        final Order sut = orderService.create(order);

        //then
        assertAll(
            () -> assertThat(sut.getStatus()).isEqualTo(OrderStatus.WAITING),
            () -> assertThat(sut.getId()).isEqualTo(order.getId()),
            () -> assertThat(sut.getType()).isEqualTo(OrderType.EAT_IN)
        );

    }

    @DisplayName("create - 주문타입이 없으면 예외를 반환한다")
    @Test
    void createNoType() {
        //given
        final Order order = NULL_TYPE_ORDER();

        //when, then
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> orderService.create(order));
    }

    @DisplayName("create - 주문상품(orderLineItems)이 null이면 예외를 반환한다")
    @Test
    void createNullOrderLineItems() {
        //given
        final Order order = NULL_ORDER_LINE_ITEMS_ORDER();

        //when, then
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> orderService.create(order));
    }

    @DisplayName("create - 주문상품(orderLineItems)이 EmptyList 이면 예외를 반환한다")
    @Test
    void createEmptyOrderLineItems() {
        //given
        final Order order = EMPTY_ORDER_LINE_ITEMS_ORDER();

        //when, then
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> orderService.create(order));
    }

    @DisplayName("create - 주문 타입이 배달, 포장인 경우 주문 상품 수량이 하나라도 음수면 예외를 반환한다")
    @ParameterizedTest
    @EnumSource(value = OrderType.class, names = {"DELIVERY", "TAKEOUT"})
    void createNegativeQuantity(final OrderType orderType) {
        //given
        final Order order = NEGATIVE_QUANTITY_ORDER_LINE_ITEMS_ORDER(orderType);

        given(menuRepository.findAllById(any())).willReturn(MENUS());

        //when, then
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> orderService.create(order));
    }

    @DisplayName("create - 주문 상품의 메뉴가 하나라도 존재하지 않으면 예외를 반환한다")
    @Test
    void createNotExistMenu() {
        //given
        final Order order = ORDER_WITH_TYPE_AND_STATUS(OrderType.DELIVERY, OrderStatus.WAITING);

        given(menuRepository.findAllById(any())).willReturn(MENUS());
        given(menuRepository.findById(any())).willReturn(Optional.of(MENU1()))
            .willReturn(Optional.empty());

        //when, then
        assertThatExceptionOfType(NoSuchElementException.class)
            .isThrownBy(() -> orderService.create(order));
    }

    @DisplayName("create - 주문 상품의 메뉴가 하나라도 노출된 상태가 아니면 예외를 반환한다")
    @Test
    void createHideMenu() {
        //given
        final Order order = HIDED_MENU_ORDER();

        given(menuRepository.findAllById(any())).willReturn(HIDED_MENUS());
        given(menuRepository.findById(any())).willReturn(Optional.of(HIDED_MENU()))
            .willReturn(Optional.of(HIDED_MENU()));

        //when, then
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> orderService.create(order));
    }

    @DisplayName("create - 주문 상품의 메뉴 가격과 실제 메뉴 가격이 같지 않으면 예외를 반환한다")
    @Test
    void createSamePrice() {
        //given
        final Order order = WRONG_PRICE_MENU_ORDER();

        given(menuRepository.findAllById(any())).willReturn(MENUS());
        given(menuRepository.findById(any())).willReturn(Optional.of(MENU1()))
            .willReturn(Optional.of(MENU2()));

        //when, then
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> orderService.create(order));
    }

    @DisplayName("create - 주문 타입이 배달인데 배달 주소가 없을 경우 예외를 반환한다")
    @ParameterizedTest
    @NullAndEmptySource
    void createDeliveryAddress(final String address) {
        //given
        final Order order = DELIVERY_ORDER_WITH_ADDRESS(address);

        given(menuRepository.findAllById(any())).willReturn(MENUS());
        given(menuRepository.findById(any())).willReturn(Optional.of(MENU1()))
            .willReturn(Optional.of(MENU2()));

        //when, then
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> orderService.create(order));
    }

    @DisplayName("create - 주문 타입이 매장 내 식사인경우 주문 테이블이 존재하지 않으면 예외를 반환한다")
    @Test
    void createNotExistTable() {
        //given
        final Order order = EAT_IN_NULL_ORDER_TABLE_ORDER();

        given(menuRepository.findAllById(any())).willReturn(MENUS());
        given(menuRepository.findById(any())).willReturn(Optional.of(MENU1()))
            .willReturn(Optional.of(MENU2()));

        //when, then
        assertThatExceptionOfType(NoSuchElementException.class)
            .isThrownBy(() -> orderService.create(order));
    }

    @DisplayName("create - 주문 타입이 매장 내 식사인경우 주문 테이블에 손님이 앉은 상태가 아니라면 예외를 반환한다")
    @Test
    void createTableStatus() {
        //given
        final Order order = NORMAL_ORDER();

        given(menuRepository.findAllById(any())).willReturn(MENUS());
        given(menuRepository.findById(any())).willReturn(Optional.of(MENU1()))
            .willReturn(Optional.of(MENU2()));
        given(orderTableRepository.findById(any())).willReturn(Optional.of(ORDER_TABLE1()));

        //when, then
        assertThatExceptionOfType(IllegalStateException.class)
            .isThrownBy(() -> orderService.create(order));
    }

    @DisplayName("accept - 주문을 승인할 수 있다. 성공시 주문 상태 승인")
    @Test
    void accept() {
        //given
        final Order order = NORMAL_ORDER();

        given(orderRepository.findById(any())).willReturn(Optional.of(order));

        //when
        final Order sut = orderService.accept(order.getId());

        //then
        assertAll(
            () -> assertThat(sut.getId()).isEqualTo(order.getId()),
            () -> assertThat(sut.getStatus()).isEqualTo(OrderStatus.ACCEPTED)
        );
    }

    @DisplayName("accept - 주문이 존재하지 않으면 예외를 반환한다")
    @Test
    void acceptNotExistOrder() {
        //given
        final Order order = NORMAL_ORDER();

        given(orderRepository.findById(any())).willReturn(Optional.empty());

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

        given(orderRepository.findById(any())).willReturn(Optional.of(order));

        //when, then
        assertThatExceptionOfType(IllegalStateException.class)
            .isThrownBy(() -> orderService.accept(order.getId()));
    }

    @DisplayName("serve - 서빙할 수 있다. 성공시 상태 서빙완료")
    @Test
    void serve() {
        //given
        final Order order = ORDER_WITH_TYPE_AND_STATUS(OrderType.EAT_IN, OrderStatus.ACCEPTED);

        given(orderRepository.findById(any())).willReturn(Optional.of(order));

        //when
        final Order sut = orderService.serve(order.getId());

        //then
        assertAll(
            () -> assertThat(sut.getId()).isEqualTo(order.getId()),
            () -> assertThat(sut.getStatus()).isEqualTo(OrderStatus.SERVED)
        );
    }

    @DisplayName("serve - 주문이 존재하지 않으면 예외를 반환한다")
    @Test
    void serveNotExistOrder() {
        //given
        final Order order = ORDER_WITH_TYPE_AND_STATUS(OrderType.EAT_IN, OrderStatus.ACCEPTED);

        given(orderRepository.findById(any())).willReturn(Optional.empty());

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

        given(orderRepository.findById(any())).willReturn(Optional.of(order));

        //when, then
        assertThatExceptionOfType(IllegalStateException.class)
            .isThrownBy(() -> orderService.serve(order.getId()));
    }

    @DisplayName("startDelivery - 배달을 시작할 수 있다. 성공시 상태 배송중")
    @Test
    void startDelivery() {
        //given
        final Order order = ORDER_WITH_TYPE_AND_STATUS(OrderType.DELIVERY, OrderStatus.SERVED);

        given(orderRepository.findById(any())).willReturn(Optional.of(order));

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

        given(orderRepository.findById(any())).willReturn(Optional.empty());

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

        given(orderRepository.findById(any())).willReturn(Optional.of(order));

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

        given(orderRepository.findById(any())).willReturn(Optional.of(order));

        //when, then
        assertThatExceptionOfType(IllegalStateException.class)
            .isThrownBy(() -> orderService.startDelivery(order.getId()));
    }

    @DisplayName("completeDelivery - 배달을 완료할 수 있다. 성공시 상태 배송완료")
    @Test
    void completeDelivery() {
        //given
        final Order order = ORDER_WITH_TYPE_AND_STATUS(OrderType.DELIVERY, OrderStatus.DELIVERING);

        given(orderRepository.findById(any())).willReturn(Optional.of(order));

        //when
        final Order sut = orderService.completeDelivery(order.getId());

        //then
        assertAll(
            () -> assertThat(sut.getId()).isEqualTo(order.getId()),
            () -> assertThat(sut.getStatus()).isEqualTo(OrderStatus.DELIVERED)
        );
    }

    @DisplayName("completeDelivery - 주문이 존재하지 않으면 예외를 반환한다")
    @Test
    void completeDeliveryNotExistOrder() {
        //given
        final Order order = ORDER_WITH_TYPE_AND_STATUS(OrderType.DELIVERY, OrderStatus.DELIVERING);

        given(orderRepository.findById(any())).willReturn(Optional.empty());

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

        given(orderRepository.findById(any())).willReturn(Optional.of(order));

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

        given(orderRepository.findById(any())).willReturn(Optional.of(order));

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

        given(orderRepository.findById(any())).willReturn(Optional.of(order));

        //when
        final Order sut = orderService.complete(order.getId());

        //then
        assertAll(
            () -> assertThat(sut.getId()).isEqualTo(order.getId()),
            () -> assertThat(sut.getStatus()).isEqualTo(OrderStatus.COMPLETED)
        );
    }

    @DisplayName("complete - 주문 타입이 배달인데 배송완료 상태가 아니라면 예외가 발생한다")
    @ParameterizedTest
    @EnumSource(value = OrderStatus.class, names = {"WAITING", "ACCEPTED", "SERVED", "DELIVERING", "COMPLETED"})
    void completeTypeDeliveryWrongStatus(final OrderStatus orderStatus) {
        //given
        final Order order = ORDER_WITH_TYPE_AND_STATUS(OrderType.DELIVERY, orderStatus);

        given(orderRepository.findById(any())).willReturn(Optional.of(order));

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

        given(orderRepository.findById(any())).willReturn(Optional.of(order));

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

        given(orderRepository.findById(any())).willReturn(Optional.of(order));

        //when, then
        assertThatExceptionOfType(IllegalStateException.class)
            .isThrownBy(() -> orderService.complete(order.getId()));
    }

    @DisplayName("complete - 주문 타입이 매장 내 식사라면 주문 테이블이 치워진 상태여야 한다")
    @Test
    void completeTypeEatInTableShouldEmpty() {
        //given
        final Order order = ORDER_WITH_TYPE_AND_STATUS(OrderType.EAT_IN, OrderStatus.SERVED);

        given(orderRepository.findById(any())).willReturn(Optional.of(order));

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
        given(orderRepository.findAll()).willReturn(ORDERS());

        //when
        final List<Order> sut = orderService.findAll();

        //then
        assertAll(
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
