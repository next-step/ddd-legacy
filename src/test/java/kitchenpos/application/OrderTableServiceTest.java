package kitchenpos.application;

import java.util.List;
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
import static org.mockito.BDDMockito.given;

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

            given(orderTableRepository.save(any())).willAnswer((invocation -> invocation.getArgument(0)));

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

            given(orderTableRepository.findById(tableId)).willReturn(Optional.empty());

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

            given(orderTableRepository.findById(tableId)).willReturn(Optional.of(table));

            // when
            testService.sit(tableId);

            // then
            assertThat(table.isOccupied()).isTrue();
        }
    }

    @DisplayName("매장테이블 비움")
    @Nested
    class Clear {
        @DisplayName("등록된 테이블이어야 한다.")
        @Test
        void tableNotFound() {
            // given
            final var tableId = UUID.fromString("11111111-1111-1111-1111-111111111111");

            given(orderTableRepository.findById(tableId)).willReturn(Optional.empty());

            // when
            assertThatThrownBy(() -> testService.clear(tableId))
                    // then
                    .isInstanceOf(NoSuchElementException.class);
        }

        @DisplayName("완료되지 않은 주문이 있다면 비울 수 없다.")
        @Test
        void existsNotCompletedOrder() {
            // given
            final var tableId = UUID.fromString("11111111-1111-1111-1111-111111111111");

            final var table = new OrderTable();
            table.setOccupied(true);
            table.setNumberOfGuests(4);

            given(orderTableRepository.findById(tableId)).willReturn(Optional.of(table));
            given(orderRepository.existsByOrderTableAndStatusNot(any(), any())).willReturn(true);

            // when
            assertThatThrownBy(() -> testService.clear(tableId))
                    // then
                    .isInstanceOf(IllegalStateException.class);
        }

        @DisplayName("매장테이블을 비워짐 상태로 바꾸고 손님 수는 0으로 설정된다.")
        @ParameterizedTest(name = "기존 테이블 차지여부가 [{0}]일 때, 테이블을 비워짐 상태로 바꾸고 손님 수는 0으로 설정된다.")
        @ValueSource(booleans = {false, true})
        void clear(boolean occupiedBeforeChanged) {
            // given
            final var tableId = UUID.fromString("11111111-1111-1111-1111-111111111111");

            final var table = new OrderTable();
            table.setOccupied(occupiedBeforeChanged);
            table.setNumberOfGuests(5);

            given(orderTableRepository.findById(tableId)).willReturn(Optional.of(table));
            given(orderRepository.existsByOrderTableAndStatusNot(any(), any())).willReturn(false);

            // when
            testService.clear(tableId);

            // then
            assertAll(
                    () -> assertThat(table.isOccupied()).isFalse(),
                    () -> assertThat(table.getNumberOfGuests()).isZero()
            );
        }
    }

    @DisplayName("매장테이블 손님 수 설정")
    @Nested
    class ChangeNumberOfGuests {
        @DisplayName("손님 수는 음수가 아니어야 한다.")
        @Test
        void negativeNumberOfGuests() {
            // given
            final var tableId = UUID.fromString("11111111-1111-1111-1111-111111111111");
            final var request = new OrderTable();
            request.setNumberOfGuests(-1);

            // when
            assertThatThrownBy(() -> testService.changeNumberOfGuests(tableId, request))
                    // then
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("등록된 테이블이어야 한다.")
        @Test
        void tableNotFound() {
            // given
            final var tableId = UUID.fromString("11111111-1111-1111-1111-111111111111");
            final var request = new OrderTable();
            request.setNumberOfGuests(3);

            given(orderTableRepository.findById(tableId)).willReturn(Optional.empty());

            // when
            assertThatThrownBy(() -> testService.changeNumberOfGuests(tableId, request))
                    // then
                    .isInstanceOf(NoSuchElementException.class);
        }

        @DisplayName("차지된 상태의 테이블이어야 한다.")
        @Test
        void tableShouldBeOccupied() {
            // given
            final var tableId = UUID.fromString("11111111-1111-1111-1111-111111111111");
            final var request = new OrderTable();
            request.setNumberOfGuests(3);

            final var tableInRepo = new OrderTable();
            tableInRepo.setOccupied(false);
            given(orderTableRepository.findById(tableId)).willReturn(Optional.of(tableInRepo));

            // when
            assertThatThrownBy(() -> testService.changeNumberOfGuests(tableId, request))
                    // then
                    .isInstanceOf(IllegalStateException.class);
        }

        @DisplayName("테이블의 손님 수를 바꿀 수 있다.")
        @Test
        void changeNumberOfGuests() {
            // given
            final var tableId = UUID.fromString("11111111-1111-1111-1111-111111111111");
            final var request = new OrderTable();
            request.setNumberOfGuests(3);

            final var tableInRepo = new OrderTable();
            tableInRepo.setOccupied(true);
            tableInRepo.setNumberOfGuests(0);
            given(orderTableRepository.findById(tableId)).willReturn(Optional.of(tableInRepo));

            // when
            testService.changeNumberOfGuests(tableId, request);

            // then
            assertThat(tableInRepo.getNumberOfGuests()).isEqualTo(3);
        }
    }

    @DisplayName("모든 매장테이블을 조회할 수 있다.")
    @Test
    void findAll() {
        // given
        final var table1 = new OrderTable();
        table1.setId(UUID.fromString("11111111-1111-1111-1111-111111111111"));
        final var table2 = new OrderTable();
        table2.setId(UUID.fromString("22222222-2222-2222-2222-222222222222"));
        final var tablesInRepo = List.of(table1, table2);
        given(orderTableRepository.findAll()).willReturn(tablesInRepo);

        // when
        final var result = testService.findAll();

        // then
        assertThat(result).hasSize(2)
                .extracting(OrderTable::getId)
                .containsExactlyInAnyOrder(
                        UUID.fromString("11111111-1111-1111-1111-111111111111"),
                        UUID.fromString("22222222-2222-2222-2222-222222222222")
                );
    }
}
