package kitchenpos.application;

import kitchenpos.application.testFixture.OrderTableFixture;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderTableRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@DisplayName("주문테이블(OrderTable) 서비스 테스트")
@ExtendWith(MockitoExtension.class)
class OrderTableServiceTest {

    @Mock
    private OrderTableRepository orderTableRepository;

    @Mock
    private OrderRepository orderRepository;

    private OrderTableService orderTableService;

    @BeforeEach
    void setUp() {
        orderTableService = new OrderTableService(orderTableRepository, orderRepository);
    }

    @DisplayName("좌석에 앉은 손님 수를 변경시 ")
    @Nested
    class ChangeGuestNumbers {

        @Test
        @DisplayName("손님 수가 변경된다.")
        void changedGuestNumberTest() {
            // given
            var id = UUID.randomUUID();
            var originalOrderTable = OrderTableFixture.newOne(id, "1번 테이블", 4, true);
            var updatedOrderTable = OrderTableFixture.newOne(id, 2);
            given(orderTableRepository.findById(any())).willReturn(Optional.of(originalOrderTable));

            // when
            var actual = orderTableService.changeNumberOfGuests(id, updatedOrderTable);

            // then
            assertThat(actual.getNumberOfGuests()).isEqualTo(2);
        }

        @Test
        @DisplayName("[예외] 변경할 손님 수는 음수일 수 없다.")
        void negativeGuestNumbersExceptionTest() {
            // given
            var id = UUID.randomUUID();
            var updatedOrderTable = OrderTableFixture.newOne(id, -1);

            // when & then
            assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(id, updatedOrderTable))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("[예외] 존재하지 않는 주문 테이블일 경우 예외가 발생한다.")
        void notFoundOrderTableExceptionTest() {
            // given
            var id = UUID.randomUUID();
            var updatedOrderTable = OrderTableFixture.newOne(id, 4);
            given(orderTableRepository.findById(any())).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(id, updatedOrderTable))
                    .isInstanceOf(NoSuchElementException.class);
        }

        @Test
        @DisplayName("[예외] '미사용' 중인 주문 테이블일 경우 예외가 발생한다.")
        void notOccupiedOrderTableExceptionTest() {
            // given
            var id = UUID.randomUUID();
            var originalOrderTable = OrderTableFixture.newOne(id, "1번 테이블", 4, false);
            var updatedOrderTable = OrderTableFixture.newOne(id, 4);
            given(orderTableRepository.findById(any())).willReturn(Optional.of(originalOrderTable));

            // when & then
            assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(id, updatedOrderTable))
                    .isInstanceOf(IllegalStateException.class);
        }
    }

    @DisplayName("주문 테이블 전체를 조회한다.")
    @Test
    void findAll() {
        // given
        var orderTable1 = OrderTableFixture.newOne("1번 테이블");
        var orderTable2 = OrderTableFixture.newOne("2번 테이블");
        given(orderTableRepository.findAll()).willReturn(List.of(orderTable1, orderTable2));

        // when
        var actual = orderTableService.findAll();

        // then
        assertThat(actual).containsAll(List.of(orderTable1, orderTable2));
    }

    @Nested
    @DisplayName("주문 테이블을 생성시")
    class Create {

        @DisplayName("손님 수 0명, '미사용중' 상태로 주문 테이블이 생성된다.")
        @Test
        void createTest() {
            // given
            var orderTable = OrderTableFixture.newOne("1번 테이블");
            given(orderTableRepository.save(any())).willReturn(orderTable);

            // when
            var actual = orderTableService.create(orderTable);

            // then
            assertSoftly(softly -> {
                softly.assertThat(actual.getName()).isEqualTo("1번 테이블");
                softly.assertThat(actual.getNumberOfGuests()).isEqualTo(0);
                softly.assertThat(actual.isOccupied()).isFalse();
            });
        }

        @DisplayName("주문 테이블명은 빈 문자열이나 null일 수 없다.")
        @EmptySource
        @NullSource
        @ParameterizedTest
        void invalidOrderTableNameTest(String orderTableName) {
            // given
            var orderTable = OrderTableFixture.newOne(orderTableName);

            // when & then
            assertThatThrownBy(() -> orderTableService.create(orderTable))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @DisplayName("손님을 좌석에 앉힐때,")
    @Nested
    class Sit {

        @DisplayName("좌석의 상태는 '사용중'으로 변경된다.")
        @Test
        void sitTest() {
            // given
            var orderTable = OrderTableFixture.newOne("1번 테이블");
            given(orderTableRepository.findById(any())).willReturn(Optional.of(orderTable));

            // when
            var actual = orderTableService.sit(orderTable.getId());

            // then
            assertThat(actual.isOccupied()).isTrue();
        }

        @DisplayName("[예외] 존재하지 않는 좌석일 경우 예외가 발생한다.")
        @Test
        void notFoundSitExceptionTest() {
            // when & then
            assertThatThrownBy(() -> orderTableService.sit(UUID.randomUUID()))
                    .isInstanceOf(NoSuchElementException.class);
        }
    }

    @DisplayName("주문 테이블을 초기화할 때,")
    @Nested
    class Clear {

        @DisplayName("좌석에 앉은 손님 수는 0명이 되고, 좌석의 상태는 '미사용중'으로 변경 된다.")
        @Test
        void sitTest() {
            // given
            var id = UUID.randomUUID();
            var orderTable = OrderTableFixture.newOne(id, "1번 테이블", 4, true);
            given(orderTableRepository.findById(any())).willReturn(Optional.of(orderTable));
            given(orderRepository.existsByOrderTableAndStatusNot(any(), any())).willReturn(false);

            // when
            var actual = orderTableService.clear(id);

            // then
            assertSoftly(softly -> {
                softly.assertThat(actual.getNumberOfGuests()).isZero();
                softly.assertThat(actual.isOccupied()).isFalse();
            });
        }

        @DisplayName("[예외] 존재하지 않는 좌석일 경우 예외가 발생한다.")
        @Test
        void notFoundSitExceptionTest() {
            // when & then
            assertThatThrownBy(() -> orderTableService.clear(UUID.randomUUID()))
                    .isInstanceOf(NoSuchElementException.class);
        }

        @DisplayName("[예외] 주문 상태가 '완료'가 아닐 경우 예외가 발생한다.")
        @Test
        void notCompletedExceptionTest() {
            // given
            var id = UUID.randomUUID();
            var orderTable = OrderTableFixture.newOne("1번 테이블");
            given(orderTableRepository.findById(any())).willReturn(Optional.of(orderTable));
            given(orderRepository.existsByOrderTableAndStatusNot(any(), any())).willReturn(true);

            // when & then
            assertThatThrownBy(() -> orderTableService.clear(id))
                    .isInstanceOf(IllegalStateException.class);
        }
    }
}
