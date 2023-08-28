package kitchenpos.application;

import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import kitchenpos.support.BaseServiceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import java.util.UUID;

import static kitchenpos.fixture.OrderTableFixture.createOrderTable;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

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
}