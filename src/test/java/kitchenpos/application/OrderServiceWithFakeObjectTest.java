package kitchenpos.application;

import kitchenpos.application.fake.FakeMenuRepository;
import kitchenpos.application.fake.FakeOrderRepository;
import kitchenpos.application.fake.FakeOrderTableRepository;
import kitchenpos.application.fixture.MenuFixture;
import kitchenpos.application.fixture.OrderFixture;
import kitchenpos.application.fixture.OrderLineItemFixture;
import kitchenpos.domain.Order;
import kitchenpos.domain.*;
import kitchenpos.infra.KitchenridersClient;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

class OrderServiceWithFakeObjectTest {

    private OrderService orderService;
    private OrderRepository orderRepository;
    private MenuRepository menuRepository;
    private OrderTableRepository orderTableRepository;
    private KitchenridersClient kitchenridersClient;

    @BeforeEach
    public void setup() {
        orderRepository = new FakeOrderRepository();
        menuRepository = new FakeMenuRepository();
        orderTableRepository = new FakeOrderTableRepository();
        kitchenridersClient = new KitchenridersClient();
        orderService = new OrderService(orderRepository, menuRepository, orderTableRepository, kitchenridersClient);
    }

    @Test@DisplayName("주문타입은 필수이다.")
    void throwExceptionWithOutType(){
        //given
        Order order = new Order();
        //when
        //then
        assertThatThrownBy(() -> orderService.create(order)).isInstanceOf(IllegalArgumentException.class);
    }


    @Test
    @DisplayName("주문 생성시 상태는 WAITTING 으로 생성된다.")
    void createOrder() {
        //given
        Order order = OrderFixture.makeOrder(OrderType.EAT_IN, OrderLineItemFixture.DEFAULT_ORDER_LINE_ITEM,OrderFixture.DEFAULT_ORDER_TABLE);
        menuRepository.save(MenuFixture.DEFAULT_MENU);
        orderTableRepository.save(OrderFixture.DEFAULT_ORDER_TABLE);
        //when
        Order createdOrder = orderService.create(order);
        //then
        assertAll(
                () -> assertThat(createdOrder).isNotNull(),
                () -> assertThat(createdOrder.getId()).isInstanceOf(UUID.class),
                () -> assertThat(createdOrder.getType()).isEqualTo(OrderType.EAT_IN),
                () -> assertThat(createdOrder.getStatus()).isEqualTo(OrderStatus.WAITING)
        );
    }

