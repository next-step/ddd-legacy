package kitchenpos.order.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("주문")
class OrderTest {

    @DisplayName("주문 타입은 null 일 수 없다.")
    @Test
    void requireOrderType() {
        List<OrderLineItem> orderLineItems = orderLineItems();
        assertThatThrownBy(() -> new Order(null, orderLineItems))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("주문 타입을 입력해주세요.");
    }

    @DisplayName("주문 항목은 비어 있을 수 없다.")
    @Test
    void orderLineItemsNotNull() {
        assertThatThrownBy(() -> new Order(OrderType.TAKEOUT, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("주문 항목은 비어 있을 수 없습니다.");
    }

    @DisplayName("주문을 수락 할 수 있다.")
    @Test
    void acceptSuccess() {
        List<OrderLineItem> orderLineItems = orderLineItems();
        Order order = new Order(OrderType.TAKEOUT, orderLineItems);
        order.accept();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
    }

    @DisplayName("WAITING 상태가 아니면 접수를 받을 수 없다.")
    @Test
    void acceptFail() {
        List<OrderLineItem> orderLineItems = orderLineItems();
        Order order = new Order(OrderType.TAKEOUT, orderLineItems);
        order.accept();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
        assertThatThrownBy(order::accept)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("WAITING 상태만 접수가능합니다.");
    }

    private static List<OrderLineItem> orderLineItems() {
        List<OrderLineItem> orderLineItems = new ArrayList<>();
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItems.add(orderLineItem);
        return orderLineItems;
    }
}
