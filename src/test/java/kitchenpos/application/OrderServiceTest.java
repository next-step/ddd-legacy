package kitchenpos.application;

import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Sql({"/truncate-menu-integration.sql", "/insert-order-integration.sql"})
@SpringBootTest
class OrderServiceTest {
    @Autowired
    private OrderService sut;

    @DisplayName("주문을 생성할 수 있다.")
    @Test
    void create() {
        final OrderType type = OrderType.EAT_IN;
        final List<OrderLineItem> orderLineItems = List.of(
                new OrderLineItem(1L, "f59b1e1c-b145-440a-aa6f-6095a0e2d63b", new BigDecimal("16000")));
        final String deliveryAddress = "주소";
        Order request = new Order(type, orderLineItems, deliveryAddress, "8d710043-29b6-420e-8452-233f5a035520");

        final Order response = sut.create(request);

        assertThat(response).isNotNull();
    }

}
