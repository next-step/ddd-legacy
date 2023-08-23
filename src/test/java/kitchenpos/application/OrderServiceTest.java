package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.infra.KitchenridersClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static java.util.Collections.emptyList;
import static kitchenpos.application.MenuServiceTest.createMenu;
import static kitchenpos.application.MenuServiceTest.createMenuProduct;
import static kitchenpos.application.OrderTableServiceTest.createOrderTable;
import static kitchenpos.application.ProductServiceTest.createProduct;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private MenuRepository menuRepository;
    @Mock
    private OrderTableRepository orderTableRepository;
    @Mock
    private KitchenridersClient kitchenridersClient;

    @InjectMocks
    private OrderService sut;

    private final static UUID uuid = UUID.randomUUID();

    private Menu menu;
    private OrderLineItem orderLineItem;
    private OrderTable orderTable;

    @BeforeEach
    void setUp() {
        Product product = createProduct("햄버거", new BigDecimal("1000"));
        MenuProduct menuProduct = createMenuProduct(product, 1L);

        menu = createMenu(new BigDecimal("2000"), "메뉴", uuid, List.of(menuProduct));
        orderTable = createOrderTable("테이블1", 3);
        orderLineItem = createOrderLineItem(1L, menu.getPrice());
    }

    @Test
    void 주문_타입이_null이면_주문을_생성할_수_없다() {
        // given
        Order request = new Order();
        request.setType(null);

        // when & then
        assertThatThrownBy(() -> sut.create(request)).isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest(name = "주문은_최소한_1개_이상의_상품으로_이루어져야_주문을_생성할_수_있다: orderLineItems = {0}")
    @NullAndEmptySource
    void 주문은_최소한_1개_이상의_상품으로_이루어져야_주문을_생성할_수_있다(List<OrderLineItem> orderLineItems) {
        // given
        Order request = createOrder(OrderType.EAT_IN, orderLineItems, "주소지");

        // when & then
        assertThatThrownBy(() -> sut.create(request)).isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 포장_및_배달_주문의_경우_주문할_메뉴의_양이_0_이상이어야_한다() {
        // given
        Order request = createOrder(OrderType.EAT_IN, List.of(orderLineItem), "주소지");

        given(menuRepository.findAllByIdIn(any())).willReturn(emptyList());

        // when & then
        assertThatThrownBy(() -> sut.create(request)).isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest(name = "포장_및_배달_주문의_경우_주문할_메뉴의_양이_0이상이_아니라면_주문을_생성할_수_없다: orderType = {0}")
    @EnumSource(value = OrderType.class, names = {"DELIVERY", "TAKEOUT"})
    void 포장_및_배달_주문의_경우_주문할_메뉴의_양이_0이상이_아니라면_주문을_생성할_수_없다(OrderType orderType) {
        // given
        OrderLineItem orderLineItem = createOrderLineItem(-1L, menu.getPrice());
        Order request = createOrder(orderType, List.of(orderLineItem), "주소지");

        given(menuRepository.findAllByIdIn(any())).willReturn(List.of(menu));

        // when & then
        assertThatThrownBy(() -> sut.create(request)).isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 화면에_표시되지_않고_있는_메뉴를_주문한_경우엔_주문을_생성할_수_없다() {
        // given
        menu.setDisplayed(false);

        Order request = createOrder(OrderType.EAT_IN, List.of(orderLineItem), "주소지");

        given(menuRepository.findAllByIdIn(any())).willReturn(List.of(menu));
        given(menuRepository.findById(any())).willReturn(Optional.of(menu));

        // when & then
        assertThatThrownBy(() -> sut.create(request)).isExactlyInstanceOf(IllegalStateException.class);
    }

    @Test
    void 메뉴의_현재_가격과_주문시점의_메뉴_가격이_다르면_주문을_생성할_수_없다() {
        // given
        OrderLineItem orderLineItem = createOrderLineItem(1L, new BigDecimal("3000"));
        Order request = createOrder(OrderType.EAT_IN, List.of(orderLineItem), "주소지");

        given(menuRepository.findAllByIdIn(any())).willReturn(List.of(menu));
        given(menuRepository.findById(any())).willReturn(Optional.of(menu));

        // when & then
        assertThatThrownBy(() -> sut.create(request)).isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest(name = "배달_주문의_경우_주소지가_없다면_주문을_생성할_수_없다: deliveryAddress = {0}")
    @NullAndEmptySource
    void 배달_주문의_경우_주소지가_없다면_주문을_생성할_수_없다(String deliveryAddress) {
        // given
        Order request = createOrder(OrderType.DELIVERY, List.of(orderLineItem), deliveryAddress);

        given(menuRepository.findAllByIdIn(any())).willReturn(List.of(menu));
        given(menuRepository.findById(any())).willReturn(Optional.of(menu));

        // when & then
        assertThatThrownBy(() -> sut.create(request)).isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 매장에서_식사하는_경우_테이블에_착석해_있지_않다면_주문을_생성할_수_없다() {
        // given
        Order request = createOrder(OrderType.EAT_IN, List.of(orderLineItem), "주소");

        orderTable.setOccupied(false);

        given(menuRepository.findAllByIdIn(any())).willReturn(List.of(menu));
        given(menuRepository.findById(any())).willReturn(Optional.of(menu));
        given(orderTableRepository.findById(any())).willReturn(Optional.of(orderTable));

        // when & then
        assertThatThrownBy(() -> sut.create(request)).isExactlyInstanceOf(IllegalStateException.class);
    }

    @Test
    void 주문을_생성할_수_있다() {
        // given
        Order request = createOrder(OrderType.EAT_IN, List.of(orderLineItem), "주소");

        given(menuRepository.findAllByIdIn(any())).willReturn(List.of(menu));
        given(menuRepository.findById(any())).willReturn(Optional.of(menu));
        given(orderTableRepository.findById(any())).willReturn(Optional.of(orderTable));
        given(orderRepository.save(any())).willReturn(new Order());

        // when
        Order result = sut.create(request);

        // then
        assertThat(result).isExactlyInstanceOf(Order.class);
    }

    @Test
    void 주문이_대기중이_아닌_경우에는_수락_처리할_수_없다() {
        // given
        Order order = createOngoingOrder(OrderType.TAKEOUT, OrderStatus.ACCEPTED);

        given(orderRepository.findById(any())).willReturn(Optional.of(order));

        // when & then
        assertThatThrownBy(() -> sut.accept(uuid)).isExactlyInstanceOf(IllegalStateException.class);
    }

    @Test
    void 주문을_수락_처리할_수_있다() {
        // given
        Order order = createOngoingOrder(OrderType.TAKEOUT, OrderStatus.WAITING);

        given(orderRepository.findById(any())).willReturn(Optional.of(order));

        // when
        sut.accept(uuid);

        // then
        assertThat(order.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
    }

    @Test
    void 배달_주문의_수락처리는_kitchenridersClient_배달_요청_API를_요청한_후에_이뤄진다() {
        // given
        Order order = createOngoingOrder(OrderType.DELIVERY, OrderStatus.WAITING);
        order.setOrderLineItems(List.of(orderLineItem));

        given(orderRepository.findById(any())).willReturn(Optional.of(order));

        // when
        sut.accept(uuid);

        // then
        verify(kitchenridersClient).requestDelivery(any(), any(), any());
        assertThat(order.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
    }

    @Test
    void 수락_상태가_아닌_주문을_제공_처리할_수_없다() {
        // given
        Order order = createOngoingOrder(OrderType.DELIVERY, OrderStatus.WAITING);

        given(orderRepository.findById(any())).willReturn(Optional.of(order));

        // when & then
        assertThatThrownBy(() -> sut.serve(uuid)).isExactlyInstanceOf(IllegalStateException.class);
    }

    @Test
    void 주문을_제공_처리할_수_있다() {
        // given
        Order order = createOngoingOrder(OrderType.TAKEOUT, OrderStatus.ACCEPTED);

        given(orderRepository.findById(any())).willReturn(Optional.of(order));

        // then
        sut.serve(uuid);

        // then
        assertThat(order.getStatus()).isEqualTo(OrderStatus.SERVED);
    }

    @Test
    void 배달_주문이_아닌_경우_배달을_시작_처리할_수_없다() {
        // given
        Order order = createOngoingOrder(OrderType.TAKEOUT, OrderStatus.ACCEPTED);

        given(orderRepository.findById(any())).willReturn(Optional.of(order));

        // when & then
        assertThatThrownBy(() -> sut.startDelivery(uuid)).isExactlyInstanceOf(IllegalStateException.class);
    }

    @Test
    void 주문이_제공인_상태가_아닌_경우_배달을_시작_처리할_수_없다() {
        // given
        Order order = createOngoingOrder(OrderType.DELIVERY, OrderStatus.ACCEPTED);

        given(orderRepository.findById(any())).willReturn(Optional.of(order));

        // when & then
        assertThatThrownBy(() -> sut.startDelivery(uuid)).isExactlyInstanceOf(IllegalStateException.class);
    }

    @Test
    void 배달을_시작_처리할_수_있다() {
        // given
        Order order = createOngoingOrder(OrderType.DELIVERY, OrderStatus.SERVED);

        given(orderRepository.findById(any())).willReturn(Optional.of(order));

        // then
        sut.startDelivery(uuid);

        // then
        assertThat(order.getStatus()).isEqualTo(OrderStatus.DELIVERING);
    }

    @Test
    void 배달을_시작한_상태가_아닌_경우_배달을_완료_처리할_수_없다() {
        // given
        Order order = createOngoingOrder(OrderType.DELIVERY, OrderStatus.SERVED);

        given(orderRepository.findById(any())).willReturn(Optional.of(order));

        // when & then
        assertThatThrownBy(() -> sut.completeDelivery(uuid)).isExactlyInstanceOf(IllegalStateException.class);
    }

    @Test
    void 배달을_완료_처리할_수_있다() {
        // given
        Order order = createOngoingOrder(OrderType.DELIVERY, OrderStatus.DELIVERING);

        given(orderRepository.findById(any())).willReturn(Optional.of(order));

        // when
        sut.completeDelivery(uuid);

        // then
        assertThat(order.getStatus()).isEqualTo(OrderStatus.DELIVERED);
    }

    @Test
    void 배달_주문이면서_배달_완료_상태가_아닌_경우_주문을_완료_처리할_수_없다() {
        // given
        Order order = createOngoingOrder(OrderType.DELIVERY, OrderStatus.DELIVERING);

        given(orderRepository.findById(any())).willReturn(Optional.of(order));

        // when & then
        assertThatThrownBy(() -> sut.complete(uuid)).isExactlyInstanceOf(IllegalStateException.class);
    }

    @ParameterizedTest(name = "포장_주문_또는_매장에서_식사하는_경우_제공_상태가_아닌_경우_주문을_완료_처리할_수_없다: orderType = {0}")
    @EnumSource(value = OrderType.class, names = {"EAT_IN", "TAKEOUT"})
    void 포장_주문_또는_매장에서_식사하는_경우_제공_상태가_아닌_경우_주문을_완료_처리할_수_없다(OrderType orderType) {
        // given
        Order order = createOngoingOrder(orderType, OrderStatus.ACCEPTED);

        given(orderRepository.findById(any())).willReturn(Optional.of(order));

        // when & then
        assertThatThrownBy(() -> sut.complete(uuid)).isExactlyInstanceOf(IllegalStateException.class);
    }

    @Test
    void 배달_주문을_완료_처리할_수_있다() {
        // given
        Order order = createOngoingOrder(OrderType.DELIVERY, OrderStatus.DELIVERED);

        given(orderRepository.findById(any())).willReturn(Optional.of(order));

        // when
        sut.complete(uuid);

        // then
        assertThat(order.getStatus()).isEqualTo(OrderStatus.COMPLETED);
    }

    @Test
    void 포장_주문을_완료_처리할_수_있다() {
        // given
        Order order = createOngoingOrder(OrderType.TAKEOUT, OrderStatus.SERVED);

        given(orderRepository.findById(any())).willReturn(Optional.of(order));

        // when
        sut.complete(uuid);

        // then
        assertThat(order.getStatus()).isEqualTo(OrderStatus.COMPLETED);
    }

    @Test
    void 매장에서_식사하는_주문을_완료_처리할_수_있다() {
        // given
        Order order = createOngoingOrder(OrderType.EAT_IN, OrderStatus.SERVED);
        order.setOrderTable(orderTable);

        given(orderRepository.findById(any())).willReturn(Optional.of(order));
        given(orderRepository.existsByOrderTableAndStatusNot(any(), any())).willReturn(false);

        // when
        sut.complete(uuid);

        // then
        assertThat(order.getStatus()).isEqualTo(OrderStatus.COMPLETED);
        assertThat(orderTable.getNumberOfGuests()).isZero();
        assertThat(orderTable.isOccupied()).isFalse();
    }

    private Order createOngoingOrder(OrderType orderType, OrderStatus orderStatus) {
        Order request = new Order();
        request.setType(orderType);
        request.setStatus(orderStatus);
        return request;
    }

    private Order createOrder(OrderType orderType, List<OrderLineItem> orderLineItems, String deliveryAddress) {
        Order request = new Order();
        request.setType(orderType);
        request.setOrderLineItems(orderLineItems);
        request.setDeliveryAddress(deliveryAddress);
        request.setOrderTableId(uuid);
        return request;
    }

    private OrderLineItem createOrderLineItem(long quantity, BigDecimal price) {
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenu(menu);
        orderLineItem.setQuantity(quantity);
        orderLineItem.setPrice(price);
        return orderLineItem;
    }
}
