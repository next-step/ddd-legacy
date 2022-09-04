package kitchenpos.application;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderTableServiceTest {
    @Mock
    private OrderTableRepository orderTableRepository;
    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderTableService testService;

    @DisplayName("매장테이블 등록")
    @Nested
    class Create {
        @DisplayName("이름은 비어 있지 않아야 한다.")
        @ParameterizedTest(name = "이름은 [{0}]이 아니어야 한다.")
        @NullAndEmptySource
        void nullOrEmptyName(String name) {
            // given
            final var request = new OrderTable();
            request.setName(name);

            // when
            assertThatThrownBy(() -> testService.create(request))
                    // then
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("매장테이블을 등록하면 차지되지 않은 상태이고, 손님수는 0이다.")
        @Test
        void create() {
            // given
            final var request = new OrderTable();
            request.setName("table 1");

            when(orderTableRepository.save(any())).thenAnswer((invocation -> invocation.getArgument(0)));

            // when
            final var result = testService.create(request);

            // then
            assertAll(
                    () -> assertThat(result.getId()).isNotNull(),
                    () -> assertThat(result.getName()).isEqualTo("table 1"),
                    () -> assertThat(result.isOccupied()).isFalse(),
                    () -> assertThat(result.getNumberOfGuests()).isZero()
            );
        }
    }

    @DisplayName("매장테이블 차지")
    @Nested
    class Sit {
        @DisplayName("등록된 테이블이어야 한다.")
        @Test
        void tableNotFound() {
            // given
            final var tableId = UUID.fromString("11111111-1111-1111-1111-111111111111");

            when(orderTableRepository.findById(tableId)).thenReturn(Optional.empty());

            // when
            assertThatThrownBy(() -> testService.sit(tableId))
                    // then
                    .isInstanceOf(NoSuchElementException.class);
        }

        @DisplayName("매장테이블을 차지된 상태로 바꿀 수 있다.")
        @ParameterizedTest(name = "기존 테이블 차지여부가 [{0}]일 때, 테이블을 차지 상태로 바꿀 수 있다.")
        @ValueSource(booleans = {false, true})
        void sit(boolean occupiedBeforeChanged) {
            // given
            final var tableId = UUID.fromString("11111111-1111-1111-1111-111111111111");

            final var table = new OrderTable();
            table.setOccupied(occupiedBeforeChanged);

            when(orderTableRepository.findById(tableId)).thenReturn(Optional.of(table));

            // when
            testService.sit(tableId);

            // then
            assertThat(table.isOccupied()).isTrue();
        }
    }
}