package kitchenpos.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import kitchenpos.application.OrderService;
import kitchenpos.domain.Order;

@SpringBootTest
public class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @Test
    void 주문_생성_실패__주문타입이_null() {
        Order request = OrderFixture.builder()
                .type(null)
                .build();

        assertThatThrownBy(() -> orderService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 주문_생성_실패__주문항목이_null() {
        Order request = OrderFixture.builder()
                .orderLineItem(null)
                .build();

        assertThatThrownBy(() -> orderService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 주문_생성_실패__주문항목이_비어있음() {
        Order request = OrderFixture.builder()
                .orderLineItem(List.of())
                .build();

        assertThatThrownBy(() -> orderService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