    @ParameterizedTest
    @DisplayName("주문 생성시에는 주문 메뉴리스트는 필수로 가져야 한다.")
    @NullAndEmptySource
    void throwExceptionWhenOrderLineIsEmpty(List<OrderLineItem> orderMenuList) {
        //given
        Order order = OrderFixture.makeOrder(OrderType.EAT_IN, orderMenuList,OrderFixture.DEFAULT_ORDER_TABLE);
        //when
        //then
        assertThatThrownBy(() -> orderService.create(order)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("주문시 주문메뉴리스트의 모든 메뉴는 실제 메뉴에 등록이 되어있어야 한다.")
    void throwExceptionWhenMenuAndOrderLineItemDiff() {
        //given
        Menu menu1 = MenuFixture.create("menu1", "10000");
        Menu menu2 = MenuFixture.create("menu2", "10000");

        List<OrderLineItem> orderLineItems = OrderLineItemFixture.moreMenuLine(1, menu1, menu2);
        menuRepository.save(menu1);
        menuRepository.save(menu2);

        //when
        Order order = OrderFixture.makeOrder(OrderType.EAT_IN, orderLineItems,OrderFixture.DEFAULT_ORDER_TABLE);

        // then
        assertThatThrownBy(() -> orderService.create(order)).isInstanceOf(IllegalArgumentException.class);
    }

    //'주문 내역 중 화면에 표시 되지 않은 메뉴는 포함될수 없다.'
    //'주문내역 가격과 메뉴의 가격이 다르면 주문할 수 없다'
    @Test
    @DisplayName("주문 내역 중 화면에 표시 되지 않은 메뉴는 포함될수 없다.")
    void throwExceptionWhenOrderUnDisplayedMenu(){
        //given
        Menu menu1 = MenuFixture.create("menu1", "10000");
        menu1.setDisplayed(false);
        menuRepository.save(menu1);

        List<OrderLineItem> orderLineItems = OrderLineItemFixture.createMenuLine(1, menu1);
        //when
        Order order = OrderFixture.makeOrder(OrderType.EAT_IN, orderLineItems,OrderFixture.DEFAULT_ORDER_TABLE);
        //then
        assertThatThrownBy(() -> orderService.create(order)).isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("주문내역 가격과 메뉴의 가격이 다르면 주문할 수 없다.")
    void throwExceptionWhenMenuPriceIsNotSameAsOrderPrice(){
        //given
        Menu menu1 = MenuFixture.create("menu1", "10000");
        menuRepository.save(menu1);

        List<OrderLineItem> orderLineItems = OrderLineItemFixture.createMenuLine(1, menu1);
        orderLineItems.forEach(orderLineItem -> orderLineItem.setPrice(BigDecimal.valueOf(20000)));
        //when
        Order order = OrderFixture.makeOrder(OrderType.EAT_IN, orderLineItems,OrderFixture.DEFAULT_ORDER_TABLE);
        //then
        assertThatThrownBy(() -> orderService.create(order)).isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @DisplayName("매장식사가 아닌 주문시에는 주문메뉴 리스트의 수량은 0개 이상이어야 한다.")
    @ValueSource(ints = {-1, -2, -3})
    void throwExceptionWhenQuantityIsUnderOne(int quantity) {

        //given
        Order order = readyToOrder(OrderType.TAKEOUT, quantity);

        //when
        //then
        assertThatThrownBy(() -> orderService.create(order))
              .isInstanceOf(IllegalArgumentException.class);

    }

    @Test
    @DisplayName("준비상태의 주문은 수락할수 있다.")
    void canAcceptWaitingOrder(){
        //given
        //when
        Order acceptedOrder = getAcceptedOrder(OrderType.EAT_IN);

        //then
        assertThat(acceptedOrder.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
    }



    @ParameterizedTest
    @DisplayName("배달 주문은 주소지가 비어있으면 안된다.")
    @NullAndEmptySource
    void deliveryAddressCannotBeNullOrEmpty(String address){
        //given
        Order order = readyToOrder(OrderType.DELIVERY);
        //when
        //then
        assertThatThrownBy(() -> orderService.create(order))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("서빙된 배달 주문은 배달중으로 준비될 수 있다.")
    void servedOrderCanBeDelivering(){
        //given
        //when
        Order deliveringOrder = deliveringOrder(OrderType.DELIVERY, "주소지");
        //then
        assertThat(deliveringOrder.getStatus()).isEqualTo(OrderStatus.DELIVERING);
    }

    @Test
    @DisplayName("배달주문의 경우 배달이 완료되지 않으면 완료될수 없다.")
    void cannotCompletedDeliveryOrder(){
        //given
        //when
        Order deliveringOrder = deliveringOrder(OrderType.DELIVERY, "주소지");

        //then
        assertThatThrownBy(() -> orderService.complete(deliveringOrder.getId()))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("배달이 완료되면 주문이 완료될수 있다.")
    void canCompleteOrderDelivery(){
        //given
        Order deliveringCompletedOrder = compltedDeliveryOrder(OrderType.DELIVERY);

        //when
        Order completeOrder = orderService.complete(deliveringCompletedOrder.getId());

        //then
        assertThat(completeOrder.getStatus()).isEqualTo(OrderStatus.COMPLETED);
    }

    @Test
    @DisplayName("포장은 서빙이 완료되면 완료될수 있다.")
    void canCompleteTakeOutOrder(){
        //given
        Order takeOutOrder = createOrder(OrderType.TAKEOUT);
        //when
        Order acceptedOrder = orderService.accept(takeOutOrder.getId());
        Order servedOrder = orderService.serve(acceptedOrder.getId());
        Order complete = orderService.complete(servedOrder.getId());
        //then
        assertThat(complete.getStatus()).isEqualTo(OrderStatus.COMPLETED);
    }

    @Test
    @DisplayName("매장식사는 식사가 완료된뒤 완료될수 있다.")
    void canCompleteEatInOrder(){
        //given
        Order eatInOrder = createOrder(OrderType.EAT_IN);
        //when
        Order acceptedOrder = orderService.accept(eatInOrder.getId());
        Order servedOrder = orderService.serve(acceptedOrder.getId());
        Order complete = orderService.complete(servedOrder.getId());
        //then
        assertThat(complete.getStatus()).isEqualTo(OrderStatus.COMPLETED);
    }

    private Order compltedDeliveryOrder(OrderType type){
        Order deliveringOrder = deliveringOrder(type, "adress");
        return orderService.completeDelivery(deliveringOrder.getId());
    }

    private Order getAcceptedOrder(OrderType type) {
        //given
        Order createdOrder = createOrder(type);
        //when
        return orderService.accept(createdOrder.getId());
    }

    private Order deliveringOrder(OrderType type, String address) {
        //given
        Order createdOrder = createOrder(type, address);
        Order acceptedOrder = orderService.accept(createdOrder.getId());
        Order servedOrder = orderService.serve(acceptedOrder.getId());
        //when
        return orderService.startDelivery(servedOrder.getId());
    }



    private Order readyToOrder(OrderType type){
        return readyToOrder(type, 1);
    }

    private Order readyToOrder(OrderType type, int quantity){
        //given
        Order order = OrderFixture.makeOrder(type, OrderLineItemFixture.createOrderLineItems(quantity), OrderFixture.DEFAULT_ORDER_TABLE);
        menuRepository.save(MenuFixture.DEFAULT_MENU);
        orderTableRepository.save(OrderFixture.DEFAULT_ORDER_TABLE);
        return order;
    }

    private Order createOrder(OrderType type){
        //given
        Order order = readyToOrder(type);
        //when
        return orderService.create(order);
    }

    private Order createOrder(OrderType type, String deliveryAddress){
        //given
        Order order = readyToOrder(type);
        order.setDeliveryAddress(deliveryAddress);
        return orderService.create(order);
    }
}
