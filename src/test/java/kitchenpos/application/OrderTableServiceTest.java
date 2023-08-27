package kitchenpos.application;

import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static kitchenpos.fixture.OrderTableFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrderTable")
class OrderTableServiceTest {

    @InjectMocks
    private OrderTableService orderTableService;

    @Mock
    private OrderTableRepository orderTableRepository;

    @Mock
    private OrderRepository orderRepository;

    @Nested
    @DisplayName("주문테이블을 등록할 수 있다.")
    class create {

        @Test
        @DisplayName("등록")
        void create_1() {
            // Given
            OrderTable orderTable = createOrderTable();
            when(orderTableRepository.save(any())).thenReturn(orderTable);

            // When
            OrderTable result = orderTableService.create(orderTable);

            // Then
            assertThat(result).isEqualTo(orderTable);
        }

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("이름은 비어있거나 공백이면 예외가 발생한다.")
        void create_2(String name) {
            // When
            OrderTable orderTable = createOrderTableWithName(name);

            // Then
            assertThatThrownBy(() -> orderTableService.create(orderTable))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("주문테이블에 착석을 등록할 수 있다.")
    class sit {

        @Test
        @DisplayName("존재하지 않는 주문테이블일 경우 예외가 발생한다.")
        void sit_1() {
            // Given
            when(orderTableRepository.findById(any())).thenReturn(Optional.empty());

            // Then
            assertThatThrownBy(() -> orderTableService.sit(any()))
                    .isInstanceOf(NoSuchElementException.class);
        }

        @Test
        @DisplayName("착석 등록")
        void sit_2() {
            // Given
            OrderTable orderTable = createOrderTable();
            when(orderTableRepository.findById(any())).thenReturn(Optional.of(orderTable));

            // When
            OrderTable result = orderTableService.sit(any());

            // Then
            assertThat(result.isOccupied()).isTrue();
        }
    }

    @Nested
    @DisplayName("주문테이블을 정리할 수 있다.")
    class clear {

        @Test
        @DisplayName("존재하지 않는 주문테이블일 경우 예외가 발생한다.")
        void clear_1() {
            // Given
            when(orderTableRepository.findById(any())).thenReturn(Optional.empty());

            // Then
            assertThatThrownBy(() -> orderTableService.clear(any()))
                    .isInstanceOf(NoSuchElementException.class);
        }

        @Test
        @DisplayName("주문이 완료상태가 아니면 예외가 발생한다.")
        void clear_2() {
            // Given
            OrderTable orderTable = createOrderTable();
            when(orderTableRepository.findById(any())).thenReturn(Optional.of(orderTable));

            // When
            when(orderRepository.existsByOrderTableAndStatusNot(orderTable, OrderStatus.COMPLETED)).thenReturn(true);

            // Then
            assertThatThrownBy(() -> orderTableService.clear(any()))
                    .isInstanceOf(IllegalStateException.class);
        }

        @Test
        @DisplayName("정리")
        void clear_3() {
            // Given
            OrderTable orderTable = createOrderTable();
            when(orderTableRepository.findById(any())).thenReturn(Optional.of(orderTable));
            when(orderRepository.existsByOrderTableAndStatusNot(orderTable, OrderStatus.COMPLETED)).thenReturn(false);

            // When
            OrderTable result = orderTableService.clear(any());

            // Then
            assertAll(
                    () -> assertThat(result.getNumberOfGuests()).isZero(),
                    () -> assertThat(result.isOccupied()).isFalse()
            );
        }
    }

    @Test
    @DisplayName("주문테이블의 전체목록을 조회할 수 있다.")
    void findAll() {
        // Given
        List<OrderTable> orderTables = createOrderTables();
        when(orderTableRepository.findAll()).thenReturn(orderTables);

        // When
        List<OrderTable> findAllOrderTables = orderTableService.findAll();

        // Then
        assertThat(findAllOrderTables).hasSize(orderTables.size());
    }

    @Nested
    @DisplayName("주문테이블의 손님수를 변경할 수 있다.")
    class changeNumberOfGuests {

        @Test
        @DisplayName("손님수는 0보다 작으면 예외가 발생한다.")
        void changeNumberOfGuests_1() {
            // When
            OrderTable orderTable = createOrderTableWithNumberOfGuest(-1);

            // Then
            assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(orderTable.getId(), orderTable))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("존재하지 않는 주문테이블일 경우 예외가 발생한다.")
        void changeNumberOfGuests_2() {
            // Given
            OrderTable orderTable = createOrderTable();
            when(orderTableRepository.findById(any())).thenReturn(Optional.empty());

            // Then
            assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(any(), orderTable))
                    .isInstanceOf(NoSuchElementException.class);
        }

        @Test
        @DisplayName("사용중이 아니라면 예외가 발생한다.")
        void changeNumberOfGuests_3() {
            // Given
            OrderTable orderTable = createOrderTableWithOccupied(false);
            when(orderTableRepository.findById(any())).thenReturn(Optional.of(orderTable));

            // Then
            assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(any(), orderTable))
                    .isInstanceOf(IllegalStateException.class);
        }

        @Test
        @DisplayName("손님수 변경")
        void changeNumberOfGuests_4() {
            // Given
            OrderTable orderTable = createOrderTable();
            when(orderTableRepository.findById(any())).thenReturn(Optional.of(orderTable));

            // When
            orderTable.setNumberOfGuests(10);
            orderTable.setOccupied(true);
            OrderTable result = orderTableService.changeNumberOfGuests(any(), orderTable);

            // Then
            assertThat(result.getNumberOfGuests()).isEqualTo(10);
        }
    }

}