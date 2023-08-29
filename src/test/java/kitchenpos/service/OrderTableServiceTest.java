package kitchenpos.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import kitchenpos.application.OrderTableService;
import kitchenpos.domain.OrderTable;

@SpringBootTest
public class OrderTableServiceTest {

    @Autowired
    private OrderTableService orderTableService;

    @Test
    void 주문테이블_생성_실패__이름이_null() {
        OrderTable request = new OrderTable();
        request.setName(null);

        assertThatThrownBy(() -> orderTableService.create(request));
    }

    @Test
    void 주문테이블_생성_실패__이름이_비어있음() {
        OrderTable request = new OrderTable();
        request.setName("");

        assertThatThrownBy(() -> orderTableService.create(request));
    }
}
