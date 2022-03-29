package kitchenpos.application;

import static kitchenpos.application.MenuFixture.뿌링클_세트;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
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

    @BeforeEach
    void setUp() {
        orderService = new OrderService(orderRepository, menuRepository, orderTableRepository, kitchenridersClient);
    }

    @DisplayName("주문 유형이 반드시 있어야 한다.")
    @Test
    void createOrderTypeException() {
        //given
        Order order = new Order();

        //when
        ThrowingCallable actual = () -> orderService.create(order);

        //then
        assertThatThrownBy(actual).isInstanceOf(OrderTypeNotExistException.class);
    }

    @DisplayName("상품없이 주문할 수 없다.")
    @ParameterizedTest(name = "주문 메뉴: [{arguments}]")
    @NullAndEmptySource
    void createOrderHasNoMenuException(List<OrderLineItem> orderLineItems) {
        //given
        Order 상품_없는_주문 = 신규_배달_주문(orderLineItems);

        //when
        ThrowingCallable actual = () -> orderService.create(상품_없는_주문);

        //then
        assertThatThrownBy(actual).isInstanceOf(OrderLineItemNotExistException.class);
    }

    @DisplayName("등록되지 않은 상품은 주문할 수 없다.")
    @Test
    void createOrderInvalidMenuException() {
        //given
        OrderLineItem 등록_되지_않은_상품 = 주문_항목_1개(new Menu());
        Order 등록되지_않은_상품_주문 = 신규_배달_주문(Collections.singletonList(등록_되지_않은_상품));

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
        Menu menu = menuRepository.save(뿌링클_세트);
        OrderLineItem 모자란_수량 = 주문_항목(menu, -1);
        Order 신규_배달_주문 = 신규_주문(orderType, Collections.singletonList(모자란_수량));

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
        Order 진열되지_않은_상품_주문 = 신규_배달_주문(진열되지_않은_메뉴_상품);

        //when
        ThrowingCallable actual = () -> orderService.create(진열되지_않은_상품_주문);

        //then
        assertThatThrownBy(actual).isInstanceOf(OrderDisplayException.class);
    }

    @DisplayName("상품가격과 일치하지 않은 금액으로 주문할 수 없다.")
    @Test
    void createOrderMismatchPriceException() {
        //given
        Menu 뿌링클_세트 = menuRepository.save(뿌링클_세트());

        List<OrderLineItem> 모자란_금액 = Collections.singletonList(주문_항목_1개(뿌링클_세트, 8_000));
        Order 모자란_금액_주문 = 신규_배달_주문(모자란_금액);

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
        List<OrderLineItem> 뿌링클_1개 = 메뉴_뿌링클_1개(뿌링클_세트());
        Order 주소_없는_배달_주문 = 신규_배달_주문(뿌링클_1개, deliveryAddress);

        //when
        ThrowingCallable actual = () -> orderService.create(주소_없는_배달_주문);

        //then
        assertThatThrownBy(actual).isInstanceOf(OrderDeliveryAddressException.class);
    }

    @DisplayName("식탁에 착석하지 않으면 매장 주문을 할 수 없다.")
    @Test
    void createEatInException() {
        //given
        List<OrderLineItem> 뿌링클_1개 = 메뉴_뿌링클_1개(뿌링클_세트());
        Order 착석하지_않고_주문 = 신규_주문(OrderType.EAT_IN, 뿌링클_1개);
        OrderTable orderTable1 = new OrderTable();
        orderTable1.setEmpty(true);
        OrderTable orderTable = orderTableRepository.save(orderTable1);
        착석하지_않고_주문.setOrderTable(orderTable);
        착석하지_않고_주문.setOrderTableId(orderTable.getId());

        //when
        ThrowingCallable actual = () -> orderService.create(착석하지_않고_주문);

        //then
        assertThatThrownBy(actual).isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("매장 식당 주문")
    @Test
    void createOrderEatIn() {
        //given
        List<OrderLineItem> 뿌링클_1개 = 메뉴_뿌링클_1개(뿌링클_세트());
        OrderTable orderTable = orderTableRepository.save(착석한_식탁());

        Order 매장_식사_주문 = 신규_주문(OrderType.EAT_IN, 뿌링클_1개);
        매장_식사_주문.setOrderTableId(orderTable.getId());

        //when
        Order order = orderService.create(매장_식사_주문);

        //then
        assertAll(
            () -> assertThat(order.getStatus()).isEqualTo(OrderStatus.WAITING),
            () -> assertThat(order.getType()).isEqualTo(OrderType.EAT_IN),
            () -> assertThat(order.getOrderTable().getId()).isEqualTo(orderTable.getId())
        );
    }

    @DisplayName("포장 주문 성공")
    @Test
    void createOrderTakeout() {
        //given
        List<OrderLineItem> 뿌링클_1개 = 메뉴_뿌링클_1개(뿌링클_세트());

        Order 포장_식사_주문 = 신규_주문(OrderType.TAKEOUT, 뿌링클_1개);

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
        List<OrderLineItem> 뿌링클_1개 = 메뉴_뿌링클_1개(뿌링클_세트());

        Order 배달_식사_주문 = 신규_배달_주문(뿌링클_1개, "우리집");

        //when
        Order order = orderService.create(배달_식사_주문);

        //then
        assertAll(
            () -> assertThat(order.getStatus()).isEqualTo(OrderStatus.WAITING),
            () -> assertThat(order.getType()).isEqualTo(OrderType.DELIVERY),
            () -> assertThat(order.getDeliveryAddress()).isEqualTo("우리집")
        );
    }

    @DisplayName("대기중인 주문만 수락할 수 있다.")
    @ParameterizedTest(name = "주문 상태: [{arguments}]")
    @EnumSource(value = OrderStatus.class, names = {"WAITING"}, mode = Mode.EXCLUDE)
    void accept(OrderStatus orderStatus) {
        //given
        List<OrderLineItem> 뿌링클_1개 = 메뉴_뿌링클_1개(뿌링클_세트());

        Order 대기중이_아닌_배달_식사_주문 = orderRepository.save(신규_배달_주문(뿌링클_1개, "우리집", orderStatus));

        //when
        ThrowingCallable actual = () -> orderService.accept(대기중이_아닌_배달_식사_주문.getId());

        //then
        assertThatThrownBy(actual).isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("대기 중인 배달 주문만 수락할 수 있다.")
    @Test
    void acceptOrderDelivery() {
        //given
        List<OrderLineItem> 뿌링클_1개 = 메뉴_뿌링클_1개(뿌링클_세트());

        Order 대기중인_배달_식사_주문 = orderRepository.save(신규_배달_주문(뿌링클_1개, "우리집"));

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
        List<OrderLineItem> 뿌링클_1개 = 메뉴_뿌링클_1개(뿌링클_세트());

        Order 대기중인_주문 = orderRepository.save(신규_주문(orderType, 뿌링클_1개, OrderStatus.WAITING));

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
        List<OrderLineItem> 뿌링클_1개 = 메뉴_뿌링클_1개(뿌링클_세트());

        Order 수락되지_않은_주문 = orderRepository.save(신규_주문(OrderType.EAT_IN, 뿌링클_1개, orderStatus));

        //when
        ThrowingCallable actual = () -> orderService.serve(수락되지_않은_주문.getId());

        //then
        assertThatThrownBy(actual).isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("조리 완료")
    @Test
    void serve() {
        //given
        List<OrderLineItem> 뿌링클_1개 = 메뉴_뿌링클_1개(뿌링클_세트());

        Order 수락된_주문 = orderRepository.save(신규_주문(OrderType.EAT_IN, 뿌링클_1개, OrderStatus.ACCEPTED));

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
        List<OrderLineItem> 뿌링클_1개 = 메뉴_뿌링클_1개(뿌링클_세트());

        Order 배달이_아닌_주문 = orderRepository.save(신규_주문(orderType, 뿌링클_1개, OrderStatus.SERVED));

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
        List<OrderLineItem> 뿌링클_1개 = 메뉴_뿌링클_1개(뿌링클_세트());

        Order 조리_완료되지_않은_배달_주문 = orderRepository.save(신규_배달_주문(뿌링클_1개, "우리집", orderStatus));

        //when
        ThrowingCallable actual = () -> orderService.startDelivery(조리_완료되지_않은_배달_주문.getId());

        //then
        assertThatThrownBy(actual).isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("배달 중")
    @Test
    void startDelivery() {
        //given
        List<OrderLineItem> 뿌링클_1개 = 메뉴_뿌링클_1개(뿌링클_세트());

        Order 조리_완료된_배달_주문 = orderRepository.save(신규_배달_주문(뿌링클_1개, "우리집", OrderStatus.SERVED));

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
        List<OrderLineItem> 뿌링클_1개 = 메뉴_뿌링클_1개(뿌링클_세트());

        Order 배달중인_배달_주문 = orderRepository.save(신규_배달_주문(뿌링클_1개, "우리집", orderStatus));

        //when
        ThrowingCallable actual = () -> orderService.completeDelivery(배달중인_배달_주문.getId());

        //then
        assertThatThrownBy(actual).isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("배달 완료")
    @Test
    void completeDelivery() {
        //given
        List<OrderLineItem> 뿌링클_1개 = 메뉴_뿌링클_1개(뿌링클_세트());

        Order 배달중인_배달_주문 = orderRepository.save(신규_배달_주문(뿌링클_1개, "우리집", OrderStatus.DELIVERING));

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
        List<OrderLineItem> 뿌링클_1개 = 메뉴_뿌링클_1개(뿌링클_세트());

        Order 배달완료되지_않은_배달_주문 = orderRepository.save(신규_배달_주문(뿌링클_1개, "우리집", orderStatus));

        //when
        ThrowingCallable actual = () -> orderService.complete(배달완료되지_않은_배달_주문.getId());

        //then
        assertThatThrownBy(actual).isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("배달 주문 완료")
    @Test
    void completeDeliveryOrder() {
        //given
        List<OrderLineItem> 뿌링클_1개 = 메뉴_뿌링클_1개(뿌링클_세트());

        Order 배달완료된_배달_주문 = orderRepository.save(신규_배달_주문(뿌링클_1개, "우리집", OrderStatus.DELIVERED));

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
        List<OrderLineItem> 뿌링클_1개 = 메뉴_뿌링클_1개(뿌링클_세트());

        Order 조리완료되지_않은_포장_주문 = orderRepository.save(신규_주문(OrderType.TAKEOUT, 뿌링클_1개, orderStatus));

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
        List<OrderLineItem> 뿌링클_1개 = 메뉴_뿌링클_1개(뿌링클_세트());

        Order 조리완료되지_않은_매장_주문 = orderRepository.save(신규_주문(OrderType.TAKEOUT, 뿌링클_1개, orderStatus));

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
        List<OrderLineItem> 뿌링클_1개 = 메뉴_뿌링클_1개(뿌링클_세트());

        Order 조리완료된_주문 = orderRepository.save(신규_주문(orderType, 뿌링클_1개, orderStatus));

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
        List<OrderLineItem> 뿌링클_1개 = 메뉴_뿌링클_1개(뿌링클_세트());

        Order 신규_주문 = 신규_주문(OrderType.EAT_IN, 뿌링클_1개, OrderStatus.SERVED);
        OrderTable orderTable = orderTableRepository.save(착석한_식탁());
        신규_주문.setOrderTableId(orderTable.getId());
        신규_주문.setOrderTable(orderTable);

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

    private List<OrderLineItem> 메뉴_뿌링클_1개(Menu menu) {
        Menu 뿌링클_세트 = menuRepository.save(menu);
        return Collections.singletonList(주문_항목_1개(뿌링클_세트));
    }

    @DisplayName("모든 주문 조회")
    @Test
    void findAll() {
        //given
        List<OrderLineItem> 뿌링클_1개 = 메뉴_뿌링클_1개(뿌링클_세트());

        Order 매장_주문 = 신규_주문(OrderType.EAT_IN, 뿌링클_1개);
        Order 포장_주문 = 신규_주문(OrderType.TAKEOUT, 뿌링클_1개);
        Order 배달_주문 = 신규_주문(OrderType.DELIVERY, 뿌링클_1개);

        orderRepository.save(매장_주문);
        orderRepository.save(포장_주문);
        orderRepository.save(배달_주문);

        //when
        List<Order> actual = orderService.findAll();

        //then
        assertThat(actual).hasSize(3);
    }

    private OrderTable 착석한_식탁() {
        OrderTable orderTable = new OrderTable();
        orderTable.setEmpty(false);
        return orderTable;
    }

    private Menu 뿌링클_세트() {
        Menu menu = new Menu();
        menu.setDisplayed(true);
        menu.setPrice(BigDecimal.valueOf(10_000));
        return menu;
    }

    private Order 신규_주문(OrderType orderType, List<OrderLineItem> orderLineItems) {
        return 신규_주문(orderType, orderLineItems, OrderStatus.WAITING);
    }

    private Order 신규_주문(OrderType orderType, List<OrderLineItem> orderLineItems, OrderStatus orderStatus) {
        Order order = new Order();
        order.setType(orderType);
        order.setOrderLineItems(orderLineItems);
        order.setStatus(orderStatus);
        return order;
    }

    private Order 신규_배달_주문(List<OrderLineItem> orderLineItems, String deliveryAddress, OrderStatus orderStatus) {
        Order order = 신규_주문(OrderType.DELIVERY, orderLineItems, orderStatus);
        order.setDeliveryAddress(deliveryAddress);
        return order;
    }

    private Order 신규_배달_주문(List<OrderLineItem> orderLineItems, String deliveryAddress) {
        return 신규_배달_주문(orderLineItems, deliveryAddress, OrderStatus.WAITING);
    }

    private Order 신규_배달_주문(List<OrderLineItem> orderLineItems) {
        return 신규_배달_주문(orderLineItems, "독도");
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

}
