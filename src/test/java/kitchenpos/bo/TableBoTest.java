package kitchenpos.bo;

import kitchenpos.TestFixture;
import kitchenpos.dao.OrderDao;
import kitchenpos.dao.OrderTableDao;
import kitchenpos.model.OrderStatus;
import kitchenpos.model.OrderTable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TableBoTest {

    @InjectMocks
    private TableBo tableBo;

    @Mock
    private OrderDao orderDao;

    @Mock
    private OrderTableDao orderTableDao;

    @DisplayName("테이블 리스트를 조회")
    @Test
    void list() {
        OrderTable orderTable1 = TestFixture.generateOrderTableEmptyOne();
        OrderTable orderTable2 = TestFixture.generateOrderTableEmptyTWo();

        List<OrderTable> orderTables = Arrays.asList(orderTable1, orderTable2);

        when(orderTableDao.findAll()).thenReturn(orderTables);

        List<OrderTable> findOrderTables = tableBo.list();

        assertAll(
                () -> assertThat(findOrderTables).containsAll(orderTables),
                () -> assertThat(findOrderTables.size()).isEqualTo(orderTables.size())
        );
    }

    @DisplayName("테이블 생성")
    @Test
    void create() {
        OrderTable orderTable = TestFixture.generateOrderTableEmptyOne();

        when(orderTableDao.save(orderTable)).thenReturn(orderTable);

        OrderTable savedOrderTable = tableBo.create(orderTable);

        assertAll(
                () -> assertThat(savedOrderTable.getId()).isEqualTo(orderTable.getId()),
                () -> assertThat(savedOrderTable.getNumberOfGuests()).isEqualTo(orderTable.getNumberOfGuests()),
                () -> assertThat(savedOrderTable.isEmpty()).isTrue()
        );
    }

    @DisplayName("테이블 착석 상태를 비움으로 변경")
    @Test
    void changeEmpty() {
        OrderTable requestOrderTable = TestFixture.generateOrderTableEmptyOne();

        OrderTable orderTable = TestFixture.generateOrderTableNotEmpty();

        when(orderTableDao.findById(orderTable.getId())).thenReturn(Optional.of(orderTable));
        when(orderDao.existsByOrderTableIdAndOrderStatusIn(orderTable.getId(),
                Arrays.asList(OrderStatus.COOKING.name(), OrderStatus.MEAL.name()))).thenReturn(false);
        when(orderTableDao.save(orderTable)).thenReturn(orderTable);

        OrderTable changedOrderTable = tableBo.changeEmpty(orderTable.getId(), requestOrderTable);

        assertThat(changedOrderTable.isEmpty()).isTrue();
    }

    @DisplayName("테이블 그룹에 포함된 테이블의 상태를 변경 시 에러")
    @Test
    void changeEmptyFailByInTableGroup() {
        OrderTable orderTable = TestFixture.generateOrderTableNotEmpty();
        orderTable.setTableGroupId(1L);

        when(orderTableDao.findById(orderTable.getId())).thenReturn(Optional.of(orderTable));

        assertThrows(IllegalArgumentException.class, () -> tableBo.changeEmpty(orderTable.getId(), orderTable));
    }

    @DisplayName("주문상태가 식사중 또는 요리중인 테이블의 상태를 변경 시 에러")
    @Test
    void changeEmptyFailByOrderStatus() {
        OrderTable orderTable = TestFixture.generateOrderTableNotEmpty();

        when(orderTableDao.findById(orderTable.getId())).thenReturn(Optional.of(orderTable));
        when(orderDao.existsByOrderTableIdAndOrderStatusIn(orderTable.getId(),
                Arrays.asList(OrderStatus.COOKING.name(), OrderStatus.MEAL.name()))).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> tableBo.changeEmpty(orderTable.getId(), orderTable));
    }

    @DisplayName("손님이 왔을때 인원수에 맞게 테이블 정보 변경")
    @ParameterizedTest
    @ValueSource(ints = {2, 3, 5})
    void changeNumberOfGuests(int numberOfGuest) {
        OrderTable requestOrderTable = TestFixture.generateOrderTableNotEmpty();
        requestOrderTable.setNumberOfGuests(numberOfGuest);

        OrderTable savedOrderTable = TestFixture.generateOrderTableNotEmpty();
        savedOrderTable.setNumberOfGuests(numberOfGuest);

        when(orderTableDao.findById(requestOrderTable.getId())).thenReturn(Optional.of(requestOrderTable));
        when(orderTableDao.save(requestOrderTable)).thenReturn(savedOrderTable);

        OrderTable changedOrderTable = tableBo.changeNumberOfGuests(requestOrderTable.getId(), requestOrderTable);

        assertAll(
                () -> assertThat(changedOrderTable.getId()).isEqualTo(savedOrderTable.getId()),
                () -> assertThat(changedOrderTable.getNumberOfGuests()).isEqualTo(savedOrderTable.getNumberOfGuests())
        );
    }

    @DisplayName("인원수가 0보다 작을 시 에러")
    @ParameterizedTest
    @ValueSource(ints = {-1, -5})
    void changeNumberOfGuestsFailByNumberLessThanZero(int numberOfGuest) {
        OrderTable orderTable = TestFixture.generateOrderTableOne();
        orderTable.setNumberOfGuests(numberOfGuest);

        assertThrows(IllegalArgumentException.class, () -> tableBo.changeNumberOfGuests(orderTable.getId(), orderTable));
    }

    @DisplayName("등록되어 있지 않은 테이블 정보 변경시 에러")
    @Test
    void changeNumberOfGuestsFailByNotExistTable() {
        OrderTable orderTable = TestFixture.generateOrderTableEmptyOne();

        when(orderTableDao.findById(orderTable.getId())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> tableBo.changeNumberOfGuests(orderTable.getId(), orderTable));
    }
}
