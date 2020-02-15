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
        //given
        OrderTable expected = new OrderTable();
        expected.setEmpty(true);
        expected.setNumberOfGuests(0);

        given(orderTableDao.save(expected)).willReturn(expected);

        //when
        OrderTable actual = tableBo.create(expected);

        //then
        Assertions.assertThat(tableBo.create(expected)).isEqualTo(actual);
    }

    @DisplayName("테이블 목록을 조회 할수 있다.")
    @Test
    void list() {
        List<OrderTable> orderTables = Arrays.asList(mock(OrderTable.class), mock(OrderTable.class), mock(OrderTable.class));
        given(orderTableDao.findAll()).willReturn(orderTables);

        Assertions.assertThat(tableBo.list()).containsAll(orderTables);
    }

    @DisplayName("테이블 주문의 상태가 완료일 경우 테이블 이용여부를 변경할수 없다.")
    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void changeEmpty(boolean requestEmpty) {
        //given
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

        //when, then
        Assertions.assertThat(tableBo.changeEmpty(savedOrderTable.getId(), requestOrderTable).isEmpty()).isEqualTo(requestEmpty);
    }

    @DisplayName("테이블에 인원은 0명 미만일 경우 실패한다.")
    @ParameterizedTest
    @ValueSource(ints = {-1,-2,-3,-4})
    void guestEqualToGreaterThanZero(int numberOfGuests) {
        //given
        OrderTable requestOrderTable = new OrderTable();
        requestOrderTable.setNumberOfGuests(numberOfGuests);

        //when then
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(()->tableBo.changeNumberOfGuests(1L, requestOrderTable));
    }

    @DisplayName("테이블이 이용중인 상태에서만 인원을 변경할수 있다")
    @ParameterizedTest
    @ValueSource(ints = {1,2,3,4})
    void changeNumberOfGuests(int numberOfGuests) {
        //given
        Long tableId= 1L;
        OrderTable requestOrderTable = new OrderTable();
        requestOrderTable.setNumberOfGuests(numberOfGuests);

        OrderTable savedOrderTable = new OrderTable();
        savedOrderTable.setEmpty(false);

        given(orderTableDao.findById(tableId)).willReturn(Optional.ofNullable(savedOrderTable));
        given(orderTableDao.save(savedOrderTable)).willReturn(savedOrderTable);

        //when then
        Assertions.assertThat(tableBo.changeNumberOfGuests(tableId, requestOrderTable).getNumberOfGuests())
            .isEqualTo(numberOfGuests);
    }
}
