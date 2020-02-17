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
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class TableBoTest {

    @Mock
    private OrderDao orderDao;

    @Mock
    private OrderTableDao orderTableDao;

    @InjectMocks
    private TableBo tableBo;

    private OrderTable expectedOrderTable = null;

    @BeforeEach
    void setUp() {
        expectedOrderTable = new OrderTable();
        expectedOrderTable.setId(1L);
        expectedOrderTable.setNumberOfGuests(4);

    }

    @DisplayName("주문 테이블을 생성한다.")
    @Test
    void createOrderTable() {
        given(orderTableDao.save(any(OrderTable.class))).willReturn(expectedOrderTable);

        OrderTable actualOrderTable = tableBo.create(expectedOrderTable);

        assertThat(actualOrderTable).isEqualTo(expectedOrderTable);
    }


    @DisplayName("주문 테이블 목록을 불러온다.")
    @Test
    void getOrderTables() {
        expectedOrderTable.setId(1L);
        given(orderTableDao.findAll()).willReturn(Arrays.asList(expectedOrderTable));

        List<OrderTable> actual = tableBo.list();
        assertThat(actual).isEqualTo(orderTableDao.findAll());

    }

    @DisplayName("존재하지 않는 주문테이블은 비울수 없다.")
    @Test
    void shouldThrowIllegalArgumentExceptionChangeOrderTable() {
        given(orderTableDao.findById(anyLong())).willReturn(Optional.empty());

        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> tableBo.changeEmpty(expectedOrderTable.getId(), expectedOrderTable));

    }

    @DisplayName("주문 테이블의 그룹이 존재하면 비울수 없다.")
    @Test
    void shouldThrowIllegalArgumentException() {
        expectedOrderTable.setTableGroupId(1L);
        given(orderTableDao.findById(anyLong())).willReturn(Optional.of(expectedOrderTable));

        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> tableBo.changeEmpty(expectedOrderTable.getId(), expectedOrderTable));

    }

    @DisplayName("주문 테이블의 상태가 특정 상태 값에 포함된다면 비울수 없다.")
    @Test
    void shouldThrowIllegalArgumentExceptionInOrderStatusIn() {
        given(orderTableDao.findById(anyLong())).willReturn(Optional.of(expectedOrderTable));
        given(orderDao.existsByOrderTableIdAndOrderStatusIn(anyLong(), anyList())).willReturn(true);

        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> tableBo.changeEmpty(expectedOrderTable.getId(), expectedOrderTable));
    }

    @DisplayName("주문 테이블을 비운다.")
    @Test
    void canChangeOrderTable() {
        given(orderTableDao.findById(anyLong())).willReturn(Optional.of(expectedOrderTable));
        given(orderDao.existsByOrderTableIdAndOrderStatusIn(anyLong(), anyList())).willReturn(false);
        given(orderTableDao.save(any(OrderTable.class))).willReturn(expectedOrderTable);

        OrderTable actualOrderTable = tableBo.changeEmpty(1L, expectedOrderTable);

        assertThat(actualOrderTable).isEqualTo(expectedOrderTable);
    }

    @DisplayName("주문테이블의 인원을 변경한다.")
    @Test
    void canChangeNumberOfGuests() {
        expectedOrderTable.setNumberOfGuests(3);
        given(orderTableDao.findById(anyLong())).willReturn(Optional.of(expectedOrderTable));
        given(orderTableDao.save(any(OrderTable.class))).willReturn(expectedOrderTable);

        OrderTable actualOrderTable = tableBo.changeNumberOfGuests(1L, expectedOrderTable);

        assertThat(actualOrderTable).isEqualTo(expectedOrderTable);
    }

    @DisplayName("주문테이블의 인원은 0명 이상이여야한다.")
    @Test
    void shouldThrowIllegalArgumentExceptionWhenGuestOfNumberLessThan0() {
        expectedOrderTable.setNumberOfGuests(0);

        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> tableBo.changeNumberOfGuests(expectedOrderTable.getId(), expectedOrderTable));
    }

    @DisplayName("없는 테이블의 인원수를 변경할수 없다.")
    @Test
    void shouldThrowIllegalArgumentExceptionForNoneOrderTable() {
        given(orderTableDao.findById(anyLong())).willReturn(Optional.empty());

        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> tableBo.changeNumberOfGuests(expectedOrderTable.getId(), expectedOrderTable));
    }


    @DisplayName("빈 테이블은 또 비울수 없다.")
    @Test
    void shouldThrowIllegalArgumentExceptionForEmptyOrderTable() {
        expectedOrderTable.setEmpty(true);
        given(orderTableDao.findById(anyLong())).willReturn(Optional.of(expectedOrderTable));

        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> tableBo.changeNumberOfGuests(expectedOrderTable.getId(), expectedOrderTable));
    }
}
