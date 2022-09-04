package kitchenpos.application;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import kitchenpos.domain.Menu;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@DisplayName("주문 접수")
class OrderServiceAcceptTest extends OrderServiceTestSupport {
    @DisplayName("주문이 존재해야 한다.")
    @Test
    void orderNotFound() {
        //given
        final var orderId = UUID.fromString("11111111-1111-1111-1111-111111111111");

        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        // when
        assertThatThrownBy(() -> testService.accept(orderId))
                // then
                .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("대기중인 주문이어야 한다.")
    @ParameterizedTest(name = "주문상태가 {0}이 아닌 WAITING이어야 한다.")
    @EnumSource(value = OrderStatus.class, names = "WAITING", mode = EnumSource.Mode.EXCLUDE)
    void shouldBeWaiting(OrderStatus statusBeforeAccepted) {
        //given
        final var orderId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        final var order = new Order();
        order.setStatus(statusBeforeAccepted);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        // when
        assertThatThrownBy(() -> testService.accept(orderId))
                // then
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("배달 주문인 경우에만 배달 요청을 보낸다.")
    @ParameterizedTest(name = "{0} 주문인 경우 배달 요청을 {2}회 보낸다.")
    @CsvSource({
            "DELIVERY,'서울시 송파구',1",
            "TAKEOUT,null,0",
            "EAT_IN,null,0"
    })
    void deliveryOrderShouldOnlyRequestDelivery(
            OrderType type,
            String deliveryAddress,
            int invocationCount
    ) {
        //given
        final var menu = new Menu();
        menu.setPrice(new BigDecimal(10000));

        final var orderLineItem = new OrderLineItem();
        orderLineItem.setMenu(menu);
        orderLineItem.setQuantity(1L);

        final var orderId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        final var order = new Order();
        order.setId(orderId);
        order.setType(type);
        order.setStatus(OrderStatus.WAITING);
        order.setOrderLineItems(List.of(orderLineItem));
        order.setDeliveryAddress(deliveryAddress);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        // when
        testService.accept(orderId);

        // then
        verify(kitchenridersClient, times(invocationCount))
                .requestDelivery(orderId, new BigDecimal(10000), deliveryAddress);
    }

    @DisplayName("대기중인 주문은 접수할 수 있다.")
    @ParameterizedTest(name = "{0}인 주문을 접수할 수 있다.")
    @CsvSource({
            "DELIVERY,'서울시 송파구'",
            "TAKEOUT,null",
            "EAT_IN,null"
    })
    void accept(
            OrderType type,
            String deliveryAddress
    ) {
        //given
        final var menu = new Menu();
        menu.setPrice(new BigDecimal(10000));

        final var orderLineItem = new OrderLineItem();
        orderLineItem.setMenu(menu);
        orderLineItem.setQuantity(1L);

        final var orderId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        final var order = new Order();
        order.setId(orderId);
        order.setType(type);
        order.setStatus(OrderStatus.WAITING);
        order.setOrderLineItems(List.of(orderLineItem));
        order.setDeliveryAddress(deliveryAddress);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        // when
        testService.accept(orderId);

        // then
        assertThat(order.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
    }
}
