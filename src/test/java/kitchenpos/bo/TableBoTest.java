package kitchenpos.bo;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import kitchenpos.dao.OrderDao;
import kitchenpos.dao.OrderTableDao;
import kitchenpos.model.OrderStatus;
import kitchenpos.model.OrderTable;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
class TableBoTest {

    @Mock
    private OrderDao orderDao;

    @Mock
    private OrderTableDao orderTableDao;

    @InjectMocks
    private TableBo tableBo;

    @DisplayName("테이블을 단건씩 생성할 수 있다.")
    @Test
    void createOne(){
        OrderTable orderTable = mock(OrderTable.class);
        given(orderTableDao.save(orderTable)).willReturn(orderTable);

        Assertions.assertThat(tableBo.create(orderTable)).isEqualTo(orderTable);
    }

    @DisplayName("테이블 목록을 조회 할수 있다.")
    @Test
    void list() {
        List<OrderTable> orderTables = Arrays.asList(mock(OrderTable.class), mock(OrderTable.class), mock(OrderTable.class));
        given(orderTableDao.findAll()).willReturn(orderTables);

        Assertions.assertThat(tableBo.list()).containsAll(orderTables);
    }

    @DisplayName("테이블의 주문이 완료된 상태, 테이블 그룹이 없을때만 이용여부를 변경할수 없다.")
    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void changeEmpty(boolean requestEmpty) {

        OrderTable requestOrderTable = new OrderTable();
        requestOrderTable.setEmpty(requestEmpty);

        OrderTable savedOrderTable = new OrderTable();
        savedOrderTable.setId(1L);
        savedOrderTable.setTableGroupId(null);


        given(orderTableDao.findById(savedOrderTable.getId()))
            .willReturn(Optional.ofNullable(savedOrderTable));
        given(orderDao.existsByOrderTableIdAndOrderStatusIn(savedOrderTable.getId(), Arrays.asList(OrderStatus.COOKING.name(), OrderStatus.MEAL.name())))
            .willReturn(false);
        given(orderTableDao.save(savedOrderTable)).willReturn(savedOrderTable);


        Assertions.assertThat(tableBo.changeEmpty(savedOrderTable.getId(), requestOrderTable).isEmpty()).isEqualTo(requestEmpty);
    }

    @DisplayName("테이블에 인원은 0명 이상이다.")
    @ParameterizedTest
    @ValueSource(ints = {-1,-2,-3,-4})
    void guestEqualToGreaterThanZero(int numberOfGuests) {
        OrderTable requestOrderTable = new OrderTable();
        requestOrderTable.setNumberOfGuests(numberOfGuests);

        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(()->tableBo.changeNumberOfGuests(1L, requestOrderTable));
    }

    @DisplayName("테이블이 이용중이고 인원을 양수로 변경할수 있다")
    @ParameterizedTest
    @ValueSource(ints = {1,2,3,4})
    void changeNumberOfGuests(int numberOfGuests) {

        Long tableId= 1L;
        OrderTable requestOrderTable = new OrderTable();
        requestOrderTable.setNumberOfGuests(numberOfGuests);

        OrderTable savedOrderTable = new OrderTable();
        savedOrderTable.setEmpty(false);

        given(orderTableDao.findById(tableId)).willReturn(Optional.ofNullable(savedOrderTable));
        given(orderTableDao.save(savedOrderTable)).willReturn(savedOrderTable);

        Assertions.assertThat(tableBo.changeNumberOfGuests(tableId, requestOrderTable).getNumberOfGuests()).isEqualTo(numberOfGuests);
    }
}
