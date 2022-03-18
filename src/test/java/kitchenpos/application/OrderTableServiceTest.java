package kitchenpos.application;

import kitchenpos.domain.InMemoryOrderTableRepository;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

class OrderTableServiceTest {

    public static OrderTableRepository orderTableRepository = new InMemoryOrderTableRepository();
    private OrderRepository orderRepository = OrderServiceTest.orderRepository;

    private OrderTableService orderTableService = new OrderTableService(orderTableRepository, orderRepository);

    @DisplayName("주문테이블을 생성한다.")
    @Test
    void createTable() {
        OrderTable orderTable = new OrderTable();
        orderTable.setName("1번 테이블");

        OrderTable createdOrderTable = orderTableService.create(orderTable);

        assertAll(
                () -> assertThat(createdOrderTable).isNotNull(),
                () -> assertThat(createdOrderTable.getId()).isNotNull(),
                () -> assertThat(createdOrderTable.getNumberOfGuests()).isEqualTo(0),
                () -> assertThat(createdOrderTable.isEmpty()).isTrue()
        );
    }

    static Stream<String> invalidOrderTableName() {
        return Stream.of(null, "");
    }

    @DisplayName("주문테이블의 이름은 필수로 지정해야한다.")
    @ParameterizedTest
    @MethodSource("invalidOrderTableName")
    void necessaryTableName(String tableName) {
        OrderTable orderTable = new OrderTable();
        orderTable.setName(tableName);

        assertThatThrownBy(
                () -> orderTableService.create(orderTable)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("주문테이블을 착석상태로 변경한다.")
    @Test
    void sit() {
        OrderTable savedOrderTable = saveOrderTable("1번테이블");

        OrderTable orderTable = orderTableService.sit(savedOrderTable.getId());

        assertThat(orderTable.isEmpty()).isFalse();
    }

    @DisplayName("주문테이블을 빈테이블로 변경한다.")
    @Test
    void clear() {
        OrderTable savedOrderTable = saveOrderTable("1번테이블", 4, false);

        OrderTable orderTable = orderTableService.clear(savedOrderTable.getId());

        assertThat(orderTable.isEmpty()).isTrue();
    }

    @DisplayName("완료되지 않은 주문이 존재하면 빈테이블로 변경할수 없다.")
    @Test
    void unableClear() {
        OrderTable savedOrderTable = saveOrderTable("1번테이블", 4, false);
        OrderServiceTest.saveOrderTargetTable(savedOrderTable);

        assertThatThrownBy(
                () -> orderTableService.clear(savedOrderTable.getId())
        ).isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("주문테이블의 손님수를 변경한다.")
    @ParameterizedTest
    @ValueSource(ints = {2, 3, 4})
    void changeNumberOfGuests(int numberOfGuests) {
        OrderTable savedOrderTable = saveOrderTable("1번 테이블", 1, false);
        savedOrderTable.setNumberOfGuests(numberOfGuests);

        OrderTable orderTable = orderTableService.changeNumberOfGuests(savedOrderTable.getId(), savedOrderTable);

        assertThat(orderTable.getNumberOfGuests()).isEqualTo(numberOfGuests);
    }

    @DisplayName("손님수 변경시 사람수는 0명 이상이여야 한다.")
    @ParameterizedTest
    @ValueSource(ints = {-1, -2, -3})
    void unableNumberOfGuests(int numberOfGuests) {
        OrderTable savedOrderTable = saveOrderTable("1번 테이블", 1, false);
        savedOrderTable.setNumberOfGuests(numberOfGuests);

        assertThatThrownBy(
                () -> orderTableService.changeNumberOfGuests(savedOrderTable.getId(), savedOrderTable)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("빈 테이블은 사람수를 변경할 수 없다.")
    @ParameterizedTest
    @ValueSource(ints = {2, 4})
    void unableChangeNumberOfGuestsForEmptyTable(int numberOfGuests) {
        OrderTable savedOrderTable = saveOrderTable("1번 테이블", 0, true);
        savedOrderTable.setNumberOfGuests(numberOfGuests);

        assertThatThrownBy(
                () -> orderTableService.changeNumberOfGuests(savedOrderTable.getId(), savedOrderTable)
        ).isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("주문테이블을 조회한다.")
    @Test
    void findAll() {
        saveOrderTable("1번 테이블");
        saveOrderTable("2번 테이블");
        int orderTablesSize = orderTableRepository.findAll()
                .size();

        List<OrderTable> orderTables = orderTableService.findAll();

        assertThat(orderTables).hasSize(orderTablesSize);

    }

    public static OrderTable saveOrderTable(String name) {
        return orderTableRepository.save(saveOrderTable(name, 0, true));
    }

    public static OrderTable saveOrderTable(String name, int numberOfGuests, boolean empty) {
        OrderTable orderTable = new OrderTable();
        orderTable.setId(UUID.randomUUID());
        orderTable.setName(name);
        orderTable.setNumberOfGuests(numberOfGuests);
        orderTable.setEmpty(empty);

        return orderTableRepository.save(orderTable);
    }
}
