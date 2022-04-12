package kitchenpos.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import kitchenpos.domain.OrderType;
import kitchenpos.domain.exception.OrderDeliveryAddressException;
import kitchenpos.domain.exception.OrderDisplayException;
import kitchenpos.domain.exception.OrderFromEmptyOrderTableException;
import kitchenpos.domain.exception.OrderInvalidQuantityException;
import kitchenpos.domain.exception.OrderLineItemNotExistException;
import kitchenpos.domain.exception.OrderLineItemNotMatchException;
import kitchenpos.domain.exception.OrderLineItemPriceException;
import kitchenpos.domain.exception.OrderTypeNotExistException;
import kitchenpos.infra.KitchenridersClient;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.EnumSource.Mode;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

@DisplayName("주문 관리")
class OrderServiceTest {

    private final OrderRepository orderRepository = new InMemoryOrderRepository();
    private final MenuRepository menuRepository = new InMemoryMenuRepository();
    private final OrderTableRepository orderTableRepository = new InMemoryOrderTableRepository();
    private final KitchenridersClient kitchenridersClient = new KitchenridersClient();

    private OrderService orderService;

    private Menu 맛초킹_세트;
    private Menu 뿌링클_세트;

    @BeforeEach
    void setUp() {
        orderService = new OrderService(orderRepository, menuRepository, orderTableRepository, kitchenridersClient);
        뿌링클_세트 = menuRepository.save(MenuFixture.뿌링클_세트);
        맛초킹_세트 = menuRepository.save(MenuFixture.맛초킹_세트);
    }

    @DisplayName("주문 유형이 반드시 있어야 한다.")
    @Test
    void createOrderTypeException() {
        //given
        Order 주문_유형_없는_주문 = 신규_주문()
            .withType(null)
            .build();

        //when
        ThrowingCallable actual = () -> orderService.create(주문_유형_없는_주문);

        //then
        assertThatThrownBy(actual).isInstanceOf(OrderTypeNotExistException.class);
    }

    @DisplayName("상품없이 주문할 수 없다.")
    @ParameterizedTest(name = "주문 메뉴: [{arguments}]")
    @NullAndEmptySource
    void createOrderHasNoMenuException(List<OrderLineItem> orderLineItems) {
        //given
        Order 상품_없는_주문 = 신규_배달_주문()
            .withOrderLineItems(orderLineItems)
            .build();

        //when
        ThrowingCallable actual = () -> orderService.create(상품_없는_주문);

        //then
        assertThatThrownBy(actual).isInstanceOf(OrderLineItemNotExistException.class);
    }

    @DisplayName("등록되지 않은 상품은 주문할 수 없다.")
    @Test
    void createOrderInvalidMenuException() {
        //given
        Menu 등록되지_않은_메뉴 = new Menu();
        OrderLineItem 등록_되지_않은_상품 = 주문_항목_1개(등록되지_않은_메뉴);

        Order 등록되지_않은_상품_주문 = 신규_배달_주문()
            .withOrderLineItems(Collections.singletonList(등록_되지_않은_상품))
            .build();

        //when
        ThrowingCallable actual = () -> orderService.create(등록되지_않은_상품_주문);

        //then
        assertThatThrownBy(actual).isInstanceOf(OrderLineItemNotMatchException.class);
    }

    @DisplayName("포장 또는 배달 주문인 경우 0개 미만의 수량으로 주문할 수 없다.")
    @ParameterizedTest(name = "메뉴 유형: [{arguments}]")
    @EnumSource(value = OrderType.class, names = {"DELIVERY", "TAKEOUT"})
    void createOrderInvalidQuantityException(OrderType orderType) {
        //given
        OrderLineItem 모자란_수량 = 주문_항목(뿌링클_세트, -1);
        Order 신규_배달_주문 = 신규_주문()
            .withType(orderType)
            .withOrderLineItems(Collections.singletonList(모자란_수량))
            .build();

        //when
        ThrowingCallable actual = () -> orderService.create(신규_배달_주문);

        //then
        assertThatThrownBy(actual).isInstanceOf(OrderInvalidQuantityException.class);
    }

