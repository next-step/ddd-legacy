package kitchenpos.application;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import kitchenpos.domain.OrderType;
import kitchenpos.fixture.MenuFixture;
import kitchenpos.fixture.OrderFixture;
import kitchenpos.fixture.OrderLineItemFixture;
import kitchenpos.fixture.OrderTableFixture;
import kitchenpos.infra.KitchenridersClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    OrderRepository orderRepository;
    @Mock
    MenuRepository menuRepository;
    @Mock
    OrderTableRepository orderTableRepository;
    @Mock
    KitchenridersClient kitchenridersClient;
    @InjectMocks
    OrderService orderService;

    private OrderTable orderTable;
    private Menu menu;
    private OrderLineItem orderLineItem;
    private String deliveryAddress;

    @BeforeEach
    void setUp() {
        orderTable = OrderTableFixture.create("주문테이블", 1, true);
        menu = MenuFixture.createDefaultWithNameAndPrice("메뉴", BigDecimal.valueOf(20000));
        orderLineItem = OrderLineItemFixture.create(menu, BigDecimal.valueOf(20000), 1);
        deliveryAddress = "배달주소";
    }

    @DisplayName("주문 시 필수 값 확인")
    @Test
    public void 주문필수값_체크() throws Exception {
        Order order = OrderFixture.create(null, Optional.of(orderTable),
            Optional.of(deliveryAddress),
            List.of(orderLineItem));

        assertThatThrownBy(() -> orderService.create(order)).isInstanceOf(
            IllegalArgumentException.class);
    }

    @DisplayName("주문 시 필수 값 확인")
    @Test
    public void 주문필수값_품목() throws Exception {
        Order order = OrderFixture.create(OrderType.DELIVERY, Optional.of(orderTable),
            Optional.of(deliveryAddress),
            null);

        assertThatThrownBy(() -> orderService.create(order)).isInstanceOf(
            IllegalArgumentException.class);
    }

    @DisplayName("주문 시 필수 값 확인")
    @Test
    public void 주문필수값_활성화상태() throws Exception {
        Order order = OrderFixture.create(OrderType.DELIVERY, Optional.of(orderTable),
            Optional.of(deliveryAddress),
            List.of(orderLineItem));
        menu.setDisplayed(false);

        assertThatThrownBy(() -> orderService.create(order)).isInstanceOf(
            IllegalArgumentException.class);
    }

    @DisplayName("주문 시 필수 값 확인")
    @Test
    public void 주문필수값_가격체크() throws Exception {
        Order order = OrderFixture.create(OrderType.DELIVERY, Optional.of(orderTable),
            Optional.of(deliveryAddress),
            List.of(orderLineItem));
        menu.setPrice(BigDecimal.valueOf(10000));

        assertThatThrownBy(() -> orderService.create(order)).isInstanceOf(
            IllegalArgumentException.class);
    }

    @DisplayName("주문 시 상태 값이 대기중이 된다.")
    @Test
    public void 주문생성_상태값() throws Exception {
        Order order = OrderFixture.create(OrderType.TAKEOUT, Optional.of(orderTable),
            Optional.of(deliveryAddress),
            List.of(orderLineItem));
        when(menuRepository.findAllByIdIn(any())).thenReturn(List.of(menu));
        when(menuRepository.findById(any())).thenReturn(Optional.of(menu));
        when(orderRepository.save(any())).thenReturn(order);

        order = orderService.create(order);

        assertThat(order.getStatus()).isEqualTo(OrderStatus.WAITING);
    }

    @DisplayName("주문이 대기중일 경우 승인이 가능")
    @Test
    public void 주문승인() throws Exception {
        Order order = OrderFixture.create(OrderType.TAKEOUT, Optional.of(orderTable),
            Optional.of(deliveryAddress),
            List.of(orderLineItem));
        order.setStatus(OrderStatus.WAITING);
        when(orderRepository.findById(any())).thenReturn(Optional.of(order));

        orderService.accept(order.getId());

        assertThat(order.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
    }

    @DisplayName("주문이 승인된 경우 메뉴를 제공할수 있다.")
    @Test
    public void 메뉴제공() throws Exception {
        Order order = OrderFixture.create(OrderType.TAKEOUT, Optional.of(orderTable),
            Optional.of(deliveryAddress),
            List.of(orderLineItem));
        order.setStatus(OrderStatus.ACCEPTED);
        when(orderRepository.findById(any())).thenReturn(Optional.of(order));

        orderService.serve(order.getId());

        assertThat(order.getStatus()).isEqualTo(OrderStatus.SERVED);
    }

    @DisplayName("배달 주문일 경우 주문품목의 수량이 0보다 커야 한다.")
    @Test
    public void 배달주문_주문품목수량체크() throws Exception {
        orderLineItem.setQuantity(-1);
        Order order = OrderFixture.create(OrderType.DELIVERY, Optional.of(orderTable),
            Optional.of(deliveryAddress),
            List.of(orderLineItem));
        when(menuRepository.findAllByIdIn(any())).thenReturn(List.of(menu));

        assertThatThrownBy(() -> orderService.create(order)).isInstanceOf(
            IllegalArgumentException.class);
    }

    @DisplayName("배달 주문일 경우 배달 주소가 필수 값이다.")
    @Test
    public void 배달주문_주소체크() throws Exception {
        Order order = OrderFixture.create(OrderType.DELIVERY, Optional.of(orderTable),
            Optional.empty(),
            List.of(orderLineItem));
        when(menuRepository.findAllByIdIn(any())).thenReturn(List.of(menu));
        when(menuRepository.findById(any())).thenReturn(Optional.of(menu));

        assertThatThrownBy(() -> orderService.create(order)).isInstanceOf(
            IllegalArgumentException.class);
    }

    @DisplayName("배달 주문일 경우 주문메뉴가 제공되고 나서 배달중으로 상태를 변경할 수 있다.")
    @Test
    public void 배달주문_배달중() throws Exception {
        Order order = OrderFixture.create(OrderType.DELIVERY, Optional.of(orderTable),
            Optional.empty(),
            List.of(orderLineItem));
        order.setStatus(OrderStatus.SERVED);
        when(orderRepository.findById(any())).thenReturn(Optional.of(order));

        order = orderService.startDelivery(order.getId());

        assertThat(order.getStatus()).isEqualTo(OrderStatus.DELIVERING);
    }

    @DisplayName("배달 주문일 경우 배달중일때 배달완료로 상태를 변경할 수 있다.")
    @Test
    public void 배달주문_배달완료() throws Exception {
        Order order = OrderFixture.create(OrderType.DELIVERY, Optional.of(orderTable),
            Optional.empty(),
            List.of(orderLineItem));
        order.setStatus(OrderStatus.DELIVERING);
        when(orderRepository.findById(any())).thenReturn(Optional.of(order));

        order = orderService.completeDelivery(order.getId());

        assertThat(order.getStatus()).isEqualTo(OrderStatus.DELIVERED);
    }

    @DisplayName("배달 주문일 경우 배달완료일때 주문완료 상태를 변경할 수 있다.")
    @Test
    public void 배달주문_주문완료() throws Exception {
        Order order = OrderFixture.create(OrderType.DELIVERY, Optional.of(orderTable),
            Optional.empty(),
            List.of(orderLineItem));
        order.setStatus(OrderStatus.DELIVERED);
        when(orderRepository.findById(any())).thenReturn(Optional.of(order));

        order = orderService.complete(order.getId());

        assertThat(order.getStatus()).isEqualTo(OrderStatus.COMPLETED);
    }

    @DisplayName("포장 주문일 경우 주문품목의 수량이 0보다 커야 한다.")
    @Test
    public void 포장주문_주문품목수량체크() throws Exception {
        orderLineItem.setQuantity(-1);
        Order order = OrderFixture.create(OrderType.TAKEOUT, Optional.of(orderTable),
            Optional.of(deliveryAddress),
            List.of(orderLineItem));
        when(menuRepository.findAllByIdIn(any())).thenReturn(List.of(menu));

        assertThatThrownBy(() -> orderService.create(order)).isInstanceOf(
            IllegalArgumentException.class);
    }

    @DisplayName("포장 주문 주문 완료")
    @Test
    public void 포장주문_주문완료() throws Exception {
        Order order = OrderFixture.create(OrderType.TAKEOUT, Optional.of(orderTable),
            Optional.of(deliveryAddress),
            List.of(orderLineItem));
        order.setStatus(OrderStatus.SERVED);
        when(orderRepository.findById(any())).thenReturn(Optional.of(order));

        order = orderService.complete(order.getId());

        assertThat(order.getStatus()).isEqualTo(OrderStatus.COMPLETED);
    }

    @DisplayName("매징 주문일 경우 테이블이 사용중이면 주문할 수 없다.")
    @Test
    public void 매장주문_테이블사용중_주문불가() throws Exception {
        orderTable.setOccupied(false);
        Order order = OrderFixture.create(OrderType.EAT_IN, Optional.of(orderTable),
            Optional.of(deliveryAddress),
            List.of(orderLineItem));
        when(menuRepository.findAllByIdIn(any())).thenReturn(List.of(menu));
        when(menuRepository.findById(any())).thenReturn(Optional.of(menu));
        when(orderTableRepository.findById(any())).thenReturn(Optional.of(orderTable));

        assertThatThrownBy(() -> orderService.create(order)).isInstanceOf(
            IllegalStateException.class);
    }

    @DisplayName("매징 주문일 경우 주문 완료 후 테이블을 청소한다.")
    @Test
    public void 매장주문_주문완료_테이블청소() throws Exception {
        Order order = OrderFixture.create(OrderType.EAT_IN, Optional.of(orderTable),
            Optional.of(deliveryAddress),
            List.of(orderLineItem));
        order.setStatus(OrderStatus.SERVED);
        when(orderRepository.findById(any())).thenReturn(Optional.of(order));
        when(orderRepository.existsByOrderTableAndStatusNot(any(), any())).thenReturn(false);

        order = orderService.complete(order.getId());

        assertThat(order.getOrderTable().isOccupied()).isFalse();
    }


}