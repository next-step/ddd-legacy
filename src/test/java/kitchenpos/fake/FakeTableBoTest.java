package kitchenpos.fake;

import kitchenpos.TestFixture;
import kitchenpos.bo.TableBo;
import kitchenpos.dao.OrderDao;
import kitchenpos.dao.OrderTableDao;
import kitchenpos.model.Order;
import kitchenpos.model.OrderTable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FakeTableBoTest {

    private TableBo tableBo;

    private OrderDao orderDao = new FakeOrderDao();

    private OrderTableDao orderTableDao = new FakeOrderTableDao();

    @BeforeEach
    void setUp() {
        tableBo = new TableBo(orderDao, orderTableDao);
    }

    @DisplayName("테이블 리스트를 조회")
    @Test
    void list() {
        OrderTable orderTable1 = TestFixture.generateOrderTableEmptyOne();
        OrderTable orderTable2 = TestFixture.generateOrderTableEmptyTWo();

        orderTableDao.save(orderTable1);
        orderTableDao.save(orderTable2);

        List<OrderTable> orderTables = Arrays.asList(orderTable1, orderTable2);

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

        orderTableDao.save(orderTable);

        OrderTable changedOrderTable = tableBo.changeEmpty(orderTable.getId(), requestOrderTable);

        assertThat(changedOrderTable.isEmpty()).isTrue();
    }

    @DisplayName("테이블 그룹에 포함된 테이블의 상태를 변경 시 에러")
    @Test
    void changeEmptyFailByInTableGroup() {
        OrderTable orderTable = TestFixture.generateOrderTableNotEmpty();
        orderTable.setTableGroupId(1L);

        assertThrows(IllegalArgumentException.class, () -> tableBo.changeEmpty(orderTable.getId(), orderTable));
    }

    @DisplayName("주문상태가 식사중 또는 요리중인 테이블의 상태를 변경 시 에러")
    @Test
    void changeEmptyFailByOrderStatus() {
        OrderTable orderTable = TestFixture.generateOrderTableNotEmpty();

        Order order = TestFixture.generateOrderCooking();

        orderDao.save(order);

        assertThrows(IllegalArgumentException.class, () -> tableBo.changeEmpty(orderTable.getId(), orderTable));
    }

    @DisplayName("손님이 왔을때 인원수에 맞게 테이블 정보 변경")
    @ParameterizedTest
    @ValueSource(ints = {2, 3, 5})
    void changeNumberOfGuests(int numberOfGuest) {
        OrderTable requestOrderTable = TestFixture.generateOrderTableEmptyOne();
        requestOrderTable.setNumberOfGuests(numberOfGuest);

        OrderTable savedOrderTable = TestFixture.generateOrderTableNotEmpty();

        orderTableDao.save(savedOrderTable);

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
        OrderTable orderTable = TestFixture.generateOrderTableEmptyOne();
        orderTable.setNumberOfGuests(numberOfGuest);

        assertThrows(IllegalArgumentException.class, () -> tableBo.changeNumberOfGuests(orderTable.getId(), orderTable));
    }

    @DisplayName("등록되어 있지 않은 테이블 정보 변경시 에러")
    @Test
    void changeNumberOfGuestsFailByNotExistTable() {
        OrderTable orderTable = TestFixture.generateOrderTableEmptyOne();
        orderTable.setNumberOfGuests(4);

        assertThrows(IllegalArgumentException.class, () -> tableBo.changeNumberOfGuests(orderTable.getId(), orderTable));
    }
}
