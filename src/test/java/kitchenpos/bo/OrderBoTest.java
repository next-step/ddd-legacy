package kitchenpos.bo;

import kitchenpos.TestFixtures;
import kitchenpos.dao.MenuDao;
import kitchenpos.dao.OrderDao;
import kitchenpos.dao.OrderLineItemDao;
import kitchenpos.dao.OrderTableDao;
import kitchenpos.model.Order;
import kitchenpos.model.OrderLineItem;
import kitchenpos.model.OrderStatus;
import kitchenpos.model.OrderTable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class OrderBoTest {

    @Mock
    private OrderDao orderDao;
    @Mock
    private MenuDao menuDao;
    @Mock
    private OrderTableDao orderTableDao;
    @Mock
    private OrderLineItemDao orderLineItemDao;

    @InjectMocks
    private OrderBo orderBo;

    private Order orderExpected;

    @Test
    @DisplayName("주문 아이템이 비었을 때")
    void createExceptionByEmptyOrderLineItem() {
        // give
        orderExpected = TestFixtures.createOrderByItemEmpty();
        // when then
        assertThatIllegalArgumentException().isThrownBy(() -> orderBo.create(orderExpected));
    }

    @Test
    @DisplayName("주문한 상품과 메뉴의 상품이 일치하는지 확인 한다.")
    void createExceptionByEqualsMenu() {
        // give
        orderExpected = TestFixtures.createOrderByCompletion();
        given(menuDao.countByIdIn(Arrays.asList(1L)))
                .willReturn(2L);
        // when then
        assertThatIllegalArgumentException().isThrownBy(() -> orderBo.create(orderExpected));
    }

    @Test
    @DisplayName("주문할 때 주문된 테이블은 비어있을 수 없다.")
    void createExceptionByEmptyOrderTable() {
        //give
        OrderTable orderTableExpected = TestFixtures.createOrderTableByEmpty();
        orderExpected = TestFixtures.createOrderByCompletion();

        given(menuDao.countByIdIn(Arrays.asList(1L)))
                .willReturn(1L);
        given(orderTableDao.findById(1L))
                .willReturn(Optional.of(orderTableExpected));

        // when then
        assertThatIllegalArgumentException().isThrownBy(() -> orderBo.create(orderExpected));
    }

    @Test
    @DisplayName("주문을 조회할 수 있다.")
    void getOrder() {
        // give
        OrderLineItem orderLineItemExpected = TestFixtures.createOrderLineItem();
        orderExpected = TestFixtures.createOrderByCooking();
        given(orderDao.findAll())
                .willReturn(Arrays.asList(orderExpected));

        given(orderLineItemDao.findAllByOrderId(1L))
                .willReturn(Arrays.asList(orderLineItemExpected));
        // when
        List<Order> ordersActual = orderBo.list();

        // then
        assertAll("orderExpected test", () ->
                assertAll("orderExpected first id test", () -> {
                    int firstIndex = 0;
                    assertThat(ordersActual.get(firstIndex).getId())
                            .isEqualTo(orderExpected.getId());
                }));
    }

    @Test
    @DisplayName("완료 상태일 때 상태 변화 예외처리")
    void changeOrderStatusExceptionByCompletion() {
        // given
        orderExpected = TestFixtures.createOrderByCompletion();

        given(orderDao.findById(1L))
                .willReturn(Optional.of(orderExpected));

        // when then
        assertThatIllegalArgumentException().isThrownBy(() -> orderBo.changeOrderStatus(1L, orderExpected));
    }

    @Test
    @DisplayName("주문 상태 변화")
    void changeOrderStatus() {
        // give
        given(orderDao.findById(1L))
                .willReturn(Optional.of(new Order()));

        orderExpected = TestFixtures.createOrderByCooking();
        OrderLineItem orderLineItemExpected = TestFixtures.createOrderLineItem();

        given(orderLineItemDao.findAllByOrderId(1L))
                .willReturn(Arrays.asList(orderLineItemExpected));

        Order orderActual = orderBo.changeOrderStatus(1L, orderExpected);

        assertThat(orderActual.getOrderStatus()).isEqualTo(orderExpected.getOrderStatus());
        assertThat(orderActual.getOrderStatus()).isNotEqualTo(new Order().getOrderStatus());
    }
}
