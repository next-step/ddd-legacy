package kitchenpos.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import kitchenpos.util.OrderTableBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderTableServiceTest {

    @InjectMocks
    OrderTableService orderTableService;

    @Mock
    OrderTableRepository orderTableRepository;

    @Mock
    OrderRepository orderRepository;

    @DisplayName("주문 테이블 생성")
    @Nested
    class CreateOrderTable {

        @DisplayName("주문 테이블 이름이 null이거나 빈 문자열인 경우 예외를 던진다.")
        @ParameterizedTest
        @NullAndEmptySource
        void if_name_is_null_or_empty_then_throw_exception(String name) {
            // given
            var request = new OrderTable();
            request.setName(name);

            // when then
            assertThatThrownBy(() -> orderTableService.create(request))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("주문 테이블 생성 성공하면 주문 테이블을 반환한다.")
        @Test
        void if_succeed_then_return_order_table() {
            // given
            var request = new OrderTable();
            request.setName("테이블");

            given(orderTableRepository.save(any()))
                    .will(invocation -> invocation.getArgument(0));

            // when
            var orderTable = orderTableService.create(request);

            // then
            assertThat(orderTable).isNotNull();
            Assertions.assertAll(
                    () -> assertThat(orderTable.getId()).isNotNull(),
                    () -> assertThat(orderTable.getName()).isEqualTo("테이블"),
                    () -> assertThat(orderTable.getNumberOfGuests()).isZero(),
                    () -> assertThat(orderTable.isOccupied()).isFalse()
            );
        }
    }

    @DisplayName("주문 테이블 사용")
    @Nested
    class SitOrderTable {

        @DisplayName("주문 테이블이 존재하지 않는 경우 예외를 던진다.")
        @Test
        void if_order_table_does_not_exist_then_throw_exception() {
            // given
            var orderTableId = UUID.randomUUID();

            given(orderTableRepository.findById(orderTableId))
                    .willReturn(Optional.empty());

            // when then
            assertThatThrownBy(() -> orderTableService.sit(orderTableId))
                    .isInstanceOf(NoSuchElementException.class);
        }

        @DisplayName("주문 테이블을 사용하면 주문 테이블을 반환한다.")
        @Test
        void if_succeed_then_return_order_table() {
            // given
            var orderTable = OrderTableBuilder.id(UUID.randomUUID())
                    .build();

            given(orderTableRepository.findById(orderTable.getId()))
                    .willReturn(Optional.of(orderTable));

            // when
            var result = orderTableService.sit(orderTable.getId());

            // then
            assertThat(result).isNotNull();
            assertThat(result.isOccupied()).isTrue();
        }
    }

    @DisplayName("주문 테이블 비우기")
    @Nested
    class ClearOrderTable {

        @DisplayName("주문 테이블이 존재하지 않는 경우 예외를 던진다.")
        @Test
        void if_order_table_does_not_exist_then_throw_exception() {
            // given
            var orderTableId = UUID.randomUUID();

            given(orderTableRepository.findById(orderTableId))
                    .willReturn(Optional.empty());

            // when then
            assertThatThrownBy(() -> orderTableService.clear(orderTableId))
                    .isInstanceOf(NoSuchElementException.class);
        }

        @DisplayName("주문 테이블에 주문이 존재하는 경우 예외를 던진다.")
        @Test
        void if_order_exists_then_throw_exception() {
            // given
            var orderTableId = UUID.randomUUID();
            var orderTable = OrderTableBuilder.id(orderTableId)
                    .build();

            given(orderTableRepository.findById(orderTableId))
                    .willReturn(Optional.of(orderTable));
            given(orderRepository.existsByOrderTableAndStatusNot(orderTable, OrderStatus.COMPLETED))
                    .willReturn(true);

            // when then
            assertThatThrownBy(() -> orderTableService.clear(orderTableId))
                    .isInstanceOf(IllegalStateException.class);
        }

        @DisplayName("주문 테이블 비우기 성공하면 주문 테이블을 반환한다.")
        @Test
        void if_succeed_then_return_order_table() {
            // given
            var orderTableId = UUID.randomUUID();
            var orderTable = new OrderTable();
            orderTable.setId(orderTableId);

            given(orderTableRepository.findById(orderTableId))
                    .willReturn(Optional.of(orderTable));

            given(orderRepository.existsByOrderTableAndStatusNot(orderTable, OrderStatus.COMPLETED))
                    .willReturn(false);

            // when
            var result = orderTableService.clear(orderTableId);

            // then
            assertThat(result).isNotNull();
            Assertions.assertAll(
                    () -> assertThat(result.getNumberOfGuests()).isZero(),
                    () -> assertThat(result.isOccupied()).isFalse()
            );
        }
    }

    @DisplayName("주문 테이블 인원 변경")
    @Nested
    class ChangeNumberOfGuests {

        @DisplayName("인원이 음수인 경우 예외를 던진다.")
        @Test
        void if_number_of_guests_is_negative_then_throw_exception() {
            // given
            var orderTableId = UUID.randomUUID();

            var request = new OrderTable();
            request.setNumberOfGuests(-1);

            // when then
            assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(orderTableId, request))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("주문 테이블이 존재하지 않는 경우 예외를 던진다.")
        @Test
        void if_order_table_does_not_exist_then_throw_exception() {
            // given
            var orderTableId = UUID.randomUUID();
            var request = OrderTableBuilder.id(orderTableId)
                    .numberOfGuests(2)
                    .build();

            given(orderTableRepository.findById(orderTableId))
                    .willReturn(Optional.empty());

            // when then
            assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(orderTableId, request))
                    .isInstanceOf(NoSuchElementException.class);
        }

        @DisplayName("주문 테이블이 비어있는 경우 예외를 던진다.")
        @Test
        void if_order_table_is_empty_then_throw_exception() {
            // given
            var orderTableId = UUID.randomUUID();
            var orderTable = OrderTableBuilder.id(orderTableId)
                    .occupied(false)
                    .build();
            var request = OrderTableBuilder.id(orderTableId)
                    .numberOfGuests(2)
                    .build();

            given(orderTableRepository.findById(orderTableId))
                    .willReturn(Optional.of(orderTable));

            // when then
            assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(orderTableId, request))
                    .isInstanceOf(IllegalStateException.class);
        }

        @DisplayName("주문 테이블 인원 변경 성공하면 주문 테이블을 반환한다.")
        @Test
        void if_succeed_then_return_order_table() {
            // given
            var orderTableId = UUID.randomUUID();
            var orderTable = OrderTableBuilder.id(orderTableId)
                    .numberOfGuests(2)
                    .occupied(true)
                    .build();
            var request = OrderTableBuilder.id(orderTableId)
                    .numberOfGuests(4)
                    .build();

            given(orderTableRepository.findById(orderTableId))
                    .willReturn(Optional.of(orderTable));

            // when
            var result = orderTableService.changeNumberOfGuests(orderTableId, request);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getNumberOfGuests()).isEqualTo(4);
        }
    }

    @DisplayName("주문 테이블 목록 조회")
    @Nested
    class ListOrderTables {

        @DisplayName("주문 테이블이 존재하지 않는 경우 빈 목록을 반환한다.")
        @Test
        void if_order_tables_do_not_exist_then_return_empty_list() {
            // given
            given(orderTableRepository.findAll())
                    .willReturn(List.of());

            // when
            var result = orderTableService.findAll();

            // then
            assertThat(result).isEmpty();
        }

        @DisplayName("주문 테이블이 존재하는 경우 주문 테이블 목록을 반환한다.")
        @Test
        void if_order_tables_exist_then_return_order_tables() {
            // given
            var orderTable1 = OrderTableBuilder.id(UUID.randomUUID())
                    .name("테이블1")
                    .numberOfGuests(2)
                    .occupied(true)
                    .build();
            var orderTable2 = OrderTableBuilder.id(UUID.randomUUID())
                    .name("테이블2")
                    .numberOfGuests(4)
                    .occupied(false)
                    .build();

            given(orderTableRepository.findAll())
                    .willReturn(List.of(orderTable1, orderTable2));

            // when
            var result = orderTableService.findAll();

            // then
            assertThat(result).hasSize(2);
        }
    }
}
