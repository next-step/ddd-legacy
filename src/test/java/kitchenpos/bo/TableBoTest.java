package kitchenpos.bo;

import kitchenpos.dao.OrderDao;
import kitchenpos.dao.OrderTableDao;
import kitchenpos.model.OrderTable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class TableBoTest {

    @Mock
    private OrderTableDao orderTableDao;
    @Mock
    private OrderDao orderDao;

    @InjectMocks
    private TableBo tableBo;

    private OrderTable orderTable;

    @BeforeEach
    void setUp() {
        orderTable = new OrderTable();
        orderTable.setId(1L);
        orderTable.setEmpty(true);
        orderTable.setNumberOfGuests(0);
    }

    @Test
    @DisplayName("주문 테이블 생성")
    void create() {
        // give
        OrderTable orderTableExpected = new OrderTable();
        given(orderTableDao.save(orderTableExpected))
                .willReturn(orderTableExpected);
        // when
        OrderTable orderTableActual = tableBo.create(orderTableExpected);
        // then
        assertThat(orderTableActual.getId()).isEqualTo(orderTableExpected.getId());
        assertThat(orderTableActual.getTableGroupId()).isEqualTo(orderTableExpected.getTableGroupId());
    }

    @Test
    @DisplayName("주문 테이블은 테이블이 비었는지 안비었는지 알 수 있다.")
    void getOrderTable() {
        // give
        given(orderTableDao.findAll())
                .willReturn(Arrays.asList(orderTable));
        // when
        List<OrderTable> orderTablesActual = tableBo.list();
        // then
        assertThat(orderTablesActual.get(0).getId()).isEqualTo(orderTable.getId());
    }

    @Test
    @DisplayName("주문 테이블에 손님이 있을 때 상태를 변경할 수 없다.")
    void changeOrderTableStatus() {
        // give
        given(orderTableDao.findById(1L))
                .willReturn(Optional.ofNullable(orderTable));
        given(orderDao.existsByOrderTableIdAndOrderStatusIn(1L, Arrays.asList("COOKING", "MEAL")))
                .willReturn(true);
        // when then
        assertThatIllegalArgumentException().isThrownBy(() -> tableBo.changeEmpty(orderTable.getId(), new OrderTable()));
    }

    @Test
    @DisplayName("주문 테이블의 손님 수가 0명 이상이다.")
    void changeOrderTableGuestNumberByOverZero() {
        // when then
        assertThatIllegalArgumentException().isThrownBy(() -> tableBo.changeNumberOfGuests(1L, orderTable));
    }

    @Test
    @DisplayName("주문 테이블이 비어있는데 손님이 앉아 있을 수 없다.")
    void changeOrderTableByIsEmpty() {
        // give
        given(orderTableDao.findById(1L))
                .willReturn(Optional.ofNullable(orderTable));
        assertThatIllegalArgumentException().isThrownBy(() -> tableBo.changeNumberOfGuests(1L, new OrderTable()));
    }
}
