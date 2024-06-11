package kitchenpos.application;

import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import kitchenpos.fake.order.TestOrderRepository;
import kitchenpos.fake.ordertable.TestOrderTableRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static kitchenpos.OrderTestFixture.changeOrderTableRequest;
import static kitchenpos.OrderTestFixture.createOrderTableRequest;
import static kitchenpos.OrderTestFixture.getSavedOrderTable;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OrderTableServiceTest {
    private OrderRepository orderRepository;
    private OrderTableRepository orderTableRepository;
    private OrderTableService orderTableService;

    @BeforeEach
    void setUp() {
        orderRepository = new TestOrderRepository();
        orderTableRepository = new TestOrderTableRepository();
        orderTableService = new OrderTableService(orderTableRepository, orderRepository);
    }

    @DisplayName("OrderTable 생성 성공")
    @Test
    void createOrderTable() {
        // given
        String tableName = "tableName";

        // when
        OrderTable orderTable = orderTableService.create(createOrderTableRequest(tableName));

        // then
        assertAll(
                () -> assertThat(orderTable.getName()).isEqualTo(tableName),
                () -> assertNotNull(orderTable.getId()),
                () -> assertFalse(orderTable.isOccupied()),
                () -> assertThat(orderTable.getNumberOfGuests()).isEqualTo(0)
        );
    }

    @DisplayName("OrderTable 생성 이름 실패")
    @ParameterizedTest
    @NullAndEmptySource
    void createOrderTableFail(String tableName) {
        assertThrows(IllegalArgumentException.class, () -> orderTableService.create(createOrderTableRequest(tableName)));
    }

    @Test
    @DisplayName("사용중 상태로 변경할 수 있다")
    void sitOrderTableTest() {
        OrderTable original = getSavedOrderTable(orderTableService, "tableName");
        // when
        OrderTable updatedOrderTable = orderTableService.sit(original.getId());

        // then
        assertTrue(updatedOrderTable.isOccupied());
    }

    @Test
    @DisplayName("존재하지 않는다면 사용중상태로 변경할 수 없다")
    void sitOrderTableTestFail() {
        // when then
        assertThrows(NoSuchElementException.class, () -> orderTableService.sit(UUID.randomUUID()));
    }

    @Test
    @DisplayName("사용중인 테이블의 손님의 수를 변경할 수 있다.")
    void changeNumberOfGuests() {
        OrderTable original = getSavedOrderTable(orderTableService, "tableName");
        orderTableService.sit(original.getId());
        final int newNumberOfGuests = 5;
        OrderTable changed = orderTableService.changeNumberOfGuests(original.getId(), changeOrderTableRequest(newNumberOfGuests));

        assertThat(changed.getNumberOfGuests()).isEqualTo(newNumberOfGuests);
    }

    @Test
    @DisplayName("사용중인 테이블의 변경하려는 손님의 수가 음수라면 실패한다")
    void changeNegativeNumberOfGuestsFail() {
        OrderTable original = getSavedOrderTable(orderTableService, "tableName");
        orderTableService.sit(original.getId());
        final int newNumberOfGuests = -1;
        assertThrows(IllegalArgumentException.class, () -> orderTableService.changeNumberOfGuests(original.getId(), changeOrderTableRequest(newNumberOfGuests)));
    }

    @Test
    @DisplayName("사용중이지 않은 테이블의 손님수는 변경할 수 없다")
    void changeeNumberOfGuestsNotOccupiedFail() {
        OrderTable original = getSavedOrderTable(orderTableService, "tableName");

        final int newNumberOfGuests = 3;
        assertThrows(IllegalStateException.class, () -> orderTableService.changeNumberOfGuests(original.getId(), changeOrderTableRequest(newNumberOfGuests)));
    }

    @Test
    @DisplayName("모든 주문 매장 테이블을 조회할 수 있다")
    void findAllOrderTables() {
        OrderTable original = getSavedOrderTable(orderTableService, "tableName");
        List<OrderTable> all = orderTableService.findAll();

        assertAll(
                () -> assertThat(all).hasSize(1),
                () -> assertThat(all.get(0).getId()).isEqualTo(original.getId())
        );
    }
}
