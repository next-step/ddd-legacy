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

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

class OrderServiceWithFakeObjectTest {

    OrderService orderService;
    OrderRepository orderRepository;
    MenuRepository menuRepository;
    OrderTableRepository orderTableRepository;
    KitchenridersClient kitchenridersClient;

    @BeforeEach
    public void setup() {
        orderRepository = new FakeOrderRepository();
        menuRepository = new FakeMenuRepository();
        orderTableRepository = new FakeOrderTableRepository();
        orderService = new OrderService(orderRepository, menuRepository, orderTableRepository, kitchenridersClient);
    }

    @Test@DisplayName("주문타입은 필수이다.")
    void throwExceptionWithOutType(){
        Order order = new Order();

        assertThatThrownBy(() -> orderService.create(order)).isInstanceOf(IllegalArgumentException.class);
    }


    @Test
    @DisplayName("주문 생성시 상태는 WAITTING 으로 생성된다.")
    void createOrder() {


        Order order = OrderFixture.makeOrder(OrderType.EAT_IN, OrderLineItemFixture.DEFAULT_ORDER_LINE_ITEM,OrderFixture.DEFAULT_ORDER_TABLE);
        //given
        menuRepository.save(MenuFixture.DEFAULT_MENU);
        orderTableRepository.save(OrderFixture.DEFAULT_ORDER_TABLE);

        Order createdOrder = orderService.create(order);
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
        Order order = OrderFixture.makeOrder(OrderType.EAT_IN, orderMenuList,OrderFixture.DEFAULT_ORDER_TABLE);
        assertThatThrownBy(() -> orderService.create(order)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("주문시 주문메뉴리스트의 모든 메뉴는 실제 메뉴에 등록이 되어있어야 한다.")
    void throwExceptionWhenMenuAndOrderLineItemDiff() {
        Menu menu1 = MenuFixture.create("menu1", "10000");
        Menu menu2 = MenuFixture.create("menu2", "10000");

        List<OrderLineItem> orderLineItems = OrderLineItemFixture.moreMenuLine(1, menu1, menu2);
        //given
        menuRepository.save(menu1);
        menuRepository.save(menu2);

        Order order = OrderFixture.makeOrder(OrderType.EAT_IN, orderLineItems,OrderFixture.DEFAULT_ORDER_TABLE);

        assertThatThrownBy(() -> orderService.create(order)).isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @DisplayName("주문시에 주문메뉴 리스트의 수량은 1개 이상이어야 한다.")
    @ValueSource(ints = {-1, 0})
    void throwExceptionWhenQuantityIsUnderOne(int quantity) {
        Order order = OrderFixture.makeOrder(OrderType.EAT_IN, List.of(OrderLineItemFixture.createOrderLineItem(quantity)), OrderFixture.DEFAULT_ORDER_TABLE);

        menuRepository.save(MenuFixture.DEFAULT_MENU);
        orderTableRepository.save(OrderFixture.DEFAULT_ORDER_TABLE);

        assertThatThrownBy(() -> orderService.create(order)).isInstanceOf(IllegalArgumentException.class);

    }


}
