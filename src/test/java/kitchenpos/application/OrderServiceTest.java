package kitchenpos.application;

import kitchenpos.application.fixture.MenuFixture;
import kitchenpos.application.fixture.OrderLineItemFixture;
import kitchenpos.application.fixture.OrderFixture;
import kitchenpos.domain.*;
import kitchenpos.domain.Order;
import kitchenpos.infra.KitchenridersClient;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @InjectMocks
    OrderService orderService;
    @Mock
    OrderRepository orderRepository;
    @Mock
    MenuRepository menuRepository;
    @Mock
    OrderTableRepository orderTableRepository;
    @Mock
    KitchenridersClient kitchenridersClient;

    @Test@DisplayName("주문타입은 필수이다.")
    void throwExceptionWithOutType(){
        Order order = new Order();

        assertThatThrownBy(() -> orderService.create(order)).isInstanceOf(IllegalArgumentException.class);
    }


    @Test
    @DisplayName("주문 생성시 상태는 WAITTING 으로 생성된다.")
    void createOrder() {
        //given
        Order order = OrderFixture.makeOrder(OrderType.EAT_IN, OrderLineItemFixture.DEFAULT_ORDER_LINE_ITEM);


        given(menuRepository.findAllByIdIn(any())).willReturn(List.of(MenuFixture.DEFAULT_MENU));
        given(menuRepository.findById(any())).willReturn(Optional.of(MenuFixture.DEFAULT_MENU));
        given(orderTableRepository.findById(any())).willReturn(Optional.of(OrderFixture.DEFAULT_ORDER_TABLE));

        given(orderRepository.save(any())).willReturn(order);
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
        Order order = OrderFixture.makeOrder(OrderType.EAT_IN, orderMenuList);
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

        given(menuRepository.findAllByIdIn(any())).willReturn(List.of(menu1, menu2));
        Order order = OrderFixture.makeOrder(OrderType.EAT_IN, orderLineItems);
        //when
        //then
        assertThatThrownBy(() -> orderService.create(order)).isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @DisplayName("주문시에 주문메뉴 리스트의 수량은 1개 이상이어야 한다.")
    @ValueSource(ints = {-1, 0})
    void throwExceptionWhenQuantityIsUnderOne(int quantity) {
        //given
        Order order = OrderFixture.makeOrder(OrderType.EAT_IN, OrderLineItemFixture.createOrderLineItems(quantity));

        given(menuRepository.findAllByIdIn(any())).willReturn(List.of(MenuFixture.DEFAULT_MENU));
        given(menuRepository.findById(any())).willReturn(Optional.of(MenuFixture.DEFAULT_MENU));
        //when
        //then
        assertThatThrownBy(() -> orderService.create(order)).isInstanceOf(IllegalArgumentException.class);

    }


}
