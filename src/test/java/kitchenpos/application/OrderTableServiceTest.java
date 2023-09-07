package kitchenpos.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.NoSuchElementException;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import kitchenpos.fixture.OrderFixture;
import kitchenpos.fixture.OrderTableFixture;
import kitchenpos.repository.OrderFakeRepository;
import kitchenpos.repository.OrderTableFakeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class OrderTableServiceTest {

    private OrderTableRepository orderTableRepository;

    private OrderRepository orderRepository;

    private OrderTableService sut;

    @BeforeEach
    void setUp() {
        orderTableRepository = new OrderTableFakeRepository();
        orderRepository = new OrderFakeRepository();
        sut = new OrderTableService(orderTableRepository, orderRepository);
    }

    @Nested
    class 주문_테이블_등록 {

        @DisplayName("주문 테이블을 등록한다")
        @Test
        void testCreate() {
            // given
            OrderTable orderTable = OrderTableFixture.createEmpty();

            // when
            OrderTable actual = sut.create(orderTable);

            // then
            OrderTable expected = orderTableRepository.findById(actual.getId()).get();
            assertThat(actual).isEqualTo(expected);
        }

        @DisplayName("빈 문자열이나 null인 이름으로는 주문 테이블을 등록할 수 없다")
        @ParameterizedTest
        @NullAndEmptySource
        void testCreate(String name) {
            // given
            OrderTable orderTable = OrderTableFixture.createEmpty(name);

            // when // then
            assertThatThrownBy(() -> sut.create(orderTable))
                .isExactlyInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    class 주문_테이블_점유 {

        @DisplayName("주문 테이블을 점유 처리한다")
        @Test
        void testSit() {
            // given
            OrderTable orderTable = orderTableRepository.save(OrderTableFixture.createEmpty());

            // when
            OrderTable actual = sut.sit(orderTable.getId());

            // then
            assertThat(actual.isOccupied()).isTrue();
        }

        @DisplayName("존재하지 않는 주문 테이블 id로는 주문 테이블을 점유 처리할 수 없다")
        @Test
        void testSitWhenNotExistOrderTable() {
            // given
            OrderTable orderTable = OrderTableFixture.createEmpty();

            // when // then
            assertThatThrownBy(() -> sut.sit(orderTable.getId()))
                .isExactlyInstanceOf(NoSuchElementException.class);
        }
    }

    @Nested
    class 주문_테이블_초기화 {

        @DisplayName("주문 테이블을 초기화한다")
        @Test
        void testClear() {
            // given
            OrderTable orderTable = orderTableRepository.save(OrderTableFixture.createEmpty());

            // when
            OrderTable actual = sut.clear(orderTable.getId());

            // then
            assertThat(actual.isOccupied()).isFalse();
            assertThat(actual.getNumberOfGuests()).isZero();
        }

        @DisplayName("완료된 주문이 있는 경우, 주문 테이블을 초기화할 수 없다")
        @Test
        void testClearWhenCompletedOrderNotExist() {
            // given
            OrderTable orderTable = orderTableRepository.save(OrderTableFixture.createOccupied(3));
            orderRepository.save(OrderFixture.createEatIn(orderTable));

            // when // then
            assertThatThrownBy(() -> sut.clear(orderTable.getId()))
                .isExactlyInstanceOf(IllegalStateException.class);
        }
    }

    @Nested
    class 주문_테이블_손님수_지정 {

        @DisplayName("주문 테이블의 손님 수를 지정한다")
        @Test
        void testChangeNumberOfGuests() {
            // given
            OrderTable orderTable = orderTableRepository.save(OrderTableFixture.createOccupied(0));
            int expectedNumberOfGuests = 3;

            // when
            OrderTable actual = sut.changeNumberOfGuests(orderTable.getId(), OrderTableFixture.createOccupied(expectedNumberOfGuests));

            // then
            assertThat(actual.isOccupied()).isTrue();
            assertThat(actual.getNumberOfGuests()).isEqualTo(expectedNumberOfGuests);
        }

        @DisplayName("주문 테이블의 손님 수를 지정한다")
        @ParameterizedTest
        @ValueSource(ints = {-5, -1})
        void testChangeNumberOfGuestsWhenNumberOfGuestsValueIsNegative(int numberOfGuests) {
            // given
            OrderTable orderTable = OrderTableFixture.createOccupied(0);

            // when // then
            assertThatThrownBy(() -> sut.changeNumberOfGuests(orderTable.getId(), OrderTableFixture.createOccupied(numberOfGuests)));
        }

        @DisplayName("주문 테이블이 점유중인 상태가 아니라면 손님 수를 지정할 수 없다")
        @Test
        void testChangeNumberOfGuestsWhenOrderTableIsNotOccupied() {
            // given
            OrderTable orderTable = OrderTableFixture.createEmpty();

            // when // then
            assertThatThrownBy(() -> sut.changeNumberOfGuests(orderTable.getId(), OrderTableFixture.createOccupied(1)));
        }

        @DisplayName("존재하지 않는 주문 테이블의 손님 수는 지정할 수 없다")
        @Test
        void testChangeNumberOfGuestsWhenOrderTableIsNotExist() {
            // given
            OrderTable orderTable = orderTableRepository.save(OrderTableFixture.createEmpty());

            // when // then
            assertThatThrownBy(() -> sut.changeNumberOfGuests(orderTable.getId(), OrderTableFixture.createOccupied(1)));
        }
    }

    @Nested
    class 주문_테이블_조회 {

        @DisplayName("모든 주문 테이블을 조회한다")
        @Test
        void testFindAll() {
            // given
            OrderTable orderTable1 = OrderTableFixture.createEmpty();
            OrderTable orderTable2 = OrderTableFixture.create();
            orderTableRepository.save(orderTable1);
            orderTableRepository.save(orderTable2);

            // when
            List<OrderTable> actual = sut.findAll();

            // then
            assertThat(actual).hasSize(2);
            assertThat(actual.get(0)).isEqualTo(orderTable1);
            assertThat(actual.get(1)).isEqualTo(orderTable2);
        }
    }
}
