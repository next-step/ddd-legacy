package kitchenpos.application;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
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
import org.assertj.core.api.Assertions;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static kitchenpos.fixture.MenuFixtures.createMenu;
import static kitchenpos.fixture.MenuFixtures.createMenuProduct;
import static kitchenpos.fixture.MenuGroupFixtures.createMenuGroup;
import static kitchenpos.fixture.OrderFixtures.createOrder;
import static kitchenpos.fixture.OrderFixtures.createOrderLineItem;
import static kitchenpos.fixture.OrderTableFixtures.createOrderTable;
import static kitchenpos.fixture.ProductFixtures.createProduct;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {
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

    private OrderTable orderTable;
    private MenuGroup menuGroup;
    private Product product;
    private MenuProduct menuProduct;
    private Menu menu;
    private Menu notDisplayedMenu;

    @BeforeEach
    void setUp() {
        orderTable = createOrderTable("매장테이블", true, 2);
        menuGroup = createMenuGroup("메뉴그룹1");
        product = createProduct("상품1", new BigDecimal("2000"));
        menuProduct = createMenuProduct(product, 1);
        menu = createMenu("메뉴1", new BigDecimal("1500"), menuGroup, true, List.of(menuProduct));
        notDisplayedMenu = createMenu("메뉴1", new BigDecimal("1500"), menuGroup, false, List.of(menuProduct));
    }

    @Test
    void 주문을_등록할_수_있다() {
        //given
        LocalDateTime orderDateTime = LocalDateTime.now();
        OrderLineItem orderLineItem = createOrderLineItem(menu, 1L, new BigDecimal("1500"), 1L);
        Order order = createOrder("서울", orderTable, orderDateTime, OrderStatus.WAITING, OrderType.EAT_IN, List.of(orderLineItem));

        given(menuRepository.findAllByIdIn(any()))
                .willReturn(List.of(menu));
        given(menuRepository.findById(any()))
                .willReturn(Optional.of(menu));
        given(orderTableRepository.findById(any()))
                .willReturn(Optional.of(orderTable));
        given(orderRepository.save(any()))
                .willReturn(order);

        //when
        Order result = orderService.create(order);

        //then
        assertThat(result.getDeliveryAddress()).isEqualTo(order.getDeliveryAddress());
    }


    @Test
    void 주문타입은_필수로_등록되어야_한다() {
        //given
        LocalDateTime orderDateTime = LocalDateTime.now();
        OrderLineItem orderLineItem = createOrderLineItem(menu, 1L, new BigDecimal("1500"), 1L);
        Order order = createOrder("서울", orderTable, orderDateTime, OrderStatus.WAITING, null, List.of(orderLineItem));

        //when, then
        assertThatThrownBy(() -> orderService.create(order))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @NullAndEmptySource
    void 주문상품_목록은_필수로_등록되어야_한다(List<OrderLineItem> orderLineItems) {
        LocalDateTime orderDateTime = LocalDateTime.now();
        Order order = createOrder("서울", orderTable, orderDateTime, OrderStatus.WAITING, OrderType.EAT_IN, orderLineItems);

        //when, then
        assertThatThrownBy(() -> orderService.create(order))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 존재하지_않는_메뉴는_주문할_수_없다() {
        //given
        LocalDateTime orderDateTime = LocalDateTime.now();
        OrderLineItem orderLineItem = createOrderLineItem(menu, 0L, new BigDecimal("1500"), 1L);
        Order order = createOrder("서울", orderTable, orderDateTime, OrderStatus.WAITING, OrderType.EAT_IN, List.of(orderLineItem));

        given(menuRepository.findAllByIdIn(any()))
                .willReturn(List.of(menu));
        given(menuRepository.findById(any()))
                .willReturn(Optional.empty());

        //when, then
        assertThatThrownBy(() -> orderService.create(order))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void 매장식사가_아니라면_0개_이상의_주문내역이_포함되어야_한다() {
        //given
        LocalDateTime orderDateTime = LocalDateTime.now();
        OrderLineItem orderLineItem = createOrderLineItem(menu, -1L, new BigDecimal("1500"), 1L);
        Order order = createOrder("서울", orderTable, orderDateTime, OrderStatus.WAITING, OrderType.TAKEOUT, List.of(orderLineItem));

        given(menuRepository.findAllByIdIn(any()))
                .willReturn(List.of(menu));

        //when, then
        assertThatThrownBy(() -> orderService.create(order))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 판매중이지_않은_메뉴가_주문에_존재해서는_안된다() {
        //given
        LocalDateTime orderDateTime = LocalDateTime.now();
        OrderLineItem orderLineItem = createOrderLineItem(notDisplayedMenu, 1L, new BigDecimal("1500"), 1L);
        Order order = createOrder("서울", orderTable, orderDateTime, OrderStatus.WAITING, OrderType.EAT_IN, List.of(orderLineItem));

        given(menuRepository.findAllByIdIn(any()))
                .willReturn(List.of(notDisplayedMenu));
        given(menuRepository.findById(any()))
                .willReturn(Optional.of(notDisplayedMenu));


        //when, then
        assertThatThrownBy(() -> orderService.create(order))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void 매장_식사인_경우_주문_테이블은_필수로_존재해야_한다() {
        //given
        LocalDateTime orderDateTime = LocalDateTime.now();
        OrderLineItem orderLineItem = createOrderLineItem(menu, 1L, new BigDecimal("1500"), 1L);
        Order order = createOrder("서울", orderTable, orderDateTime, OrderStatus.WAITING, OrderType.EAT_IN, List.of(orderLineItem));

        given(menuRepository.findAllByIdIn(any()))
                .willReturn(List.of(menu));
        given(menuRepository.findById(any()))
                .willReturn(Optional.of(menu));
        given(orderTableRepository.findById(any()))
                .willReturn(Optional.empty());

        //when, then
        assertThatThrownBy(() -> orderService.create(order))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void 주문_승인을_할_수_있다() {
        //given
        LocalDateTime orderDateTime = LocalDateTime.now();
        OrderLineItem orderLineItem = createOrderLineItem(menu, 1L, new BigDecimal("1500"), 1L);
        Order order = createOrder("서울", orderTable, orderDateTime, OrderStatus.WAITING, OrderType.EAT_IN, List.of(orderLineItem));

        given(orderRepository.findById(any()))
                .willReturn(Optional.of(order));

        //when
        Order result = orderService.accept(order.getId());

        //then
        assertThat(result.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
    }

    @Test
    void 주문승인은_전_주문_상태가_대기_상태여야한다() {
        //given
        LocalDateTime orderDateTime = LocalDateTime.now();
        OrderLineItem orderLineItem = createOrderLineItem(menu, 1L, new BigDecimal("1500"), 1L);
        Order order = createOrder("서울", orderTable, orderDateTime, OrderStatus.DELIVERED, OrderType.EAT_IN, List.of(orderLineItem));

        given(orderRepository.findById(any()))
                .willReturn(Optional.of(order));

        //when, then
        assertThatThrownBy(() -> orderService.accept(order.getId()))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void 주문_승인은_주문_타입이_배달인_경우_배달_요청을_보낸다() {
        //given
        LocalDateTime orderDateTime = LocalDateTime.now();
        OrderLineItem orderLineItem = createOrderLineItem(menu, 1L, new BigDecimal("1500"), 1L);
        Order order = createOrder("서울", orderTable, orderDateTime, OrderStatus.WAITING, OrderType.DELIVERY, List.of(orderLineItem));

        given(orderRepository.findById(any()))
                .willReturn(Optional.of(order));

        //when
        Order result = orderService.accept(order.getId());

        //then
        verify(kitchenridersClient).requestDelivery(any(), any(), any());
        assertThat(result.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
    }

    @Test
    void 서빙_완료를_할_수_있다() {
        //given
        LocalDateTime orderDateTime = LocalDateTime.now();
        OrderLineItem orderLineItem = createOrderLineItem(menu, 1L, new BigDecimal("1500"), 1L);
        Order order = createOrder("서울", orderTable, orderDateTime, OrderStatus.ACCEPTED, OrderType.EAT_IN, List.of(orderLineItem));

        given(orderRepository.findById(any()))
                .willReturn(Optional.of(order));

        //when
        Order result = orderService.serve(order.getId());

        //then
        assertThat(result.getStatus()).isEqualTo(OrderStatus.SERVED);
    }

    @Test
    void 서빙_완료는_전_주문_상태가_승인이어야_한다() {
        //given
        LocalDateTime orderDateTime = LocalDateTime.now();
        OrderLineItem orderLineItem = createOrderLineItem(menu, 1L, new BigDecimal("1500"), 1L);
        Order order = createOrder("서울", orderTable, orderDateTime, OrderStatus.WAITING, OrderType.EAT_IN, List.of(orderLineItem));

        given(orderRepository.findById(any()))
                .willReturn(Optional.of(order));

        //when, then
        assertThatThrownBy(() -> orderService.serve(order.getId()))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void 배달중_상태로_변경할_수_있다() {
        //given
        LocalDateTime orderDateTime = LocalDateTime.now();
        OrderLineItem orderLineItem = createOrderLineItem(menu, 1L, new BigDecimal("1500"), 1L);
        Order order = createOrder("서울", orderTable, orderDateTime, OrderStatus.SERVED, OrderType.DELIVERY, List.of(orderLineItem));

        given(orderRepository.findById(any()))
                .willReturn(Optional.of(order));

        //when
        Order result = orderService.startDelivery(order.getId());

        //then
        assertThat(result.getStatus()).isEqualTo(OrderStatus.DELIVERING);
    }

    @Test
    void 배달중_상태는_주문_타입이_배달이어야_한다() {
        //given
        LocalDateTime orderDateTime = LocalDateTime.now();
        OrderLineItem orderLineItem = createOrderLineItem(menu, 1L, new BigDecimal("1500"), 1L);
        Order order = createOrder("서울", orderTable, orderDateTime, OrderStatus.SERVED, OrderType.EAT_IN, List.of(orderLineItem));

        given(orderRepository.findById(any()))
                .willReturn(Optional.of(order));

        //when, then
        assertThatThrownBy(() -> orderService.startDelivery(order.getId()))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void 배달중_상태의_전_주문_상태는_서빙완료여야_한다() {
        //given
        LocalDateTime orderDateTime = LocalDateTime.now();
        OrderLineItem orderLineItem = createOrderLineItem(menu, 1L, new BigDecimal("1500"), 1L);
        Order order = createOrder("서울", orderTable, orderDateTime, OrderStatus.WAITING, OrderType.DELIVERY, List.of(orderLineItem));

        given(orderRepository.findById(any()))
                .willReturn(Optional.of(order));

        //when, then
        assertThatThrownBy(() -> orderService.startDelivery(order.getId()))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void 배달_완료_상태로_변경할_수_있다() {
        //given
        LocalDateTime orderDateTime = LocalDateTime.now();
        OrderLineItem orderLineItem = createOrderLineItem(menu, 1L, new BigDecimal("1500"), 1L);
        Order order = createOrder("서울", orderTable, orderDateTime, OrderStatus.DELIVERING, OrderType.DELIVERY, List.of(orderLineItem));

        given(orderRepository.findById(any()))
                .willReturn(Optional.of(order));

        //when
        Order result = orderService.completeDelivery(order.getId());

        //then
        assertThat(result.getStatus()).isEqualTo(OrderStatus.DELIVERED);
    }

    @Test
    void 배달_완료_상태는_전_주문_상태가_배달중이어야_한다() {
        //given
        LocalDateTime orderDateTime = LocalDateTime.now();
        OrderLineItem orderLineItem = createOrderLineItem(menu, 1L, new BigDecimal("1500"), 1L);
        Order order = createOrder("서울", orderTable, orderDateTime, OrderStatus.SERVED, OrderType.DELIVERY, List.of(orderLineItem));

        given(orderRepository.findById(any()))
                .willReturn(Optional.of(order));

        //when, then
        assertThatThrownBy(() -> orderService.completeDelivery(order.getId()))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void 완료_상태로_변경할_수_있다() {
        //given
        LocalDateTime orderDateTime = LocalDateTime.now();
        OrderLineItem orderLineItem = createOrderLineItem(menu, 1L, new BigDecimal("1500"), 1L);
        Order order = createOrder("서울", orderTable, orderDateTime, OrderStatus.DELIVERED, OrderType.DELIVERY, List.of(orderLineItem));

        given(orderRepository.findById(any()))
                .willReturn(Optional.of(order));

        //when
        Order result = orderService.complete(order.getId());

        //then
        assertThat(result.getStatus()).isEqualTo(OrderStatus.COMPLETED);
    }

    @Test
    void 주문_타입이_배달인_경우_완료상태_전_주문_상태가_배달완료여야_한다() {
        //given
        LocalDateTime orderDateTime = LocalDateTime.now();
        OrderLineItem orderLineItem = createOrderLineItem(menu, 1L, new BigDecimal("1500"), 1L);
        Order order = createOrder("서울", orderTable, orderDateTime, OrderStatus.DELIVERING, OrderType.DELIVERY, List.of(orderLineItem));

        given(orderRepository.findById(any()))
                .willReturn(Optional.of(order));

        //when, then
        assertThatThrownBy(() -> orderService.complete(order.getId()))
                .isInstanceOf(IllegalStateException.class);
    }

    @ParameterizedTest
    @EnumSource(value = OrderType.class, names = {"EAT_IN", "TAKEOUT"})
    void 주문_타입이_포장_또는_매장식사인_경우_완료상태_전_주문_상태가_서빙완료여야_한다(OrderType orderType) {
        //given
        LocalDateTime orderDateTime = LocalDateTime.now();
        OrderLineItem orderLineItem = createOrderLineItem(menu, 1L, new BigDecimal("1500"), 1L);
        Order order = createOrder("서울", orderTable, orderDateTime, OrderStatus.DELIVERING, orderType, List.of(orderLineItem));

        given(orderRepository.findById(any()))
                .willReturn(Optional.of(order));

        //when, then
        assertThatThrownBy(() -> orderService.complete(order.getId()))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void 주문_타입이_매장식사인_경우_테이블_점유_상태를_해제하고_주문_테이블의_손님수도_0으로_변경한다() {
        //given
        LocalDateTime orderDateTime = LocalDateTime.now();
        OrderLineItem orderLineItem = createOrderLineItem(menu, 1L, new BigDecimal("1500"), 1L);
        Order order = createOrder("서울", orderTable, orderDateTime, OrderStatus.SERVED, OrderType.EAT_IN, List.of(orderLineItem));

        given(orderRepository.findById(any()))
                .willReturn(Optional.of(order));

        //when
        Order result = orderService.complete(order.getId());

        //then
        assertThat(result.getOrderTable().isOccupied()).isEqualTo(false);
        assertThat(result.getOrderTable().getNumberOfGuests()).isEqualTo(0);
    }

    @Test
    void 모든_주문을_조회할_수_있다() {
        //given
        LocalDateTime orderDateTime = LocalDateTime.now();
        OrderLineItem orderLineItem = createOrderLineItem(menu, 1L, new BigDecimal("1500"), 1L);
        Order order1 = createOrder("서울", orderTable, orderDateTime, OrderStatus.SERVED, OrderType.EAT_IN, List.of(orderLineItem));
        Order order2 = createOrder("인천", orderTable, orderDateTime, OrderStatus.WAITING, OrderType.EAT_IN, List.of(orderLineItem));

        List<Order> orders = List.of(order1, order2);

        given(orderRepository.findAll())
                .willReturn(orders);

        //when
        List<Order> result = orderService.findAll();

        //then
        assertThat(result.size()).isEqualTo(orders.size());
    }
}