    @DisplayName("진열되지 않은 상품은 주문할 수 없다.")
    @Test
    void createOrderNotDisplayedMenuException() {
        //given
        List<OrderLineItem> 진열되지_않은_메뉴_상품 = 메뉴_뿌링클_1개(new Menu());
        Order 진열되지_않은_상품_주문 = 신규_배달_주문()
            .withOrderLineItems(진열되지_않은_메뉴_상품)
            .build();

        //when
        ThrowingCallable actual = () -> orderService.create(진열되지_않은_상품_주문);

        //then
        assertThatThrownBy(actual).isInstanceOf(OrderDisplayException.class);
    }

    @DisplayName("상품가격과 일치하지 않은 금액으로 주문할 수 없다.")
    @Test
    void createOrderMismatchPriceException() {
        //given
        List<OrderLineItem> 모자란_금액 = Collections.singletonList(주문_항목_1개(뿌링클_세트, 8_000));
        Order 모자란_금액_주문 = 신규_배달_주문()
            .withOrderLineItems(모자란_금액)
            .build();

        //when
        ThrowingCallable actual = () -> orderService.create(모자란_금액_주문);

        //then
        assertThatThrownBy(actual).isInstanceOf(OrderLineItemPriceException.class);
    }

    @DisplayName("배달 주문인 경우 배달 주소가 반드시 있어야 한다.")
    @ParameterizedTest(name = "배달 주소: [{arguments}]")
    @NullAndEmptySource
    void createDeliveryOrderHasNoAddressException(String deliveryAddress) {
        //given
        Order 주소_없는_배달_주문 = 신규_배달_주문()
            .withDeliveryAddress(deliveryAddress)
            .build();

        //when
        ThrowingCallable actual = () -> orderService.create(주소_없는_배달_주문);

        //then
        assertThatThrownBy(actual).isInstanceOf(OrderDeliveryAddressException.class);
    }

    @DisplayName("식탁에 착석하지 않으면 매장 주문을 할 수 없다.")
    @Test
    void createEatInException() {
        //given
        OrderTable 착석하지_않은_식탁 = 착석하지_않은_식탁();
        Order 착석하지_않고_주문 = 신규_매장_주문()
            .withOrderTableId(착석하지_않은_식탁.getId())

            .build();

        //when
        ThrowingCallable actual = () -> orderService.create(착석하지_않고_주문);

        //then
        assertThatThrownBy(actual).isInstanceOf(OrderFromEmptyOrderTableException.class);
    }

    @DisplayName("매장 식당 주문")
    @Test
    void createOrderEatIn() {
        //given
        OrderTable 착석한_식탁 = 착석한_식탁();

        Order 매장_식사_주문 = 신규_매장_주문()
            .withOrderTableId(착석한_식탁.getId())
            .build();

        //when
        Order order = orderService.create(매장_식사_주문);

        //then
        assertAll(
            () -> assertThat(order.getStatus()).isEqualTo(OrderStatus.WAITING),
            () -> assertThat(order.getType()).isEqualTo(OrderType.EAT_IN),
            () -> assertThat(order.getOrderTable().getId()).isEqualTo(착석한_식탁.getId())
        );
    }

    @DisplayName("포장 주문 성공")
    @Test
    void createOrderTakeout() {
        //given
        Order 포장_식사_주문 = 신규_포장_주문().build();

        //when
        Order order = orderService.create(포장_식사_주문);

        //then
        assertAll(
            () -> assertThat(order.getStatus()).isEqualTo(OrderStatus.WAITING),
            () -> assertThat(order.getType()).isEqualTo(OrderType.TAKEOUT)
        );
    }

    @DisplayName("배달 주문 성공")
    @Test
    void createOrderDelivery() {
        //given
        String deliveryAddress = "우리집";

        Order 배달_식사_주문 = 신규_배달_주문()
            .withDeliveryAddress(deliveryAddress)
            .build();

        //when
        Order order = orderService.create(배달_식사_주문);

        //then
        assertAll(
            () -> assertThat(order.getStatus()).isEqualTo(OrderStatus.WAITING),
            () -> assertThat(order.getType()).isEqualTo(OrderType.DELIVERY),
            () -> assertThat(order.getDeliveryAddress()).isEqualTo(deliveryAddress)
        );
    }

