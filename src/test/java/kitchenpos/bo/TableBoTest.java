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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TableBoTest {
    @Mock private OrderDao orderDao;
    @Mock private OrderTableDao orderTableDao;
    @InjectMocks private TableBo tableBo;

    private OrderTable orderTable1;
    private OrderTable orderTable2;
    private List<OrderTable> orderTableList;
    private Optional<OrderTable> optionalOrderTable;

    @BeforeEach
    void setup() {
        orderTable1 = new OrderTable();
        orderTable1.setId(1L);
        orderTable1.setTableGroupId(null);
        orderTable1.setEmpty(false);
        orderTable1.setNumberOfGuests(2);

        orderTable2 = new OrderTable();
        orderTable2.setId(2L);
        orderTable2.setTableGroupId(null);
        orderTable2.setEmpty(false);
        orderTable2.setNumberOfGuests(2);

        orderTableList = new ArrayList<>();
        orderTableList.add(orderTable1);
        orderTableList.add(orderTable2);

    }

    @DisplayName("테이블 정보를 입력할 수 있다. (테이블그룹, 손님 수, 테이블상태(비었는지 아닌지))")
    @Test
    void create() {
        when(orderTableDao.save(any(OrderTable.class))).thenReturn(orderTable1);
        OrderTable result = tableBo.create(orderTable1);
        assertThat(result.getId()).isEqualTo(orderTable1.getId());
    }

    @DisplayName("테이블 목록을 볼 수 있다.")
    @Test
    void list() {
        when(orderTableDao.findAll()).thenReturn(orderTableList);
        List<OrderTable> result = tableBo.list();
        assertThat(result.size()).isEqualTo(2);
    }

    @DisplayName("테이블상태를 변경할 수 있다.")
    @Test
    void changeEmpty() {
        when(orderTableDao.findById(anyLong()))
                .thenReturn(optionalOrderTable.of(orderTable1));
        when(orderDao.existsByOrderTableIdAndOrderStatusIn(anyLong(),any(List.class)))
                .thenReturn(false);
        when(orderTableDao.save(any(OrderTable.class)))
                .thenReturn(orderTable1);
        OrderTable result = tableBo.changeEmpty(orderTable1.getId(), orderTable1);
        assertThat(result.isEmpty()).isFalse();
    }

    @DisplayName("주문이 없는 테이블의 상태를 변경할 수 없다.")
    @Test
    void changeEmptyWithoutOrderTable() {
        when(orderTableDao.findById(anyLong()))
                .thenReturn(Optional.empty());
        Throwable thrown = catchThrowable(() ->{
            tableBo.changeEmpty(orderTable1.getId(), orderTable1);
        });
        assertThat(thrown).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("테이블그룹에 포함되지 않은 테이블은 변경할 수 없다.")
    @Test
    void changeEmptyExclusiveTableGroup() {
        orderTable1.setTableGroupId(null);
        Throwable thrown = catchThrowable(() ->{
            tableBo.changeEmpty(orderTable1.getId(), orderTable1);
        });
        assertThat(thrown).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("조리중이거나 손님이 식사중인 테이블의 상태를 변경할 수 없다.")
    @Test
    void changeEmptyCookingOrMeal() {
        orderTable1.setTableGroupId(null);
        Throwable thrown = catchThrowable(() ->{
            tableBo.changeEmpty(orderTable1.getId(), orderTable1);
        });
        assertThat(thrown).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("테이블에 손님수를 변경할 수 있다.")
    @Test
    void changeNumberOfGuests() {
        when(orderTableDao.findById(anyLong()))
                .thenReturn(optionalOrderTable.of(orderTable1));
        when(orderTableDao.save(any(OrderTable.class)))
                .thenReturn(orderTable1);
        OrderTable result = tableBo.changeNumberOfGuests(orderTable1.getId(), orderTable1);
        assertThat(result.getNumberOfGuests()).isEqualTo(2);
    }

    @DisplayName("손님수는 반드시 양수여야 한다.")
    @Test
    void changeNumberOfGuestsByNegativeNumber() {
        orderTable1.setNumberOfGuests(-1);
        Throwable thrown = catchThrowable(() ->{
            tableBo.changeNumberOfGuests(orderTable1.getId(), orderTable1);
        });
        assertThat(thrown).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("주문이 있는 테이블의 손님수만 변경할 수 있다.")
    @Test
    void changeNumberOfGuestsTableWithOrder() {
        when(orderTableDao.findById(anyLong()))
                .thenReturn(Optional.empty());
        Throwable thrown = catchThrowable(() ->{
            tableBo.changeNumberOfGuests(orderTable1.getId(), orderTable1);
        });
        assertThat(thrown).isInstanceOf(IllegalArgumentException.class);
    }


}