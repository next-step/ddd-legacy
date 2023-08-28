package kitchenpos.application;

import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import kitchenpos.support.BaseServiceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.UUID;

import static kitchenpos.fixture.OrderTableFixture.createOrderTable;
import static org.assertj.core.api.Assertions.*;

class OrderTableServiceTest extends BaseServiceTest {
    private final OrderTableService orderTableService;
    private final OrderTableRepository orderTableRepository;

    public OrderTableServiceTest(final OrderTableService orderTableService, final OrderTableRepository orderTableRepository) {
        this.orderTableService = orderTableService;
        this.orderTableRepository = orderTableRepository;
    }

    @DisplayName("테이블은 등록이 가능하며 청소된 상태로 등록된다")
    @Test
    void test1() {
        final OrderTable orderTable = createOrderTable(5, true);

        final OrderTable createdOrderTable = orderTableService.create(orderTable);

        final OrderTable foundOrderTable = orderTableRepository.findAll().get(0);

        assertThat(createdOrderTable.getId()).isNotNull();
        assertThat(createdOrderTable.getName()).isEqualTo(orderTable.getName());
        assertThat(createdOrderTable.getNumberOfGuests()).isEqualTo(0);
        assertThat(createdOrderTable.isOccupied()).isFalse();
        assertThat(foundOrderTable.getId()).isEqualTo(createdOrderTable.getId());
    }

    @DisplayName("테이블의 이름은 공백이면 안된다.")
    @NullAndEmptySource
    @ParameterizedTest
    void test2(final String name) {
        final OrderTable orderTable = createOrderTable(name, 5, true);

        assertThatIllegalArgumentException().isThrownBy(() -> orderTableService.create(orderTable));
    }

    @DisplayName("테이블은 착석이 가능하다.")
    @Test
    void test3() {
        final OrderTable orderTable = orderTableRepository.save(createOrderTable(UUID.randomUUID(), 5, false));

        orderTableService.sit(orderTable.getId());

        assertThat(orderTable.isOccupied()).isTrue();
    }

    @DisplayName("테이블은 청소가 가능하다.")
    @Test
    void test4() {
        final OrderTable orderTable = orderTableRepository.save(createOrderTable(UUID.randomUUID(), 5, true));

        orderTableService.clear(orderTable.getId());

        assertThat(orderTable.getNumberOfGuests()).isZero();
        assertThat(orderTable.isOccupied()).isFalse();
    }

    @DisplayName("테이블은 인원수를 수정할 수 있다.")
    @Test
    void test5() {
        final OrderTable orderTable = orderTableRepository.save(createOrderTable(UUID.randomUUID(), 5, true));
        final OrderTable changeOrderTable = createOrderTable(1, true);

        orderTableService.changeNumberOfGuests(orderTable.getId(), changeOrderTable);

        assertThat(orderTable.getNumberOfGuests()).isEqualTo(changeOrderTable.getNumberOfGuests());
    }

    @DisplayName("테이블 인원수 수정시 변경 될 인원수는 0명 이상이어야 한다")
    @Test
    void test6() {
        final OrderTable orderTable = orderTableRepository.save(createOrderTable(UUID.randomUUID(), 5, true));
        final OrderTable changeOrderTable = createOrderTable(-1, true);

        assertThatIllegalArgumentException().isThrownBy(() -> orderTableService.changeNumberOfGuests(orderTable.getId(), changeOrderTable));
    }

    @DisplayName("테이블 인원수 수정시 테이블은 착석중이어야 한다.")
    @Test
    void test7() {
        final OrderTable orderTable = orderTableRepository.save(createOrderTable(UUID.randomUUID(), 5, false));
        final OrderTable changeOrderTable = createOrderTable(1, true);

        assertThatIllegalStateException().isThrownBy(() -> orderTableService.changeNumberOfGuests(orderTable.getId(), changeOrderTable));
    }

    @DisplayName("테이블은 전체 조회가 가능하다")
    @Test
    void test8() {
        final OrderTable orderTable1 = orderTableRepository.save(createOrderTable(UUID.randomUUID(), 5, false));
        final OrderTable orderTable2 = orderTableRepository.save(createOrderTable(UUID.randomUUID(), 5, false));
        final OrderTable orderTable3 = orderTableRepository.save(createOrderTable(UUID.randomUUID(), 5, false));

        final List<OrderTable> foundOrderTables = orderTableService.findAll();

        assertThat(foundOrderTables).containsExactly(orderTable1, orderTable2, orderTable3);
    }
}