package kitchenpos.application;

import static kitchenpos.application.fixture.MenuFixture.MENU1;
import static kitchenpos.application.fixture.MenuFixture.MENU2;
import static kitchenpos.application.fixture.MenuFixture.MENUS;
import static kitchenpos.application.fixture.OrderTableFixture.NOT_EMPTY_TABLE;
import static kitchenpos.application.fixture.OrderTableFixture.ORDER_TABLE1;
import static kitchenpos.application.fixture.ProductFixture.PRODUCT1;
import static kitchenpos.application.fixture.ProductFixture.PRODUCT2;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import kitchenpos.domain.Menu;
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
import kitchenpos.infra.KitchenridersClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;

class OrderServiceTest extends MockTest {

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

    private OrderLineItem orderLineItem1;
    private OrderLineItem orderLineItem2;

    private MenuProduct menuProduct1;
    private MenuProduct menuProduct2;

    @BeforeEach
    void setUp() {
        orderService = new OrderService(orderRepository, menuRepository, orderTableRepository, kitchenridersClient);

        orderLineItem1 = createOrderLineItem(MENU1().getPrice()
            .intValue(), 2, MENU1());
        orderLineItem2 = createOrderLineItem(MENU2().getPrice()
            .intValue(), 3, MENU2());

        menuProduct1 = makeMenuProduct(PRODUCT1(), 2L);
        menuProduct2 = makeMenuProduct(PRODUCT2(), 3L);
    }

    @DisplayName("create - 주문할 수 있다. 성공시 주문 상태는 대기")
    @Test
    void create() {
        //given
        final Order order = createOrder(OrderType.EAT_IN, OrderStatus.WAITING);

        given(menuRepository.findAllById(any())).willReturn(MENUS());
        given(menuRepository.findById(any())).willReturn(Optional.of(MENU1()))
            .willReturn(Optional.of(MENU2()));
        given(orderTableRepository.findById(any())).willReturn(Optional.of(NOT_EMPTY_TABLE()));
        given(orderRepository.save(any())).willReturn(order);

        //when
        final Order sut = orderService.create(order);

        //then
        assertThat(sut).isInstanceOf(Order.class);
        assertThat(sut.getStatus()).isEqualTo(OrderStatus.WAITING);
    }

