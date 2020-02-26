package kitchenpos.bo;

import kitchenpos.bo.mock.TestOrderDao;
import kitchenpos.bo.mock.TestOrderTableDao;
import kitchenpos.dao.OrderDao;
import kitchenpos.dao.OrderTableDao;
import kitchenpos.model.Order;
import kitchenpos.model.OrderStatus;
import kitchenpos.model.OrderTable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static kitchenpos.Fixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TableBoTest {

    private static final long DEFAULT_TABLE_ID = 1L;
    private static final long EMPTY_GROUP_TABLE_ID = 3L;
    private static final long NOT_EXIST_TABLE_ID = 5L;

    private OrderDao orderDao = new TestOrderDao();
    private OrderTableDao orderTableDao = new TestOrderTableDao();

    private TableBo tableBo = new TableBo(orderDao, orderTableDao);

    private OrderTable defaultOrderTable;
    private OrderTable emptyGroupIdOrderTable;
    private OrderTable emptyTrueOrderTable;
    private OrderTable inputOrderTable = new OrderTable();
    private Order defaultOrder = new Order();

    @BeforeEach
    void setUp() {
        defaultOrderTable = defaultOrderTable();
        emptyGroupIdOrderTable = emptyGroupIdOrderTable();
        emptyTrueOrderTable = emptyTrueOrderTable();

        orderTableDao.save(defaultOrderTable);
        orderTableDao.save(emptyGroupIdOrderTable);
        orderTableDao.save(emptyTrueOrderTable);

        defaultOrder.setId(1L);
        defaultOrder.setOrderTableId(1L);
        defaultOrder.setOrderStatus(OrderStatus.COOKING.toString());
        orderDao.save(defaultOrder);
    }

    @DisplayName("테이블 생성")
    @Test
    void create() {
        OrderTable input = new OrderTable();
        input.setId(4L);

        OrderTable result = tableBo.create(input);

        assertAll(
                () -> assertThat(result.getId()).isEqualTo(4L),
                () -> assertThat(result.getTableGroupId()).isEqualTo(null),
                () -> assertThat(result.getNumberOfGuests()).isEqualTo(0),
                () -> assertThat(result.isEmpty()).isFalse()
        );
    }

    @DisplayName("테이블 목록 조회")
    @Test
    void list() {
        List<OrderTable> result = tableBo.list();

        assertAll(
                () -> assertThat(result.size()).isEqualTo(4),
                () -> assertThat(result.get(0).getId()).isEqualTo(1L),
                () -> assertThat(result.get(0).getTableGroupId()).isEqualTo(1L),
                () -> assertThat(result.get(0).getNumberOfGuests()).isEqualTo(4),
                () -> assertThat(result.get(0).isEmpty()).isFalse()
        );
    }

    @DisplayName("존재하지 않는 테이블의 상태를 바꾸려고 했을 때 오류 발생")
    @Test
    void changeEmptyWithException() {
        assertThrows(IllegalArgumentException.class, () -> tableBo.changeEmpty(NOT_EXIST_TABLE_ID, inputOrderTable));
    }

    @DisplayName("테이블 상태 바꿀 때 그룹 번호가 있으면 오류 발생")
    @Test
    void changeEmptyTableGroupId() {
        assertThrows(IllegalArgumentException.class, () -> tableBo.changeEmpty(DEFAULT_TABLE_ID, inputOrderTable));
    }

    @DisplayName("테이블 상태 바꿀 때 주문 상태가 조리중, 식사중이면 오류 발생")
    @Test
    void changeEmptyOrderStatus() {
        assertThrows(IllegalArgumentException.class, () -> tableBo.changeEmpty(NOT_EXIST_TABLE_ID, inputOrderTable));
    }

    @DisplayName("테이블 상태 바꾸기 성공")
    @Test
    void changeEmpty() {
        inputOrderTable.setEmpty(true);

        OrderTable result = tableBo.changeEmpty(2L, inputOrderTable);

        assertAll(
                () -> assertThat(result.getId()).isEqualTo(2L),
                () -> assertThat(result.getTableGroupId()).isEqualTo(null),
                () -> assertThat(result.getNumberOfGuests()).isEqualTo(4),
                () -> assertThat(result.isEmpty()).isTrue()
        );
    }

    @DisplayName("테이블의 손님 수 바꾸기 손님이 0보다 작으면 오류 발생")
    @Test
    void changeNumberOfGuestsLessThenZero() {
        inputOrderTable.setNumberOfGuests(-2);

        assertThrows(IllegalArgumentException.class, () -> tableBo.changeNumberOfGuests(DEFAULT_TABLE_ID, inputOrderTable));
    }

    @DisplayName("테이블의 손님 수 바꾸기 테이블이 존재하지 않으면 오류 발생")
    @Test
    void changeNumberOfGuestsNullTable() {
        assertThrows(IllegalArgumentException.class, () -> tableBo.changeNumberOfGuests(NOT_EXIST_TABLE_ID, inputOrderTable));
    }

    @DisplayName("테이블의 손님 수 바꾸기 테이블이 비어있으면 오류 발생")
    @Test
    void changeNumberOfGuestsEmptyGuests() {
        assertThrows(IllegalArgumentException.class, () -> tableBo.changeNumberOfGuests(EMPTY_GROUP_TABLE_ID, inputOrderTable));
    }

    @DisplayName("테이블의 손님 수 바꾸기")
    @Test
    void changeNumberOfGuests() {
        inputOrderTable.setNumberOfGuests(8);

        OrderTable result = tableBo.changeNumberOfGuests(1L, inputOrderTable);

        assertAll(
                () -> assertThat(result.getId()).isEqualTo(1L),
                () -> assertThat(result.getTableGroupId()).isEqualTo(1L),
                () -> assertThat(result.getNumberOfGuests()).isEqualTo(8),
                () -> assertThat(result.isEmpty()).isFalse()
        );
    }
}
