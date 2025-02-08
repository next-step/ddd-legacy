package kitchenpos.application;

import kitchenpos.IntegrationTestSupport;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import static kitchenpos.fixtures.OrderTableFixtures.orderTableChange;
import static kitchenpos.fixtures.OrderTableFixtures.orderTableClear;
import static kitchenpos.fixtures.OrderTableFixtures.orderTableCreate;
import static kitchenpos.fixtures.OrderTableFixtures.orderTableSit;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;


@Service
class OrderTableServiceTest extends IntegrationTestSupport {

    @Autowired
    private OrderTableService orderTableService;

    @Autowired
    private OrderTableRepository orderTableRepository;

    @Autowired
    private OrderRepository orderRepository;

    @AfterEach
    void tearDown() {
        orderTableRepository.deleteAllInBatch();
        orderRepository.deleteAllInBatch();
    }

    @DisplayName("주문 테이블을 등록하면 손님 수는 0명이고, 사용 가능 상태여야 한다.")
    @Test
    void createOrderTable_DefaultState_Success() {
        // given
        OrderTable expected = orderTableCreate("1번 테이블");

        // when
        OrderTable actual = orderTableService.create(expected);

        // then
        assertThat(actual).isNotNull();
        assertAll(
            () -> assertThat(actual.getId()).isNotNull(),
            () -> assertThat(actual.getName()).isEqualTo(expected.getName()),
            () -> assertThat(actual.getNumberOfGuests()).isEqualTo(0),
            () -> assertThat(actual.isOccupied()).isFalse()
        );
    }

    @DisplayName("주문 테이블의 이름이 없으면 등록할 수 없다.")
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   "})
    void createOrderTable_WhenNameIsNullOrEmpty_ThrowException(final String invalidName) {
        // given
        OrderTable expected = new OrderTable();
        expected.setName(invalidName);

        // when & then
        assertThatThrownBy(() -> orderTableService.create(expected))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("주문 테이블 이름이 존재해야 합니다.");
    }

    @DisplayName("주문 테이블에 손님이 앉으면 사용중 상태로 변경된다.")
    @Test
    void sitOrderTable_Success() {
        // given
        OrderTable orderTable = orderTableSit(UUID.randomUUID(), "1번 테이블", false, 0);
        OrderTable expected = orderTableRepository.save(orderTable);

        // when
        OrderTable actual = orderTableService.sit(expected.getId());

        // then
        assertThat(actual.isOccupied()).isTrue();
    }

    @DisplayName("존재하지 않는 주문 테이블에 손님이 앉으려 하면 예외가 발생한다.")
    @Test
    void sitOrderTable_WhenOrderTableNotExists_ShouldThrowException() {
        // given
        UUID nonExistentTableId = UUID.randomUUID();

        // when & then
        assertThatThrownBy(() -> orderTableService.sit(nonExistentTableId))
            .isInstanceOf(NoSuchElementException.class)
            .hasMessage("주문 테이블이 존재해야 한다.");
    }

    @DisplayName("주문이 완료된 경우, 주문 테이블을 정리할 수 있다.")
    @Test
    void clearOrderTable_Success() {
        // given
        OrderTable orderTable = orderTableClear(UUID.randomUUID(), "1번 테이블", true, 4);
        OrderTable expected = orderTableRepository.save(orderTable);

        // when
        OrderTable actual = orderTableService.clear(expected.getId());

        // then
        assertAll(
            () -> assertThat(actual.getNumberOfGuests()).isEqualTo(0),
            () -> assertThat(actual.isOccupied()).isFalse()
        );
    }

    @DisplayName("주문 테이블의 손님 수를 변경할 수 있다.")
    @Test
    void changeNumberOfGuests_Success() {
        // given
        OrderTable orderTable = orderTableChange(UUID.randomUUID(), "1번 테이블", true, 4);
        orderTableRepository.save(orderTable);

        // when
        orderTable.setNumberOfGuests(5);
        OrderTable updatedTable = orderTableService.changeNumberOfGuests(orderTable.getId(), orderTable);

        // then
        assertThat(updatedTable.getNumberOfGuests()).isEqualTo(5);
    }

    @DisplayName("손님 수가 0 미만이면 변경할 수 없다.")
    @Test
    void changeNumberOfGuests_WhenNegative_ShouldThrowException() {
        // given
        OrderTable orderTable = orderTableChange(UUID.randomUUID(), "1번 테이블", true, 4);
        orderTableRepository.save(orderTable);

        // when & then
        orderTable.setNumberOfGuests(-1);
        assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(orderTable.getId(), orderTable))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("손님 수가 0명 이상이어야 합니다.");
    }

    @DisplayName("주문 테이블이 사용중이 아니면 손님 수를 변경할 수 없다.")
    @Test
    void changeNumberOfGuests_WhenTableNotOccupied_ShouldThrowException() {
        // given
        OrderTable orderTable = orderTableChange(UUID.randomUUID(), "1번 테이블", false, 4);
        orderTableRepository.save(orderTable);

        // when & then
        orderTable.setNumberOfGuests(4);
        assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(orderTable.getId(), orderTable))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("주문 테이블이 사용중이어야 합니다.");
    }

    @DisplayName("등록된 주문 테이블을 모두 조회할 수 있다.")
    @Test
    void findAllOrderTables_Success() {
        // given
        OrderTable orderTable1 = orderTableCreate(UUID.randomUUID(),"1번 테이블");
        orderTableRepository.save(orderTable1);
        OrderTable orderTable2 = orderTableCreate(UUID.randomUUID(),"2번 테이블");
        orderTableRepository.save(orderTable2);

        // when
        List<OrderTable> orderTables = orderTableService.findAll();

        // then
        assertThat(orderTables).hasSize(2);
        assertAll(
            () -> assertThat(orderTable1).isNotNull(),
            () -> assertThat(orderTable1.getName()).isEqualTo("1번 테이블")
        );
        assertAll(
            () -> assertThat(orderTable2).isNotNull(),
            () -> assertThat(orderTable2.getName()).isEqualTo("2번 테이블")
        );
    }
}
