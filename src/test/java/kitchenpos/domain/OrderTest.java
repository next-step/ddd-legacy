package kitchenpos.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static kitchenpos.fixture.OrderLineItemFixture.createOrderLineItem;
import static kitchenpos.fixture.OrderTableFixture.createOrderTable;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class OrderTest {
    @DisplayName("주문 생성")
    @Test
    void test1() {
        final UUID id = UUID.randomUUID();
        final OrderType type = OrderType.DELIVERY;
        final OrderStatus status = OrderStatus.WAITING;
        final LocalDateTime orderDateTime = LocalDateTime.now();
        final List<OrderLineItem> orderLineItems = List.of(createOrderLineItem());
        final String deliveryAddress = "청주시";
        final OrderTable orderTable = createOrderTable();
        final UUID orderTableId = orderTable.getId();

        final Order order = new Order();
        order.setId(id);
        order.setType(type);
        order.setStatus(status);
        order.setOrderDateTime(orderDateTime);
        order.setOrderLineItems(orderLineItems);
        order.setDeliveryAddress(deliveryAddress);
        order.setOrderTable(orderTable);
        order.setOrderTableId(orderTableId);

        assertAll(
                () -> assertThat(order.getId()).isEqualTo(id),
                () -> assertThat(order.getType()).isEqualTo(type),
                () -> assertThat(order.getStatus()).isEqualTo(status),
                () -> assertThat(order.getOrderDateTime()).isEqualTo(orderDateTime),
                () -> assertThat(order.getOrderLineItems()).isEqualTo(orderLineItems),
                () -> assertThat(order.getDeliveryAddress()).isEqualTo(deliveryAddress),
                () -> assertThat(order.getOrderTable()).isEqualTo(orderTable),
                () -> assertThat(order.getOrderTableId()).isEqualTo(orderTableId)
        );
    }

}