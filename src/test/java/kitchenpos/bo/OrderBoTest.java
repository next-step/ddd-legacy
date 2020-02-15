package kitchenpos.bo;

import kitchenpos.dao.MenuDao;
import kitchenpos.dao.OrderDao;
import kitchenpos.model.Order;
import kitchenpos.model.OrderLineItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class OrderBoTest {

    @Mock
    private OrderDao orderDao;
    @Mock
    private MenuDao menuDao;

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

    @Test
    @DisplayName("주문한 상품과 메뉴의 상품이 일치하는지 확인 한다.")
    void createExceptionByEqualsMenu() {
        // give
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenuId(1L);
        order.setOrderLineItems(Arrays.asList(orderLineItem));
        given(menuDao.countByIdIn(Arrays.asList(1L)))
                .willReturn(2L);
        // when then
        assertThatIllegalArgumentException().isThrownBy(() -> orderBo.create(order));
    }
}