    @DisplayName("대기중인 주문만 수락할 수 있다.")
    @ParameterizedTest(name = "주문 상태: [{arguments}]")
    @EnumSource(value = OrderStatus.class, names = {"WAITING"}, mode = Mode.EXCLUDE)
    void accept(OrderStatus orderStatus) {
        //given
        Order 신규_배달_주문 = 신규_배달_주문()
            .withDeliveryAddress("우리집")
            .withStatus(orderStatus)
            .build();

        Order 대기중이_아닌_배달_식사_주문 = orderRepository.save(신규_배달_주문);

        //when
        ThrowingCallable actual = () -> orderService.accept(대기중이_아닌_배달_식사_주문.getId());

        //then
        assertThatThrownBy(actual).isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("대기 중인 배달 주문만 수락할 수 있다.")
    @Test
    void acceptOrderDelivery() {
        //given
        Order 신규_배달_주문 = 신규_배달_주문()
            .withDeliveryAddress("우리집")
            .build();

        Order 대기중인_배달_식사_주문 = orderRepository.save(신규_배달_주문);

        //when
        Order actual = orderService.accept(대기중인_배달_식사_주문.getId());

        //then
        assertThat(actual.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
    }

    @DisplayName("대기 중인 주문만 수락할 수 있다.")
    @ParameterizedTest(name = "주문 유형: [{arguments}]")
    @EnumSource(value = OrderType.class, names = {"EAT_IN", "TAKEOUT"})
    void accept(OrderType orderType) {
        //given
        Order 신규_주문 = 신규_주문()
            .withType(orderType)
            .build();

        Order 대기중인_주문 = orderRepository.save(신규_주문);

        //when
        Order actual = orderService.accept(대기중인_주문.getId());

        //then
        assertThat(actual.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
    }

    @DisplayName("수락된 주문만 조리 완료할 수 있다.")
    @ParameterizedTest(name = "주문 상태: [{arguments}]")
    @EnumSource(value = OrderStatus.class, names = {"ACCEPTED"}, mode = Mode.EXCLUDE)
    void serveException(OrderStatus orderStatus) {
        //given
        Order 신규_주문 = 신규_주문()
            .withStatus(orderStatus)
            .build();

        Order 수락되지_않은_주문 = orderRepository.save(신규_주문);

        //when
        ThrowingCallable actual = () -> orderService.serve(수락되지_않은_주문.getId());

        //then
        assertThatThrownBy(actual).isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("조리 완료")
    @Test
    void serve() {
        //given
        Order 신규_주문 = 신규_주문()
            .withStatus(OrderStatus.ACCEPTED)
            .build();

        Order 수락된_주문 = orderRepository.save(신규_주문);

        //when
        Order actual = orderService.serve(수락된_주문.getId());

        //then
        assertThat(actual.getStatus()).isEqualTo(OrderStatus.SERVED);
    }

    @DisplayName("배달 주문만 배달 시작할 수 있다.")
    @ParameterizedTest(name = "주문 유형: [{arguments}]")
    @EnumSource(value = OrderType.class, names = {"EAT_IN", "TAKEOUT"})
    void startDeliveryOrderTypeException(OrderType orderType) {
        //given
        Order 신규_주문 = 신규_주문()
            .withStatus(OrderStatus.SERVED)
            .withType(orderType)
            .build();

        Order 배달이_아닌_주문 = orderRepository.save(신규_주문);

        //when
        ThrowingCallable actual = () -> orderService.startDelivery(배달이_아닌_주문.getId());

        //then
        assertThatThrownBy(actual).isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("조리완료된 주문만 배달을 시작할 수 있다.")
    @ParameterizedTest(name = "주문 상태: [{arguments}]")
    @EnumSource(value = OrderStatus.class, names = {"WAITING", "ACCEPTED", "DELIVERING", "DELIVERED", "COMPLETED"})
    void startDeliveryOrderStatusException(OrderStatus orderStatus) {
        //given
        Order 배달_주문 = 신규_배달_주문()
            .withStatus(orderStatus)
            .build();

        Order 조리_완료되지_않은_배달_주문 = orderRepository.save(배달_주문);

        //when
        ThrowingCallable actual = () -> orderService.startDelivery(조리_완료되지_않은_배달_주문.getId());

        //then
        assertThatThrownBy(actual).isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("배달 중")
    @Test
    void startDelivery() {
        //given
        Order 배달_주문 = 신규_배달_주문()
            .withStatus(OrderStatus.SERVED)
            .build();

        Order 조리_완료된_배달_주문 = orderRepository.save(배달_주문);

        //when
        Order actual = orderService.startDelivery(조리_완료된_배달_주문.getId());

        //then
        assertThat(actual.getStatus()).isEqualTo(OrderStatus.DELIVERING);
    }

    @DisplayName("배달중인 주문만 가능 배달완료할 수 있다.")
    @ParameterizedTest(name = "주문 상태: [{arguments}]")
    @EnumSource(value = OrderStatus.class, names = {"WAITING", "ACCEPTED", "SERVED", "DELIVERED", "COMPLETED"})
    void completeDeliveryOrderStatusException(OrderStatus orderStatus) {
        //given
        Order 신규_배달_주문 = 신규_배달_주문()
            .withStatus(orderStatus)
            .build();

        Order 배달중인_배달_주문 = orderRepository.save(신규_배달_주문);

        //when
        ThrowingCallable actual = () -> orderService.completeDelivery(배달중인_배달_주문.getId());

        //then
        assertThatThrownBy(actual).isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("배달 완료")
    @Test
    void completeDelivery() {
        //given
        Order 신규_배달_주문 = 신규_배달_주문()
            .withStatus(OrderStatus.DELIVERING)
            .build();

        Order 배달중인_배달_주문 = orderRepository.save(신규_배달_주문);

        //when
        Order actual = orderService.completeDelivery(배달중인_배달_주문.getId());

        //then
        assertThat(actual.getStatus()).isEqualTo(OrderStatus.DELIVERED);
    }

    @DisplayName("배달 완료 주문만 주문 완료할 수 있다.")
    @ParameterizedTest(name = "주문 상태: [{arguments}]")
    @EnumSource(value = OrderStatus.class, names = {"WAITING", "ACCEPTED", "SERVED", "DELIVERING", "COMPLETED"})
    void completeDeliveryStatusException(OrderStatus orderStatus) {
        //given
        Order 신규_배달_주문 = 신규_배달_주문()
            .withStatus(orderStatus)
            .build();

        Order 배달완료되지_않은_배달_주문 = orderRepository.save(신규_배달_주문);

        //when
        ThrowingCallable actual = () -> orderService.complete(배달완료되지_않은_배달_주문.getId());

        //then
        assertThatThrownBy(actual).isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("배달 주문 완료")
    @Test
    void completeDeliveryOrder() {
        //given
        Order 신규_배달_주문 = 신규_배달_주문()
            .withStatus(OrderStatus.DELIVERED)
            .build();

        Order 배달완료된_배달_주문 = orderRepository.save(신규_배달_주문);

        //when
        Order actual = orderService.complete(배달완료된_배달_주문.getId());

        //then
        assertThat(actual.getStatus()).isEqualTo(OrderStatus.COMPLETED);
    }

    @DisplayName("조리완료된 포장주문만 주문완료할 수 있다.")
    @ParameterizedTest(name = "주문 상태: [{arguments}]")
    @EnumSource(value = OrderStatus.class, names = {"WAITING", "ACCEPTED", "DELIVERED", "DELIVERING", "COMPLETED"})
    void completeTakeoutOrderStatusException(OrderStatus orderStatus) {
        //given
        Order 신규_포장_주문 = 신규_포장_주문()
            .withStatus(orderStatus)
            .build();

        Order 조리완료되지_않은_포장_주문 = orderRepository.save(신규_포장_주문);

        //when
        ThrowingCallable actual = () -> orderService.complete(조리완료되지_않은_포장_주문.getId());

        //then
        assertThatThrownBy(actual).isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("조리완료된 매장주문만 주문완료할 수 있다.")
    @ParameterizedTest(name = "주문 상태: [{arguments}]")
    @EnumSource(value = OrderStatus.class, names = {"WAITING", "ACCEPTED", "DELIVERED", "DELIVERING", "COMPLETED"})
    void completeEatInStatusException(OrderStatus orderStatus) {
        //given
        Order 신규_포장_주문 = 신규_포장_주문()
            .withStatus(orderStatus)
            .build();

        Order 조리완료되지_않은_매장_주문 = orderRepository.save(신규_포장_주문);

        //when
        ThrowingCallable actual = () -> orderService.complete(조리완료되지_않은_매장_주문.getId());

        //then
        assertThatThrownBy(actual).isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("주문 완료 - 포장, 배달")
    @ParameterizedTest(name = "주문 유형: [{0}], 주문 상태: [{1}]")
    @MethodSource("completeTakeoutOrDelivery")
    void completeTakeoutOrDeliveryOrder(OrderType orderType, OrderStatus orderStatus) {
        //given
        Order 신규_주문 = 신규_주문()
            .withType(orderType)
            .withStatus(orderStatus)
            .build();

        Order 조리완료된_주문 = orderRepository.save(신규_주문);

        //when
        Order actual = orderService.complete(조리완료된_주문.getId());

        //then
        assertThat(actual.getStatus()).isEqualTo(OrderStatus.COMPLETED);
    }

    private static Stream<Arguments> completeTakeoutOrDelivery() {
        return Stream.of(
            Arguments.of(OrderType.DELIVERY, OrderStatus.DELIVERED),
            Arguments.of(OrderType.TAKEOUT, OrderStatus.SERVED)
        );
    }

    @DisplayName("매장 주문 완료")
    @Test
    void completeEatInOrder() {
        //given
        OrderTable 착석한_식탁 = 착석한_식탁();
        Order 신규_주문 = 신규_매장_주문()
            .withOrderTableId(착석한_식탁.getId())
            .withOrderTable(착석한_식탁)
            .withStatus(OrderStatus.SERVED)
            .build();

        Order 조리완료된_주문 = orderRepository.save(신규_주문);

        //when
        Order actual = orderService.complete(조리완료된_주문.getId());

        //then
        assertAll(
            () -> assertThat(actual.getStatus()).isEqualTo(OrderStatus.COMPLETED),
            () -> assertThat(actual.getOrderTable().isEmpty()).isTrue(),
            () -> assertThat(actual.getOrderTable().getNumberOfGuests()).isZero()
        );
    }

    @DisplayName("모든 주문 조회")
    @Test
    void findAll() {
        //given
        Order 매장_주문 = 신규_매장_주문().build();
        Order 포장_주문 = 신규_포장_주문().build();
        Order 배달_주문 = 신규_배달_주문().build();

        orderRepository.save(매장_주문);
        orderRepository.save(포장_주문);
        orderRepository.save(배달_주문);

        //when
        List<Order> actual = orderService.findAll();

        //then
        assertThat(actual).hasSize(3);
    }


    private OrderTable 착석하지_않은_식탁() {
        OrderTable orderTable = new OrderTable();
        orderTable.setEmpty(true);
        return orderTableRepository.save(orderTable);
    }

    private OrderTable 착석한_식탁() {
        OrderTable orderTable = new OrderTable();
        orderTable.setEmpty(false);
        return orderTableRepository.save(orderTable);
    }

    public static OrderLineItem 주문_항목_1개(Menu menu) {
        return 주문_항목(menu, 1);
    }

    public static OrderLineItem 주문_항목_1개(Menu menu, int price) {
        return 주문_항목(menu, 1, price);
    }

    public static OrderLineItem 주문_항목(Menu menu, int quantity) {
        return 주문_항목(menu, quantity, 10_000);
    }

    public static OrderLineItem 주문_항목(Menu menu, int quantity, int price) {
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenu(menu);
        orderLineItem.setMenuId(menu.getId());
        orderLineItem.setQuantity(quantity);
        orderLineItem.setPrice(BigDecimal.valueOf(price));
        return orderLineItem;
    }

    private List<OrderLineItem> 메뉴_뿌링클_1개(Menu menu) {
        Menu 뿌링클_세트 = menuRepository.save(menu);
        return Collections.singletonList(주문_항목_1개(뿌링클_세트));
    }

    private OrderBuilder 신규_매장_주문() {
        return 신규_주문()
            .withType(OrderType.EAT_IN)
            .withDeliveryAddress(null);
    }

    private OrderBuilder 신규_포장_주문() {
        return 신규_주문()
            .withType(OrderType.TAKEOUT)
            .withOrderTableId(null)
            .withDeliveryAddress(null);
    }

    private OrderBuilder 신규_배달_주문() {
        return 신규_주문()
            .withType(OrderType.DELIVERY)
            .withOrderTableId(null);
    }

    private OrderBuilder 신규_주문() {
        return new OrderBuilder()
            .withOrderDateTime(LocalDateTime.now())
            .withStatus(OrderStatus.WAITING)
            .withOrderLineItems(Arrays.asList(
                주문_항목(뿌링클_세트, 1, 11_000),
                주문_항목(맛초킹_세트, 1, 11_000))
            )
            .withDeliveryAddress("독도")
            .withOrderTableId(UUID.randomUUID());
    }
}
