package kitchenpos.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("OrderTable 은")
class OrderTableServiceTest {

    private final OrderTableRepository orderTableRepository = mock(OrderTableRepository.class);
    private final OrderRepository orderRepository = mock(OrderRepository.class);
    private final OrderTableService orderTableService = new OrderTableService(
        orderTableRepository,
        orderRepository
    );

    @Nested
    @DisplayName("추가할 수 있다.")
    class 추가할_수_있다 {

        @ParameterizedTest(name = "{0} 인 경우 추가 할 수 없다.")
        @DisplayName("이름이")
        @NullAndEmptySource
        void 이름이_공백_혹은_없다면_추가_할_수_없다(String name) {
            // given
            final OrderTable orderTable = createOrderTable(
                name,
                3,
                false
            );

            // when // then
            assertThatIllegalArgumentException()
                .isThrownBy(() -> orderTableService.create(orderTable));
        }

        @Test
        @DisplayName("이름이 있는 경우 추가 할 수 있다.")
        void 이름이_있는_경우_추가_할_수_있다() {
            // given
            final OrderTable orderTable = createOrderTable(
                "test",
                0,
                true
            );

            doReturn(orderTable).when(orderTableRepository).save(any());

            // when
            final OrderTable actual = orderTableService.create(orderTable);

            // then
            assertAll(
                () -> assertThat(actual.getName()).isEqualTo(orderTable.getName()),
                () -> assertThat(actual.getNumberOfGuests()).isEqualTo(orderTable.getNumberOfGuests()),
                () -> assertThat(actual.isEmpty()).isEqualTo(orderTable.isEmpty())
            );
        }
    }

    @Nested
    @DisplayName("앉을 수 있다.")
    class 앉을_수_있다 {

        @Test
        @DisplayName("orderTable 이 존재해야 앉을 수 있다.")
        void orderTable_이_존재해야_앉을_수_있다() {
            // given
            final UUID orderTableId = UUID.randomUUID();
            final OrderTable orderTable = createOrderTable(
                orderTableId,
                "test",
                1,
                true
            );

            doReturn(Optional.of(orderTable)).when(orderTableRepository).findById(orderTableId);

            // when
            final OrderTable actual = orderTableService.sit(orderTableId);

            // then
            assertThat(actual.isEmpty()).isEqualTo(false);
        }

        @Test
        @DisplayName("존재하지 않는다면 앉을 수 없다.")
        void orderTable_이_존재하지_않는다면_앉을_수_없다() {
            // given
            final UUID orderTableId = UUID.randomUUID();
            doReturn(Optional.empty()).when(orderTableRepository).findById(orderTableId);

            // when // then
            assertThatThrownBy(() -> orderTableService.sit(orderTableId))
                .isInstanceOf(NoSuchElementException.class);
        }
    }

    @Nested
    @DisplayName("비울 수 있다.")
    class 비울_수_있다 {

        @Test
        @DisplayName("존재하지 않는다면 비울 수 없다")
        void orderTable_이_존재하지_않는다면_비울_수_없다() {
            // given
            final UUID orderTableId = UUID.randomUUID();
            doReturn(Optional.empty()).when(orderTableRepository).findById(orderTableId);

            // when // then
            assertThatThrownBy(() -> orderTableService.clear(orderTableId))
                .isInstanceOf(NoSuchElementException.class);
        }

        @Test
        @DisplayName("order 상태가 완료가 되지 않으면 비울 수 없다.")
        void order_상태가_완료가_되지_않으면_비울_수_없다() {
            // given
            final UUID orderTableId = UUID.randomUUID();
            final OrderTable orderTable = createOrderTable(
                orderTableId,
                "test",
                3,
                false
            );

            doReturn(Optional.of(orderTable)).when(orderTableRepository).findById(orderTableId);
            doReturn(true).when(orderRepository)
                .existsByOrderTableAndStatusNot(orderTable, OrderStatus.COMPLETED);

            // when // then
            assertThatIllegalStateException()
                .isThrownBy(() -> orderTableService.clear(orderTableId));
        }
    }

    @Nested
    @DisplayName("손님의 수를 변경할 수 있다.")
    class 손님의_수를_변경할_수_있다 {

        @Nested
        @DisplayName("변경할 손님의")
        class 변경할_손님의 {

            @Test
            @DisplayName("수가 0 보다 작은 경우 변경할 수 없다.")
            void 수가_0_보다_작은_경우_변경할_수_없다() {
                // given
                final OrderTable orderTable = new OrderTable();
                orderTable.setNumberOfGuests(-1);

                // when // then
                assertThatIllegalArgumentException()
                    .isThrownBy(() -> orderTableService.changeNumberOfGuests(UUID.randomUUID(),
                        orderTable));
            }

            @ParameterizedTest(name = "{0} 인 경우")
            @DisplayName("수가 0 이상 인 경우 변경할 수 있다.")
            @ValueSource(ints = {0, 1})
            void 수가_0_이상_인_경우_변경할_수_있다(int numberOfGuests) {
                // given
                final UUID orderTableId = UUID.randomUUID();
                final OrderTable orderTable = createOrderTable(
                    orderTableId,
                    "test",
                    numberOfGuests,
                    false
                );

                doReturn(Optional.of(orderTable)).when(orderTableRepository).findById(orderTableId);

                // when
                final OrderTable actual = orderTableService.changeNumberOfGuests(orderTableId, orderTable);

                // then
                assertThat(actual.getNumberOfGuests()).isEqualTo(numberOfGuests);
            }
        }

        @Test
        @DisplayName("변경할 orderTable 이 비어 있다면 변경할 수 없다.")
        void 변경할_orderTable_이_비어_있다면_변경할_수_없다() {
            // given
            final UUID orderTableId = UUID.randomUUID();
            final OrderTable orderTable = createOrderTable(
                orderTableId,
                "test",
                3,
                false
            );
            orderTable.setId(orderTableId);
            orderTable.setEmpty(false);

            doReturn(Optional.of(orderTable)).when(orderTableRepository).findById(orderTableId);
            doReturn(true).when(orderRepository)
                .existsByOrderTableAndStatusNot(orderTable, OrderStatus.COMPLETED);

            // when // then
            assertThatIllegalStateException()
                .isThrownBy(() -> orderTableService.clear(orderTableId));
        }
    }

    private OrderTable createOrderTable(
        UUID id,
        String name,
        int numberOfGuests,
        boolean empty
    ) {
        final OrderTable orderTable = new OrderTable();
        orderTable.setId(id);
        orderTable.setName(name);
        orderTable.setNumberOfGuests(numberOfGuests);
        orderTable.setEmpty(empty);
        return orderTable;
    }

    private OrderTable createOrderTable(
        String name,
        int numberOfGuests,
        boolean empty
    ) {
        return createOrderTable(
            UUID.randomUUID(),
            name,
            numberOfGuests,
            empty
        );
    }
}