    @DisplayName("create - 주문타입이 없으면 예외를 반환한다")
    @Test
    void createNoType() {
        //given
        final Order order = createOrder(OrderType.EAT_IN, OrderStatus.WAITING);
        order.setType(null);

        //when, then
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> orderService.create(order));
    }

    @DisplayName("create - 주문상품(orderLineItems)이 없으면 예외를 반환한다")
    @Test
    void createNoProduct() {
        //given
        final Order order = createOrder(OrderType.EAT_IN, OrderStatus.WAITING);
        order.setOrderLineItems(null);

        //when, then
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> orderService.create(order));
    }

    @DisplayName("create - 주문 타입이 배달, 포장인 경우 주문 상품 수량이 하나라도 음수면 예외를 반환한다")
    @ParameterizedTest
    @EnumSource(value = OrderType.class, names = {"DELIVERY", "TAKEOUT"})
    void createNegativeQuantity(final OrderType orderType) {
        //given
        final Order order = createOrder(orderType, OrderStatus.WAITING);

        order.getOrderLineItems()
            .get(0)
            .setQuantity(-1);

        given(menuRepository.findAllById(any())).willReturn(MENUS());

        //when, then
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> orderService.create(order));
    }

    @DisplayName("create - 주문 상품의 메뉴가 하나라도 존재하지 않으면 예외를 반환한다")
    @Test
    void createNotExistMenu() {
        //given
        final Order order = createOrder(OrderType.DELIVERY, OrderStatus.WAITING);

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
        final Order order = createOrder(OrderType.DELIVERY, OrderStatus.WAITING);

        given(menuRepository.findAllById(any())).willReturn(MENUS());
        given(menuRepository.findById(any())).willReturn(Optional.of(MENU1()))
            .willReturn(Optional.of(MENU2()));

        order.getOrderLineItems()
            .get(0)
            .getMenu()
            .setDisplayed(false);

        //when, then
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> orderService.create(order));
    }

    @DisplayName("create - 주문 상품의 메뉴 가격과 실제 메뉴 가격이 같지 않으면 예외를 반환한다")
    @Test
    void createSamePrice() {
        //given
        final Order order = createOrder(OrderType.DELIVERY, OrderStatus.WAITING);

        given(menuRepository.findAllById(any())).willReturn(MENUS());
        given(menuRepository.findById(any())).willReturn(Optional.of(MENU1()))
            .willReturn(Optional.of(MENU2()));

        order.getOrderLineItems()
            .get(0)
            .getMenu()
            .setPrice(BigDecimal.valueOf(2000L));

        //when, then
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> orderService.create(order));
    }

    @DisplayName("create - 주문 타입이 배달인데 배달 주소가 없을 경우 예외를 반환한다")
    @Test
    void createDeliveryAddress() {
        //given
        final Order order = createOrder(OrderType.DELIVERY, OrderStatus.WAITING);

        given(menuRepository.findAllById(any())).willReturn(MENUS());
        given(menuRepository.findById(any())).willReturn(Optional.of(MENU1()))
            .willReturn(Optional.of(MENU2()));

        order.setDeliveryAddress(null);

        //when, then
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> orderService.create(order));
    }

    @DisplayName("create - 주문 타입이 매장 내 식사인경우 주문 테이블이 존재하지 않으면 예외를 반환한다")
    @Test
    void createNotExistTable() {
        //given
        final Order order = createOrder(OrderType.EAT_IN, OrderStatus.WAITING);

        given(menuRepository.findAllById(any())).willReturn(MENUS());
        given(menuRepository.findById(any())).willReturn(Optional.of(MENU1()))
            .willReturn(Optional.of(MENU2()));

        order.setOrderTable(null);

        //when, then
        assertThatExceptionOfType(NoSuchElementException.class)
            .isThrownBy(() -> orderService.create(order));
    }

    @DisplayName("create - 주문 타입이 매장 내 식사인경우 주문 테이블에 손님이 앉은 상태가 아니라면 예외를 반환한다")
    @Test
    void createTableStatus() {
        //given
        final Order order = createOrder(OrderType.EAT_IN, OrderStatus.WAITING);

        final OrderTable orderTable = ORDER_TABLE1();

        given(menuRepository.findAllById(any())).willReturn(MENUS());
        given(menuRepository.findById(any())).willReturn(Optional.of(MENU1()))
            .willReturn(Optional.of(MENU2()));
        given(orderTableRepository.findById(any())).willReturn(Optional.of(orderTable));

        //when, then
        assertThatExceptionOfType(IllegalStateException.class)
            .isThrownBy(() -> orderService.create(order));
    }

    @DisplayName("accept - 주문을 승인할 수 있다. 성공시 주문 상태 승인")
    @Test
    void accept() {
        //given
        final Order order = createOrder(OrderType.EAT_IN, OrderStatus.WAITING);

        given(orderRepository.findById(any())).willReturn(Optional.of(order));

        //when
        final Order sut = orderService.accept(order.getId());

        //then
        assertThat(sut.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
    }

    @DisplayName("accept - 주문이 존재하지 않으면 예외를 반환한다")
    @Test
    void acceptNotExistOrder() {
        //given
        final Order order = createOrder(OrderType.EAT_IN, OrderStatus.WAITING);

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
        final Order order = createOrder(OrderType.EAT_IN, orderStatus);

        given(orderRepository.findById(any())).willReturn(Optional.of(order));

        //when, then
        assertThatExceptionOfType(IllegalStateException.class)
            .isThrownBy(() -> orderService.accept(order.getId()));
    }

    @DisplayName("serve - 서빙할 수 있다. 성공시 상태 서빙완료")
    @Test
    void serve() {
        //given
        final Order order = createOrder(OrderType.EAT_IN, OrderStatus.ACCEPTED);

        given(orderRepository.findById(any())).willReturn(Optional.of(order));

        //when
        final Order sut = orderService.serve(order.getId());

        //then
        assertThat(sut.getStatus()).isEqualTo(OrderStatus.SERVED);
    }

    @DisplayName("serve - 주문이 존재하지 않으면 예외를 반환한다")
    @Test
    void serveNotExistOrder() {
        //given
        final Order order = createOrder(OrderType.EAT_IN, OrderStatus.ACCEPTED);

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
        final Order order = createOrder(OrderType.EAT_IN, orderStatus);

        given(orderRepository.findById(any())).willReturn(Optional.of(order));

        //when, then
        assertThatExceptionOfType(IllegalStateException.class)
            .isThrownBy(() -> orderService.serve(order.getId()));
    }

    @DisplayName("startDelivery - 배달을 시작할 수 있다. 성공시 상태 배송중")
    @Test
    void startDelivery() {
        //given
        final Order order = createOrder(OrderType.DELIVERY, OrderStatus.SERVED);

        given(orderRepository.findById(any())).willReturn(Optional.of(order));

        //when
        final Order sut = orderService.startDelivery(order.getId());

        //then
        assertThat(sut.getStatus()).isEqualTo(OrderStatus.DELIVERING);
    }

    @DisplayName("startDelivery - 주문이 존재하지 않으면 예외를 반환한다")
    @Test
    void startDeliveryNotExistOrder() {
        //given
        final Order order = createOrder(OrderType.DELIVERY, OrderStatus.SERVED);

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
        final Order order = createOrder(orderType, OrderStatus.SERVED);

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
        final Order order = createOrder(OrderType.DELIVERY, orderStatus);

        given(orderRepository.findById(any())).willReturn(Optional.of(order));

        //when, then
        assertThatExceptionOfType(IllegalStateException.class)
            .isThrownBy(() -> orderService.startDelivery(order.getId()));
    }

    @DisplayName("completeDelivery - 배달을 완료할 수 있다. 성공시 상태 배송완료")
    @Test
    void completeDelivery() {
        //given
        final Order order = createOrder(OrderType.DELIVERY, OrderStatus.DELIVERING);

        given(orderRepository.findById(any())).willReturn(Optional.of(order));

        //when
        final Order sut = orderService.completeDelivery(order.getId());

        //then
        assertThat(sut.getStatus()).isEqualTo(OrderStatus.DELIVERED);
    }

    @DisplayName("completeDelivery - 주문이 존재하지 않으면 예외를 반환한다")
    @Test
    void completeDeliveryNotExistOrder() {
        //given
        final Order order = createOrder(OrderType.DELIVERY, OrderStatus.DELIVERING);

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
        final Order order = createOrder(orderType, OrderStatus.DELIVERING);

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
        final Order order = createOrder(OrderType.DELIVERY, orderStatus);

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
        final Order order = createOrder(OrderType.valueOf(typeValue), OrderStatus.valueOf(statusValue));

        given(orderRepository.findById(any())).willReturn(Optional.of(order));

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
        final Order order = createOrder(OrderType.DELIVERY, orderStatus);

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
        final Order order = createOrder(OrderType.TAKEOUT, orderStatus);

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
        final Order order = createOrder(OrderType.EAT_IN, orderStatus);

        given(orderRepository.findById(any())).willReturn(Optional.of(order));

        //when, then
        assertThatExceptionOfType(IllegalStateException.class)
            .isThrownBy(() -> orderService.complete(order.getId()));
    }

    @DisplayName("complete - 주문 타입이 매장 내 식사라면 주문 테이블이 치워진 상태여야 한다")
    @Test
    void completeTypeEatInTableShouldEmpty() {
        //given
        final Order order = createOrder(OrderType.EAT_IN, OrderStatus.SERVED);

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
        final Order order1 = createOrder(OrderType.EAT_IN, OrderStatus.SERVED);
        final Order order2 = createOrder(OrderType.DELIVERY, OrderStatus.COMPLETED);

        given(orderRepository.findAll()).willReturn(Arrays.asList(order1, order2));

        //when
        final List<Order> sut = orderService.findAll();

        //then
        assertAll(
            () -> assertThat(sut.get(0)).isEqualTo(order1),
            () -> assertThat(sut.get(0)
                .getType()).isEqualTo(OrderType.EAT_IN),
            () -> assertThat(sut.get(0)
                .getStatus()).isEqualTo(OrderStatus.SERVED),
            () -> assertThat(sut.get(1)).isEqualTo(order2),
            () -> assertThat(sut.get(1)
                .getType()).isEqualTo(OrderType.DELIVERY),
            () -> assertThat(sut.get(1)
                .getStatus()).isEqualTo(OrderStatus.COMPLETED)
        );
    }

    private Order createOrder(final OrderType type, final OrderStatus orderStatus) {
        final Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setType(type);

        final UUID orderTableId = UUID.randomUUID();
        order.setOrderTableId(orderTableId);

        order.setOrderTable(ORDER_TABLE1());

        final List<OrderLineItem> orderLineItems = Arrays.asList(orderLineItem1, orderLineItem2);

        order.setOrderLineItems(orderLineItems);

        order.setStatus(orderStatus);
        return order;
    }

    private OrderLineItem createOrderLineItem(final int price, final int quantity, final Menu menu) {
        final OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenuId(UUID.randomUUID());
        orderLineItem.setPrice(BigDecimal.valueOf(price));
        orderLineItem.setQuantity(quantity);
        orderLineItem.setMenu(menu);
        return orderLineItem;
    }

    private Menu createMenu(final String name, final Long price) {
        final Menu menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setName(name);
        menu.setPrice(BigDecimal.valueOf(price));
        menu.setMenuGroupId(UUID.randomUUID());
        menu.setDisplayed(true);

        final List<MenuProduct> menuProducts = Arrays.asList(menuProduct1, menuProduct2);

        menu.setMenuProducts(menuProducts);
        return menu;
    }

    private MenuProduct makeMenuProduct(final Product product, final long quantity) {
        final MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProduct(product);
        menuProduct.setProductId(product.getId());
        menuProduct.setQuantity(quantity);
        return menuProduct;
    }

}
