package kitchenpos.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import kitchenpos.fixture.OrderFixture;
import kitchenpos.fixture.OrderTableFixture;
import kitchenpos.repository.OrderFakeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderTableServiceTest {

    @Mock
    private OrderTableRepository orderTableRepository;

    private OrderRepository orderRepository;

    private OrderTableService sut;

    @BeforeEach
    void setUp() {
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

            given(orderTableRepository.save(any(OrderTable.class))).willReturn(orderTable);

            // when
            OrderTable actual = sut.create(orderTable);

            // then
            assertThat(actual).isEqualTo(orderTable);
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
            OrderTable orderTable = OrderTableFixture.createEmpty();

            given(orderTableRepository.findById(orderTable.getId())).willReturn(Optional.of(orderTable));

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

            given(orderTableRepository.findById(orderTable.getId())).willReturn(Optional.empty());

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
            OrderTable orderTable = OrderTableFixture.createEmpty();

            given(orderTableRepository.findById(orderTable.getId())).willReturn(Optional.of(orderTable));

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
            OrderTable orderTable = OrderTableFixture.createOccupied(3);
            orderRepository.save(OrderFixture.createEatIn(orderTable));

            given(orderTableRepository.findById(orderTable.getId())).willReturn(Optional.of(orderTable));

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
            OrderTable orderTable = OrderTableFixture.createOccupied(0);
            int expectedNumberOfGuests = 3;

            given(orderTableRepository.findById(orderTable.getId())).willReturn(Optional.of(orderTable));

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
            OrderTable orderTable = OrderTableFixture.createEmpty();

            given(orderTableRepository.findById(orderTable.getId())).willReturn(Optional.empty());

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
            var orderTables = List.of(OrderTableFixture.createEmpty(), OrderTableFixture.create());

            given(orderTableRepository.findAll()).willReturn(orderTables);

            // when
            List<OrderTable> actual = sut.findAll();

            // then
            assertThat(actual).hasSize(2);
            assertThat(actual.get(0)).isEqualTo(orderTables.get(0));
            assertThat(actual.get(1)).isEqualTo(orderTables.get(1));
        }
    }
}
