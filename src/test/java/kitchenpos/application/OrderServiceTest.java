package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.fixture.OrderTestFixture;
import kitchenpos.fixture.ProductTestFixture;
import kitchenpos.infra.KitchenridersClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
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

    OrderLineItem orderLineItem;
    OrderTable orderTable;
    Menu menu;

    Order deliveryOrder;
    Order eatInOrder;
    Order takeOutOrder;

    @BeforeEach
    void setUp() {
        menu = ProductTestFixture.createMenu("메뉴1", BigDecimal.valueOf(10_000L), UUID.randomUUID(), true);
        orderLineItem = OrderTestFixture.createOrderLineItem(menu.getId(), 1L, BigDecimal.valueOf(10_000L));
        orderTable = OrderTestFixture.createOrderTable(true);
        deliveryOrder = OrderTestFixture.createOrder(OrderType.DELIVERY, null, OrderStatus.WAITING, "서울시 강남구 역삼동", orderLineItem);
        eatInOrder = OrderTestFixture.createOrder(OrderType.EAT_IN, orderTable, OrderStatus.WAITING, null, orderLineItem);
        takeOutOrder = OrderTestFixture.createOrder(OrderType.TAKEOUT, null, OrderStatus.WAITING, null, orderLineItem);
    }

    @Test
    void 주문을_등록한다() {
        Order request = new Order();
        request.setType(OrderType.DELIVERY);
        request.setOrderLineItems(Arrays.asList(orderLineItem));
        request.setDeliveryAddress("서울시 강남구 역삼동");
        given(menuRepository.findAllByIdIn(any())).willReturn(Arrays.asList(menu));
        given(menuRepository.findById(any())).willReturn(Optional.of(menu));
        given(orderRepository.save(any())).willReturn(deliveryOrder);

        Order actual = orderService.create(request);
        assertAll(
                () -> assertThat(actual.getId()).isNotNull(),
                () -> assertThat(actual.getType()).isEqualTo(request.getType()),
                () -> assertThat(actual.getStatus()).isEqualTo(OrderStatus.WAITING)
        );
    }

    @Test
    void 주문_타입은_필수값이다() {
        Order request = new Order();
        request.setOrderLineItems(Arrays.asList(orderLineItem));
        request.setOrderTable(orderTable);

        assertThatThrownBy(() -> orderService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 주문_항목은_필수값이다() {
        Order request = new Order();
        request.setType(OrderType.DELIVERY);

        assertThatThrownBy(() -> orderService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 주문_항목의_수량은_0이상이어야_한다() {
        Order request = new Order();
        request.setType(OrderType.DELIVERY);
        orderLineItem.setQuantity(-1L);
        request.setOrderLineItems(Arrays.asList(orderLineItem));
        given(menuRepository.findAllByIdIn(any())).willReturn(Arrays.asList(menu));

        assertThatThrownBy(() -> orderService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 메뉴는_노출상태여야_한다() {
        Order request = new Order();
        request.setType(OrderType.DELIVERY);
        request.setOrderLineItems(Arrays.asList(orderLineItem));
        menu.setDisplayed(false);
        given(menuRepository.findAllByIdIn(any())).willReturn(Arrays.asList(menu));
        given(menuRepository.findById(any())).willReturn(Optional.of(menu));

        assertThatThrownBy(() -> orderService.create(request))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void 메뉴의_가격과_주문_항목의_수량을_곱한_값의_합은_주문_금액과_같아야_한다() {
        Order request = new Order();
        request.setType(OrderType.DELIVERY);
        request.setOrderLineItems(Arrays.asList(orderLineItem));
        menu.setPrice(BigDecimal.valueOf(20_000L));
        given(menuRepository.findAllByIdIn(any())).willReturn(Arrays.asList(menu));
        given(menuRepository.findById(any())).willReturn(Optional.of(menu));

        assertThatThrownBy(() -> orderService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 배달_주문인_경우_배달_주소는_필수값이다() {
        Order request = new Order();
        request.setType(OrderType.DELIVERY);
        request.setOrderLineItems(Arrays.asList(orderLineItem));
        given(menuRepository.findAllByIdIn(any())).willReturn(Arrays.asList(menu));
        given(menuRepository.findById(any())).willReturn(Optional.of(menu));

        assertThatThrownBy(() -> orderService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 매장식사인_경우_오더테이블은_사용상태여야한다() {
        Order request = new Order();
        request.setType(OrderType.EAT_IN);
        request.setOrderLineItems(Arrays.asList(orderLineItem));
        given(menuRepository.findAllByIdIn(any())).willReturn(Arrays.asList(menu));
        given(menuRepository.findById(any())).willReturn(Optional.of(menu));
        orderTable.setOccupied(false);
        given(orderTableRepository.findById(any())).willReturn(Optional.of(orderTable));

        assertThatThrownBy(() -> orderService.create(request))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void 주문을_승인한다() {
        orderLineItem.setMenu(menu);
        deliveryOrder.setOrderLineItems(Arrays.asList(orderLineItem));
        given(orderRepository.findById(any())).willReturn(Optional.of(deliveryOrder));

        orderService.accept(deliveryOrder.getId());

        assertThat(deliveryOrder.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
    }

    @Test
    void 주문_승인은_대기상태에서만_가능하다() {
        deliveryOrder.setStatus(OrderStatus.ACCEPTED);
        given(orderRepository.findById(any())).willReturn(Optional.of(deliveryOrder));

        assertThatThrownBy(() -> orderService.accept(deliveryOrder.getId()))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void 주문을_제공한다() {
        deliveryOrder.setStatus(OrderStatus.ACCEPTED);
        given(orderRepository.findById(any())).willReturn(Optional.of(deliveryOrder));

        Order actual = orderService.serve(deliveryOrder.getId());

        assertThat(actual.getStatus()).isEqualTo(OrderStatus.SERVED);
    }

    @Test
    void 주문_제공상태_변경은_승인상태에서만_가능하다() {
        deliveryOrder.setStatus(OrderStatus.WAITING);
        given(orderRepository.findById(any())).willReturn(Optional.of(deliveryOrder));

        assertThatThrownBy(() -> orderService.serve(deliveryOrder.getId()))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void 배달_주문만_배달중상태로_변경한다() {
        deliveryOrder.setStatus(OrderStatus.SERVED);
        given(orderRepository.findById(any())).willReturn(Optional.of(deliveryOrder));

        Order actual = orderService.startDelivery(deliveryOrder.getId());

        assertThat(actual.getStatus()).isEqualTo(OrderStatus.DELIVERING);
    }

    @Test
    void 전달_완료_상태_배달_주문만_배달중상태로_변경한다() {
        deliveryOrder.setStatus(OrderStatus.WAITING);
        given(orderRepository.findById(any())).willReturn(Optional.of(deliveryOrder));

        assertThatThrownBy(() -> orderService.startDelivery(deliveryOrder.getId()))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void 주문_타입이_배달인_경우만_배달이_가능하다() {
        eatInOrder.setStatus(OrderStatus.SERVED);
        given(orderRepository.findById(any())).willReturn(Optional.of(eatInOrder));

        assertThatThrownBy(() -> orderService.startDelivery(eatInOrder.getId()))
                .isInstanceOf(IllegalStateException.class);
    }


    @Test
    void 배달중인_주문만_배달완료상태로_변경한다() {
        deliveryOrder.setStatus(OrderStatus.DELIVERING);
        given(orderRepository.findById(any())).willReturn(Optional.of(deliveryOrder));

        Order actual = orderService.completeDelivery(deliveryOrder.getId());

        assertThat(actual.getStatus()).isEqualTo(OrderStatus.DELIVERED);
    }

    @Test
    void 배달중이_아니면_배달완료_처리에_실패한다() {
        deliveryOrder.setStatus(OrderStatus.ACCEPTED);
        given(orderRepository.findById(any())).willReturn(Optional.of(deliveryOrder));

        assertThatThrownBy(() -> orderService.completeDelivery(deliveryOrder.getId()))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void 배달완료인_주문만_주문완료_처리가_가능하다() {
        deliveryOrder.setStatus(OrderStatus.DELIVERED);
        given(orderRepository.findById(any())).willReturn(Optional.of(deliveryOrder));

        Order actual = orderService.complete(deliveryOrder.getId());

        assertThat(actual.getStatus()).isEqualTo(OrderStatus.COMPLETED);
    }

    @Test
    void 포장주문은_제공상태에서만_주문완료_처리가_가능하다() {
        takeOutOrder.setStatus(OrderStatus.SERVED);
        given(orderRepository.findById(any())).willReturn(Optional.of(takeOutOrder));

        Order actual = orderService.complete(takeOutOrder.getId());

        assertThat(actual.getStatus()).isEqualTo(OrderStatus.COMPLETED);
    }

    @Test
    void 매장식사_주문은_제공상태에서만_주문완료_처리가_가능하다() {
        eatInOrder.setStatus(OrderStatus.SERVED);
        given(orderRepository.findById(any())).willReturn(Optional.of(eatInOrder));

        Order actual = orderService.complete(eatInOrder.getId());

        assertThat(actual.getStatus()).isEqualTo(OrderStatus.COMPLETED);
    }

    @Test
    void 주문테이블의_주문이_완료상태일때만_미사용상태로_변경_가능하다() {
        eatInOrder.setStatus(OrderStatus.SERVED);
        given(orderRepository.findById(any())).willReturn(Optional.of(eatInOrder));
        given(orderRepository.existsByOrderTableAndStatusNot(any(), any())).willReturn(false);

        Order actual = orderService.complete(eatInOrder.getId());

        assertAll(
                () -> assertThat(actual.getStatus()).isEqualTo(OrderStatus.COMPLETED),
                () -> assertThat(actual.getOrderTable().isOccupied()).isFalse(),
                () -> assertThat(actual.getOrderTable().getNumberOfGuests()).isZero()
        );
    }

    @Test
    void 주문_목록을_조회한다() {
        given(orderRepository.findAll()).willReturn(Arrays.asList(deliveryOrder, takeOutOrder, eatInOrder));

        List<Order> actual = orderService.findAll();
        assertThat(actual).hasSize(3);
    }
}

