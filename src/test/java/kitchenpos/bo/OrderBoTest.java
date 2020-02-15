package kitchenpos.bo;

import kitchenpos.dao.OrderDao;
import kitchenpos.model.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

@ExtendWith(MockitoExtension.class)
class OrderBoTest {

    @Mock
    private OrderDao orderDao;

    @InjectMocks
    private OrderBo orderBo;

    private Order order;

    @BeforeEach
    void setUp() {
        order = new Order();
        order.setId(1L);
    }

    @Test
    @DisplayName("주문 아이템이 비었을 때")
    void createExceptionByEmptyOrderLineItem() {
        // give
        order.setOrderLineItems(Collections.emptyList());
        // when then
        assertThatIllegalArgumentException().isThrownBy(() -> orderBo.create(order));
    }
}