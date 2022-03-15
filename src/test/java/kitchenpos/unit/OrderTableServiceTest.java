package kitchenpos.unit;

import kitchenpos.application.OrderTableService;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static kitchenpos.unit.fixture.OrderTableFixture.createOrderTable;
import static kitchenpos.unit.fixture.OrderTableFixture.테이블_1번;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderTableServiceTest {
    @Mock
    private OrderTableRepository orderTableRepository;
    @Mock
    private OrderRepository orderRepository;

    private OrderTableService orderTableService;

    @BeforeEach
    void setUp() {
        orderTableService = new OrderTableService(orderTableRepository, orderRepository);
    }

    @DisplayName("주문 테이블을 등록한다")
    @Test
    void create() {
        // given
        when(orderTableRepository.save(any(OrderTable.class))).thenReturn(테이블_1번);

        // when
        OrderTable saveOrderTable = orderTableService.create(테이블_1번);

        // then
        assertThat(saveOrderTable.getName()).isEqualTo(테이블_1번.getName());
        assertThat(saveOrderTable.getNumberOfGuests()).isZero();
        assertThat(saveOrderTable.isEmpty()).isTrue();
    }

    @DisplayName("테이블 이름을 입력하지 않은 경우 등록되지 않는다")
    @ParameterizedTest
    @NullSource
    @EmptySource
    void createInvalidName(String name) {
        assertThatThrownBy(() -> orderTableService.create(createOrderTable(name)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("테이블을 착석 상태로 변경한다")
    @Test
    void sit() {
        // given
        when(orderTableRepository.findById(any(UUID.class))).thenReturn(Optional.of(테이블_1번));

        // when
        OrderTable sitTable = orderTableService.sit(UUID.randomUUID());

        // then
        assertThat(sitTable.isEmpty()).isFalse();
    }

    @DisplayName("테이블을 비어있음 상태로 변경한다")
    @Test
    void clear() {
        // given
        when(orderTableRepository.findById(any(UUID.class))).thenReturn(Optional.of(테이블_1번));
        when(orderRepository.existsByOrderTableAndStatusNot(테이블_1번, OrderStatus.COMPLETED)).thenReturn(false);

        // when
        OrderTable clearTable = orderTableService.clear(UUID.randomUUID());

        // then
        assertThat(clearTable.isEmpty()).isTrue();
        assertThat(clearTable.getNumberOfGuests()).isZero();
    }

    @DisplayName("주문 상태가 완료가 아니면 테이블을 비어있음 상태로 변경하지 못한다")
    @Test
    void clearNotCompleted() {
        // given
        when(orderTableRepository.findById(any(UUID.class))).thenReturn(Optional.of(테이블_1번));
        when(orderRepository.existsByOrderTableAndStatusNot(테이블_1번, OrderStatus.COMPLETED)).thenReturn(true);

        // when
        // then
        assertThatThrownBy(() -> orderTableService.clear(UUID.randomUUID()))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("테이블의 인원수를 변경한다")
    @Test
    void changeNumberOfGuests() {
        // given
        OrderTable beforeTable = createOrderTable("1번", 0, false);
        OrderTable afterTable = createOrderTable("1번", 5, false);
        when(orderTableRepository.findById(any(UUID.class))).thenReturn(Optional.of(beforeTable));

        // when
        OrderTable changeTable = orderTableService.changeNumberOfGuests(UUID.randomUUID(), afterTable);

        // then
        assertThat(changeTable.getNumberOfGuests()).isEqualTo(5);
    }

    @DisplayName("변경하려는 인원수는 0명 이상이어야 한다")
    @ParameterizedTest
    @ValueSource(ints = {-1})
    void changeNumberOfGuests(int numberOfGuests) {
        // given
        OrderTable afterTable = createOrderTable("1번", numberOfGuests, false);

        // when
        // then
        assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(UUID.randomUUID(), afterTable))
                .isInstanceOf(IllegalArgumentException.class);
    }
}