package kitchenpos.bo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import kitchenpos.dao.OrderDao;
import kitchenpos.dao.OrderTableDao;
import kitchenpos.model.OrderStatus;
import kitchenpos.model.OrderTable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class TableBoTest {

    @Mock
    private OrderDao orderDao;

    @Mock
    private OrderTableDao orderTableDao;

    @InjectMocks
    private TableBo tableBo;

    private OrderTable orderTable;

    @BeforeEach
    void setUp() {
        orderTable = new OrderTable();
        orderTable.setEmpty(false);
        when(orderTableDao.save(orderTable)).thenReturn(orderTable);
        when(orderTableDao.findById(anyLong())).thenReturn(Optional.of(orderTable));
        when(orderDao.existsByOrderTableIdAndOrderStatusIn(1L, Arrays
            .asList(OrderStatus.COOKING.name(), OrderStatus.MEAL.name())))
            .thenReturn(false);
    }

    @DisplayName("테이블을 생성할 수 있다.")
    @Test
    void create() {
        assertThat(tableBo.create(orderTable)).isEqualTo(orderTable);
    }

    @DisplayName("테이블 목록을 조회할 수 있다.")
    @Test
    void list() {
        when(orderTableDao.findAll()).thenReturn(new ArrayList<>());
        assertThat(tableBo.list()).isEmpty();
    }

    @DisplayName("테이블의 상태를 `이용가능` 으로 변경할 수 있다.")
    @Test
    void changeEmpty() {
        assertThat(tableBo.changeEmpty(1L, orderTable)).isEqualTo(orderTable);
    }

    @DisplayName("상태를 변경하려는 테이블이 존재하지 않는 경우, 예외를 발생시킨다.")
    @Test
    void exceptionWithOrderTable() {
        when(orderTableDao.findById(anyLong())).thenThrow(IllegalArgumentException.class);
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> tableBo.changeEmpty(1L, orderTable));
    }

    @DisplayName("상태를 변경하려는 테이블이 이미 테이블그룹에 속해있는 경우, 예외를 발생시킨다.")
    @Test
    void exceptionWithOrderTableStatus() {
        orderTable.setTableGroupId(1L);
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> tableBo.changeEmpty(1L, orderTable));
    }

    @DisplayName("상태를 변경하려는 테이블의 모든 주문이 `완료` 상태가 아닌 경우, 예외를 발생시킨다.")
    @Test
    void exceptionWithOrderStatus() {
        when(orderDao.existsByOrderTableIdAndOrderStatusIn(1L,
            Arrays.asList(OrderStatus.COOKING.name(), OrderStatus.MEAL.name()))).thenReturn(true);

        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> tableBo.changeEmpty(1L, orderTable));
    }

    @DisplayName("테이블의 손님 수를 변경할 수 있다.")
    @Test
    void changeNumberOfGuests() {
        orderTable.setNumberOfGuests(1);
        assertThat(tableBo.changeNumberOfGuests(1L, orderTable)).isEqualTo(orderTable);
    }

    @DisplayName("변경하려는 손님 수가 `0`보다 작은 경우, 예외를 발생시킨다.")
    @Test
    void exceptionWithNumberOfGuests() {
        orderTable.setNumberOfGuests(-1);
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> tableBo.changeNumberOfGuests(1L, orderTable));
    }

    @DisplayName("변경하려는 테이블이 존재하지 않는 경우, 예외를 발생시킨다.")
    @Test
    void exceptionWithNonExistOrderTable() {
        when(orderTableDao.findById(anyLong())).thenThrow(IllegalArgumentException.class);
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> tableBo.changeNumberOfGuests(1L, orderTable));
    }

    @DisplayName("변경하려는 테이블이 `이용가능` 상태인 경우, 예외를 발생시킨다.")
    @Test
    void exceptionWithNotEmptyOrderTable() {
        orderTable.setEmpty(true);
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> tableBo.changeNumberOfGuests(1L, orderTable));
    }
}
