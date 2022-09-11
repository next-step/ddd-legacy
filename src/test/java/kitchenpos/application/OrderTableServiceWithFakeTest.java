package kitchenpos.application;

import kitchenpos.domain.Order;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import kitchenpos.domain.OrderType;
import kitchenpos.fixture.OrderFixture;
import kitchenpos.fixture.OrderTableFixture;
import kitchenpos.fixture.fake.InMemoryOrderRepository;
import kitchenpos.fixture.fake.InMemoryOrderTableRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

public class OrderTableServiceWithFakeTest {

    private OrderTableRepository orderTableRepository;
    private OrderRepository orderRepository;
    private OrderTableService orderTableService;

    private OrderTable usedTable;
    private OrderTable notUsedTable;

    @BeforeEach
    void setUp() {
        orderTableRepository = new InMemoryOrderTableRepository();
        orderRepository = new InMemoryOrderRepository();
        orderTableService = new OrderTableService(orderTableRepository, orderRepository);

        usedTable = OrderTableFixture.create("1번 테이블", 5, true);
        orderTableRepository.save(usedTable);
        notUsedTable = OrderTableFixture.create("2번 테이블", 0, false);
        orderTableRepository.save(notUsedTable);
    }

    @DisplayName("테이블 등록")
    @Nested
    class createTest {

        @DisplayName("등록 성공")
        @Test
        void createdOrderTable() {
            // given
            final OrderTable request = new OrderTable();
            request.setName("1번 테이블");

            // when
            final OrderTable result = orderTableService.create(request);

            // then
            assertAll(() -> {
                assertThat(result.getId()).isNotNull();
                assertThat(result.getName()).isEqualTo("1번 테이블");
                assertThat(result.getNumberOfGuests()).isEqualTo(0);
                assertThat(result.isOccupied()).isFalse();
            });
        }

        @ParameterizedTest(name = "이름은 비어있을 수 없다. name={0}")
        @NullAndEmptySource
        void null_or_empty_name(String name) {
            // given
            final OrderTable request = new OrderTable();
            request.setName(name);

            // then
            assertThatThrownBy(() -> orderTableService.create(request)).isInstanceOf(IllegalArgumentException.class);
        }
    }

    @DisplayName("테이블이 사용중이라고 변경한다.")
    @Test
    void sit() {
        // given
        final OrderTable orderTable = OrderTableFixture.create("1번 테이블", 0, false);
        orderTableRepository.save(orderTable);

        // when
        final OrderTable result = orderTableService.sit(orderTable.getId());

        // then
        assertThat(result.isOccupied()).isTrue();
    }

    @DisplayName("테이블이 비어있다고 변경한다.")
    @Nested
    class clearTest {

        @DisplayName("테이블 비우기 성공")
        @Test
        void clearedOrderTable() {
            // given
            final Order order = OrderFixture.create(OrderType.EAT_IN, OrderStatus.COMPLETED, usedTable);
            orderRepository.save(order);

            // when
            OrderTable result = orderTableService.clear(usedTable.getId());

            // then
            assertAll(() -> {
                assertThat(result.getNumberOfGuests()).isZero();
                assertThat(result.isOccupied()).isFalse();
            });
        }

        @DisplayName("주문상태가 완료이여야만 한다.")
        @Test
        void not_completed_order_status() {
            // given
            final Order order = OrderFixture.create(OrderType.EAT_IN, OrderStatus.SERVED, usedTable);
            orderRepository.save(order);

            // then
            assertThatThrownBy(() -> orderTableService.clear(usedTable.getId())).isInstanceOf(IllegalStateException.class);
        }
    }

    @DisplayName("테이블에 앉아있는 손님의 인원을 변경한다.")
    @Nested
    class ChangeNumberOfGuests {

        @DisplayName("변경 성공")
        @Test
        void changedNumberOfGuests() {
            // given
            final OrderTable request = new OrderTable();
            request.setNumberOfGuests(5);

            // when
            OrderTable result = orderTableService.changeNumberOfGuests(usedTable.getId(), request);

            // then
            assertThat(result.getNumberOfGuests()).isEqualTo(5);
        }

        @DisplayName("변경하려는 인원 수는 0명 보다 작을 수 없다.")
        @Test
        void negative_numberOfGuests() {
            // given
            final OrderTable request = new OrderTable();
            request.setNumberOfGuests(-1);

            // then
            assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(usedTable.getId(), request))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("사용중인 테이블만 가능하다.")
        @Test
        void not_occupied_table() {
            // given
            final OrderTable request = new OrderTable();
            request.setNumberOfGuests(5);

            // then
            assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(notUsedTable.getId(), request))
                    .isInstanceOf(IllegalStateException.class);
        }
    }
}
