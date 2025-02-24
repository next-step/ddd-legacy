package kitchenpos.application;

import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import kitchenpos.fixture.OrderTableFixture;
import kitchenpos.repository.InMemoryOrderRepository;
import kitchenpos.repository.InMemoryOrderTableRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatException;
import static org.junit.jupiter.api.Assertions.assertAll;

class OrderTableServiceTest {

    private OrderTableService orderTableService;

    private OrderTableRepository orderTableRepository;

    private OrderRepository orderRepository;

    @BeforeEach
    void setUp() {
        orderTableRepository = new InMemoryOrderTableRepository();
        orderRepository = new InMemoryOrderRepository();
        orderTableService = new OrderTableService(orderTableRepository, orderRepository);
    }

    @Nested
    @DisplayName("주문 테이블 등록")
    class CreateOrderTable {

        @Test
        @DisplayName("이름으로 주문 테이블을 생성한다.")
        void createOrderTable() {
            // given
            final OrderTable request = OrderTableFixture.createOrderTableRequest("테이블1", 0, false);

            // when
            final OrderTable result = orderTableService.create(request);

            // then
            final OrderTable found = orderTableRepository.findById(result.getId()).orElse(null);
            assertThat(found).isNotNull();
            assertAll(
                    () -> assertThat(found.getName()).isEqualTo(request.getName()),
                    () -> assertThat(found.getNumberOfGuests()).isEqualTo(request.getNumberOfGuests()),
                    () -> assertThat(found.isOccupied()).isFalse()
            );

        }
    }

    @Nested
    @DisplayName("착석")
    class SitOrderTable {

        @Test
        @DisplayName("주문 테이블을 사용중으로 변경한다.")
        void sitOrderTable() {
            // given
            final OrderTable request = OrderTableFixture.createOrderTableRequest("테이블1", 0, false);
            final OrderTable orderTable = orderTableRepository.save(request);

            // when
            final OrderTable result = orderTableService.sit(orderTable.getId());

            // then
            final OrderTable found = orderTableRepository.findById(result.getId()).orElse(null);
            assertThat(found).isNotNull();
            assertThat(found.isOccupied()).isTrue();
        }
    }

    @Nested
    @DisplayName("정리")
    class ClearOrderTable {

        @Test
        @DisplayName("주문 테이블을 미사용으로 변경하고 손님수를 0명으로 설정한다.")
        void clearOrderTable() {
            // given
            final OrderTable request = OrderTableFixture.createOrderTableRequest("테이블1", 0, false);
            final OrderTable orderTable = orderTableRepository.save(request);

            // when
            final OrderTable result = orderTableService.clear(orderTable.getId());

            // then
            final OrderTable found = orderTableRepository.findById(result.getId()).orElse(null);
            assertThat(found.isOccupied()).isFalse();
            assertThat(found.getNumberOfGuests()).isZero();
        }

        @Test
        @DisplayName("현재 주문 테이블의 모든 주문이 완료(COMPLETED)되면 정리할 수 있다.")
        void clearOrderTableWithCompletedOrders() {
            // given
            final OrderTable request = OrderTableFixture.createOrderTableRequest("테이블1", 0, false);
            final OrderTable orderTable = orderTableRepository.save(request);

            // when
            final OrderTable result = orderTableService.clear(orderTable.getId());

            // then
            final OrderTable found = orderTableRepository.findById(result.getId()).orElse(null);
            assertThat(found.isOccupied()).isFalse();
            assertThat(found.getNumberOfGuests()).isZero();
        }
    }

    @Nested
    @DisplayName("손님수 변경")
    class ChangeNumberOfGuests {

        @Test
        @DisplayName("손님 수는 0명 이상이어야 한다.")
        void changeNumberOfGuests() {
            // given
            final OrderTable orderTable = orderTableRepository.save(OrderTableFixture.createOrderTable("테이블1", 0, true));
            final OrderTable request = new OrderTable();
            request.setNumberOfGuests(3);

            // when
            final OrderTable result = orderTableService.changeNumberOfGuests(orderTable.getId(), request);

            // then
            final OrderTable found = orderTableRepository.findById(result.getId()).orElse(null);
            assertThat(found).isNotNull();
            assertThat(found.getNumberOfGuests()).isEqualTo(request.getNumberOfGuests());
        }

        @Test
        @DisplayName("미사용인 주문 테이블만 변경할 수 있다.")
        void changeNumberOfGuestsForNonOccupiedTable() {
            // given
            final OrderTable orderTable = orderTableRepository.save(OrderTableFixture.createOrderTable("테이블1", 0, false));
            final OrderTable request = new OrderTable();
            request.setNumberOfGuests(3);

            // when & then
            assertThatException()
                    .isThrownBy(() -> orderTableService.changeNumberOfGuests(orderTable.getId(), request))
                    .isInstanceOf(IllegalStateException.class);
        }
    }

    @Nested
    @DisplayName("주문 테이블 조회")
    class FindAllOrderTables {

        @Test
        @DisplayName("모든 주문 테이블 목록을 조회한다.")
        void findAllOrderTables() {
            // given
            OrderTable orderTable1 = orderTableRepository.save(OrderTableFixture.createOrderTable("테이블1", 0, false));
            OrderTable orderTable2 = orderTableRepository.save(OrderTableFixture.createOrderTable("테이블2", 0, false));

            // when
            final List<OrderTable> result = orderTableService.findAll();

            // then
            assertThat(result)
                    .hasSize(2)
                    .extracting(OrderTable::getId)
                    .containsExactlyInAnyOrder(orderTable1.getId(), orderTable2.getId());
        }
    }

}
