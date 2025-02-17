package kitchenpos.application;

import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import kitchenpos.fixture.OrderTableFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static kitchenpos.fixture.OrderTableFixture.createOrderTable;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class OrderTableServiceTest {

    private OrderTableRepository orderTableRepository;
    private OrderRepository orderRepository;
    private OrderTableService orderTableService;

    @BeforeEach
    void setUp() {
        orderTableRepository = mock(OrderTableRepository.class);
        orderRepository = mock(OrderRepository.class);
        orderTableService = new OrderTableService(orderTableRepository, orderRepository);
    }

    @Nested
    @DisplayName("가게 테이블 생성할 수 있다.")
    class CreateOrderTable {

        @Test
        @DisplayName("가게 테이블을 정상 생성")
        void create() {
            // given
            OrderTable request = new OrderTable();
            request.setName("1번 테이블");

            given(orderTableRepository.save(any(OrderTable.class)))
                .willAnswer(invocation -> {
                    OrderTable saved = invocation.getArgument(0);
                    saved.setId(UUID.randomUUID());
                    return saved;
                });

            // when
            OrderTable created = orderTableService.create(request);

            // then
            assertThat(created.getName()).isEqualTo("1번 테이블");
            assertThat(created.getNumberOfGuests()).isZero();
            assertThat(created.isOccupied()).isFalse();
            verify(orderTableRepository).save(any(OrderTable.class));
        }

        @DisplayName("가게 테이블명은 비어있을 수 없다.")
        @ParameterizedTest(name = "가게 테이블명 : `{0}`")
        @NullAndEmptySource
        void create_fail_no_name(final String name) {
            // given
            OrderTable request = new OrderTable();
            request.setName(name);

            // when & then
            assertThatThrownBy(() -> orderTableService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Test
    @DisplayName("테이블 앉히기")
    void sit() {
        // given
        OrderTable orderTable = createOrderTable();

        given(orderTableRepository.findById(orderTable.getId()))
            .willReturn(Optional.of(orderTable));

        // when
        OrderTable sat = orderTableService.sit(orderTable.getId());

        // then
        assertThat(sat.isOccupied()).isTrue();
    }

    @DisplayName("테이블 정리")
    @Nested
    class ClearOrderTable {

        @Test
        @DisplayName("테이블 정상 정리")
        void clear() {
            // given
            OrderTable orderTable = OrderTableFixture.createOccupiedOrderTable(3);

            given(orderTableRepository.findById(orderTable.getId()))
                .willReturn(Optional.of(orderTable));
            given(orderRepository.existsByOrderTableAndStatusNot(any(OrderTable.class), any(OrderStatus.class)))
                .willReturn(false);

            // when
            OrderTable cleared = orderTableService.clear(orderTable.getId());

            // then
            assertThat(cleared.isOccupied()).isFalse();
            assertThat(cleared.getNumberOfGuests()).isZero();
        }

        @Test
        @DisplayName("테이블을 정리하기 위해서는 주문이 완료여야 한다.")
        void clear_fail_invalid_order_status() {
            // given
            OrderTable orderTable = OrderTableFixture.createOccupiedOrderTable(3);

            given(orderTableRepository.findById(orderTable.getId()))
                .willReturn(Optional.of(orderTable));
            given(orderRepository.existsByOrderTableAndStatusNot(any(OrderTable.class), any(OrderStatus.class)))
                .willReturn(true);

            // when, then
            assertThatThrownBy(() -> orderTableService.clear(orderTable.getId()))
                .isInstanceOf(IllegalStateException.class);
        }
    }

    @DisplayName("테이블 고객수 변경하기")
    @Nested
    class ChangeNumberOfGuestOrderTable {

        @Test
        @DisplayName("사용 가능한 테이블만 인원 수를 변경 할 수 있다.")
        void changeNumberOfGuestsWithEmptyTable() {
            // given
            OrderTable orderTable = createOrderTable();

            OrderTable request = new OrderTable();
            request.setNumberOfGuests(6);

            given(orderTableRepository.findById(orderTable.getId()))
                .willReturn(Optional.of(orderTable));

            // when & then
            assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(orderTable.getId(), request))
                .isInstanceOf(IllegalStateException.class);
        }

        @Test
        @DisplayName("변경할 인원 수는 0명 이상이어야 한다.")
        void changeNumberOfGuestsWithNegativeNumber() {
            // given
            OrderTable request = OrderTableFixture.createOccupiedOrderTable(-1);

            // when & then
            assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(request.getId(), request))
                .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Test
    @DisplayName("가게 테이블 전부 가져오기")
    void findAll() {
        // given
        OrderTable table1 = createOrderTable("익명 테이블1");
        OrderTable table2 = createOrderTable("익명 테이블2");
        OrderTable table3 = createOrderTable("익명 테이블3");

        given(orderTableRepository.findAll())
            .willReturn(List.of(table1, table2, table3));

        // when
        List<OrderTable> orderTables = orderTableService.findAll();

        // then
        assertThat(orderTables).hasSize(3);
        assertThat(orderTables.get(0).getName()).isEqualTo("익명 테이블1");
        assertThat(orderTables.get(1).getName()).isEqualTo("익명 테이블2");
        assertThat(orderTables.get(2).getName()).isEqualTo("익명 테이블3");
    }
}
