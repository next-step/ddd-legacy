package kitchenpos.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.junit.jupiter.api.Assertions.assertAll;

class OrderTest {
    @DisplayName("주문은 주문종류, 상태, 주문시간, 주문세부항목목록, 배달주소, 매장테이블을 가지고 있다.")
    @Test
    void properties() {
        final var orderTable = new OrderTable();
        orderTable.setId(UUID.fromString("11111111-1111-1111-1111-111111111111"));

        final var menu = new Menu();
        menu.setId(UUID.fromString("22222222-2222-2222-2222-222222222222"));

        final var orderLineItem = new OrderLineItem();
        orderLineItem.setSeq(0L);
        orderLineItem.setQuantity(1L);
        orderLineItem.setPrice(new BigDecimal(10000));
        orderLineItem.setMenu(menu);

        final var order = new Order();
        order.setType(OrderType.DELIVERY);
        order.setStatus(OrderStatus.WAITING);
        order.setOrderDateTime(LocalDateTime.parse("2022-09-04T21:40:00"));
        order.setDeliveryAddress("서울시 송파구");
        order.setOrderTable(orderTable);
        order.setOrderLineItems(List.of(orderLineItem));

        assertAll(
                () -> assertThat(order.getType()).isEqualTo(OrderType.DELIVERY),
                () -> assertThat(order.getStatus()).isEqualTo(OrderStatus.WAITING),
                () -> assertThat(order.getOrderDateTime()).isEqualTo(LocalDateTime.parse("2022-09-04T21:40:00")),
                () -> assertThat(order.getDeliveryAddress()).isEqualTo("서울시 송파구"),
                () -> assertThat(order.getOrderTable().getId()).isEqualTo(UUID.fromString("11111111-1111-1111-1111-111111111111")),
                () -> assertThat(order.getOrderLineItems())
                        .extracting("seq", "quantity", "price", "menu.id")
                        .containsExactly(tuple(0L, 1L, new BigDecimal(10000), UUID.fromString("22222222-2222-2222-2222-222222222222")))
        );
    }

    @DisplayName("주문종류는 배달, 포장, 매장식사가 있다.")
    @ParameterizedTest(name = "{0}은 주문종류이다.")
    @EnumSource(OrderType.class)
    void type(OrderType type) {
        final var allTypes = List.of(
                "DELIVERY",
                "TAKEOUT",
                "EAT_IN"
        );

        assertThat(type.name()).isIn(allTypes);
    }

    @DisplayName("상태는 `대기중`, `접수됨`, `제공됨`, `배달중`, `배달됨`, `완료됨`이 있다. ")
    @ParameterizedTest(name = "{0}은 상태이다.")
    @EnumSource(OrderStatus.class)
    void status(OrderStatus status) {
        final var allStatuses = List.of(
                "WAITING",
                "ACCEPTED",
                "SERVED",
                "DELIVERING",
                "DELIVERED",
                "COMPLETED"
        );

        assertThat(status.name()).isIn(allStatuses);
    }
}
