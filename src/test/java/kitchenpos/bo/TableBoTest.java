package kitchenpos.bo;

import kitchenpos.builder.OrderTableBuilder;
import kitchenpos.dao.OrderDao;
import kitchenpos.dao.OrderTableDao;
import kitchenpos.model.OrderStatus;
import kitchenpos.model.OrderTable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class TableBoTest {
    @Mock
    private OrderDao orderDao;
    @Mock
    private OrderTableDao orderTableDao;
    @InjectMocks
    private TableBo tableBo;

    private OrderTableBuilder orderTableBuilder = new OrderTableBuilder();

    @Test
    @DisplayName("테이블을 새로 등록할 수 있다")
    void create() {
        OrderTable newOrderTable = orderTableBuilder
                .id(1L)
                .empty(Boolean.TRUE)
                .numberOfGuests(0)
                .tableGroupId(1L)
                .build();

        given(orderTableDao.save(newOrderTable))
                .willReturn(newOrderTable);

        OrderTable exceptedOrderTab1e = tableBo.create(newOrderTable);

        assertThat(exceptedOrderTab1e).isEqualTo(newOrderTable);
    }

    @Test
    @DisplayName("테이블의 목록을 조회할 수 있다")
    void list() {

        List<OrderTable> orderTableList = asList(
                orderTableBuilder
                        .id(1L)
                        .empty(Boolean.TRUE)
                        .numberOfGuests(0)
                        .tableGroupId(1L)
                        .build()
        );

        given(orderTableDao.findAll())
                .willReturn(orderTableList);

        List<OrderTable> exceptedOrderTab1eList = tableBo.list();

        assertThat(exceptedOrderTab1eList)
                .hasSameSizeAs(orderTableList)
                .isEqualTo(orderTableList);

    }

    @Test
    @DisplayName("테이블 번호를 지정하여 사용상태로 변경할 수 있다")
    void changeEmpty() {

        Long orderTableId = 1L;
        OrderTable orderTable = orderTableBuilder
                .id(1L)
                .empty(Boolean.TRUE)
                .numberOfGuests(0)
                .build();

        given(orderTableDao.findById(orderTableId))
                .willReturn(java.util.Optional.ofNullable(orderTable));

        given(orderDao.existsByOrderTableIdAndOrderStatusIn(orderTableId, Arrays.asList(OrderStatus.COOKING.name(), OrderStatus.MEAL.name())))
                .willReturn(Boolean.FALSE);

        given(orderTableDao.save(orderTable))
                .willReturn(orderTable);

        OrderTable changeOrderTable = tableBo.changeEmpty(orderTableId, orderTable);

        assertThat(changeOrderTable.isEmpty()).isEqualTo(orderTable.isEmpty());
    }

    @Test
    @DisplayName("테이블그룹 번호가 지정되지 않아야 한다")
    void changeEmptyExceptionNoGroupTable() {
        Long orderTableId = 1L;
        OrderTable orderTable = orderTableBuilder
                .id(1L)
                .empty(Boolean.TRUE)
                .numberOfGuests(0)
                .tableGroupId(1L)
                .build();

        given(orderTableDao.findById(orderTableId))
                .willReturn(java.util.Optional.ofNullable(orderTable));

        assertThatThrownBy(() -> tableBo.changeEmpty(orderTableId, orderTable))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("주문 상태가 \"COOKING\" 또는 \"MEAL\" 인 건수가 존재하지 않아야 한다")
    void changeEmptyExceptionOrderStatus() {
        Long orderTableId = 1L;
        OrderTable orderTable = orderTableBuilder
                .id(1L)
                .empty(Boolean.TRUE)
                .numberOfGuests(0)
                .build();

        given(orderTableDao.findById(orderTableId))
                .willReturn(java.util.Optional.ofNullable(orderTable));

        given(orderDao.existsByOrderTableIdAndOrderStatusIn(orderTableId, Arrays.asList(OrderStatus.COOKING.name(), OrderStatus.MEAL.name())))
                .willReturn(Boolean.TRUE);

        assertThatThrownBy(() -> tableBo.changeEmpty(orderTableId, orderTable))
                .isInstanceOf(IllegalArgumentException.class);
    }


    @Test
    @DisplayName("테이블 번호를 지정하여 손님 인원수를 등록할 수 있다")
    void changeNumberOfGuests() {
        Long orderTableId = 1L;
        OrderTable orderTable = orderTableBuilder
                .id(1L)
                .empty(Boolean.FALSE)
                .numberOfGuests(1)
                .build();

        given(orderTableDao.findById(orderTableId))
                .willReturn(java.util.Optional.ofNullable(orderTable));

        given(orderTableDao.save(orderTable))
                .willReturn(orderTable);


        OrderTable changeOrderTable = tableBo.changeNumberOfGuests(orderTableId, orderTable);

        assertThat(changeOrderTable.getNumberOfGuests()).isEqualTo(orderTable.getNumberOfGuests());
    }

    @Test
    @DisplayName("손님 인원수는 0보다 작으면 예외가 발생한다")
    void changeNumberOfGuestsExceptionGuestNumber() {
        Long orderTableId = 1L;
        OrderTable orderTable = orderTableBuilder
                .id(1L)
                .empty(Boolean.FALSE)
                .numberOfGuests(-1)
                .build();

        assertThatThrownBy(() -> tableBo.changeNumberOfGuests(orderTableId, orderTable))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("테이블 사용상태는 비어있지 않아야 한다")
    void changeNumberOfGuestsExceptionTableEmpty() {
        Long orderTableId = 1L;
        OrderTable orderTable = orderTableBuilder
                .id(1L)
                .empty(Boolean.TRUE)
                .numberOfGuests(0)
                .build();

        given(orderTableDao.findById(orderTableId))
                .willReturn(java.util.Optional.ofNullable(orderTable));

        assertThatThrownBy(() -> tableBo.changeNumberOfGuests(orderTableId, orderTable))
                .isInstanceOf(IllegalArgumentException.class);
    }

}