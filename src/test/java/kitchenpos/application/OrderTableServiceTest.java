package kitchenpos.application;

import kitchenpos.application.testFixture.OrderTableFixture;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderTableRepository;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
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

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

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

    @Test
    void sit() {
    }

    @Test
    void clear() {
    }

    @Test
    void changeNumberOfGuests() {
    }

    @Test
    void findAll() {
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
            SoftAssertions.assertSoftly(softly -> {
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
            Assertions.assertThatThrownBy(() -> orderTableService.create(orderTable))
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
            Assertions.assertThat(actual.isOccupied()).isTrue();
        }

        @DisplayName("[예외] 존재하지 않는 좌석일 경우 예외가 발생한다.")
        @Test
        void notFoundSitExceptionTest() {
            // when & then
            Assertions.assertThatThrownBy(() -> orderTableService.sit(UUID.randomUUID()))
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
            SoftAssertions.assertSoftly(softly -> {
                softly.assertThat(actual.getNumberOfGuests()).isZero();
                softly.assertThat(actual.isOccupied()).isFalse();
            });
        }

        @DisplayName("[예외] 존재하지 않는 좌석일 경우 예외가 발생한다.")
        @Test
        void notFoundSitExceptionTest() {
            // when & then
            Assertions.assertThatThrownBy(() -> orderTableService.clear(UUID.randomUUID()))
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
            Assertions.assertThatThrownBy(() -> orderTableService.clear(id))
                    .isInstanceOf(IllegalStateException.class);
        }
    }
}
