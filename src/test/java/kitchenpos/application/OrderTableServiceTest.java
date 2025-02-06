package kitchenpos.application;

import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import kitchenpos.domain.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static kitchenpos.fixture.TestFixture.makeTestOrderTable;
import static kitchenpos.fixture.TestFixture.makeTestProduct;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderTableServiceTest {
    private final OrderTableRepository orderTableRepository = mock(OrderTableRepository.class);
    private final OrderRepository orderRepository = mock(OrderRepository.class);
    private final OrderTableService orderTableService = new OrderTableService(orderTableRepository, orderRepository);

    @DisplayName("매장 테이블은 등록이 가능하다")
    @Test
    void create() {
        // given
        OrderTable orderTable = makeTestOrderTable("1번 테이블");
        when(orderTableRepository.save(any(OrderTable.class))).thenReturn(orderTable);

        // when
        OrderTable resultOrderTable = orderTableService.create(orderTable);

        // then
        assertThat(resultOrderTable.getId()).isNotNull();
        assertThat(resultOrderTable.getName()).isEqualTo(orderTable.getName());
        assertThat(resultOrderTable.getNumberOfGuests()).isEqualTo(orderTable.getNumberOfGuests());
        assertThat(resultOrderTable.isOccupied()).isEqualTo(orderTable.isOccupied());

    }

    @DisplayName("매장 테이블명은 비어있다면 에러를 발생시킨다.")
    @ParameterizedTest
    @NullAndEmptySource
    void nullName(String name) {
        // given
        OrderTable orderTable = makeTestOrderTable(name);
        // then
        assertThatThrownBy(() -> orderTableService.create(orderTable)).isInstanceOf(IllegalArgumentException.class);
    }


    @Test
    void sit() {
    }

    @Test
    void clear() {
    }

    @Test
    void changeNumberOfGuests() {
    }
}