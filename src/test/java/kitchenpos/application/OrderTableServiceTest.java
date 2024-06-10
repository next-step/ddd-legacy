package kitchenpos.application;

import static kitchenpos.OrderTestFixture.createOrderTableRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

class OrderTableServiceTest {
    private OrderRepository orderRepository;
    private OrderTableRepository orderTableRepository;

    @BeforeEach
    void setUp() {
        orderRepository = new TestOrderRepository();
        orderTableRepository = new TestOrderTableRepository();
    }

    @DisplayName("OrderTable 생성 성공")
    @Test
    void createOrderTable() {
        // given
        OrderTableService orderTableService = new OrderTableService(orderTableRepository, orderRepository);
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
        // given
        OrderTableService orderTableService = new OrderTableService(orderTableRepository, orderRepository);

        // when, then
        assertThrows(IllegalArgumentException.class, () -> orderTableService.create(createOrderTableRequest(tableName)));
    }


}
