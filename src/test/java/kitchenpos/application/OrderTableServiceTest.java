package kitchenpos.application;

import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderTable;
import kitchenpos.fixture.OrderTableFixture;
import kitchenpos.spy.SpyOrderTableRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class OrderTableServiceTest {

    private OrderTableService orderTableService;

    @Spy
    private SpyOrderTableRepository orderTableRepository;

    @Mock
    private OrderRepository orderRepository;

    @BeforeEach
    void beforeEach() {
        orderTableService = new OrderTableService(orderTableRepository, orderRepository);
    }

    @DisplayName("테이블 등록")
    @Nested
    class OrderTableCreateTestGroup {

        @DisplayName("테이블 이름이 없으면 예외 발생")
        @ParameterizedTest(name = "테이블 이름: {0}")
        @NullAndEmptySource
        void createTest1(String name) {

            // given
            final OrderTable request = OrderTableFixture.createOrderTableWithName(name);

            // when + then
            assertThatExceptionOfType(IllegalArgumentException.class)
                    .isThrownBy(() -> orderTableService.create(request));
        }

        @DisplayName("테이블 등록됨")
        @Test
        void createTest2() {

            // given
            final OrderTable request = OrderTableFixture.createOrderTable();

            // when
            OrderTable actual = orderTableService.create(request);

            // then
            assertThat(actual).isNotNull();
            assertThat(actual.getId()).isNotNull();
            assertThat(actual.getName()).isEqualTo(request.getName());
            assertThat(actual.getNumberOfGuests()).isEqualTo(request.getNumberOfGuests());
            assertThat(actual.isOccupied()).isEqualTo(request.isOccupied());
        }
    }

    @DisplayName("테이블 앉기")
    @Nested
    class OrderTableSitTestGroup {

        @DisplayName("등록된 테이블이 아니면 예외 발생")
        @Test
        void sitTest1() {

            // given
            final UUID orderTableId = UUID.randomUUID();

            given(orderTableRepository.findById(any()))
                    .willReturn(Optional.empty());

            // when + then
            assertThatExceptionOfType(NoSuchElementException.class)
                    .isThrownBy(() -> orderTableService.sit(orderTableId));
        }

        @DisplayName("테이블에 손님이 앉음")
        @Test
        void sitTest2() {

            // given
            final UUID orderTableId = UUID.randomUUID();
            final OrderTable orderTable = OrderTableFixture.createOrderTableWithIsOccupied(false);

            given(orderTableRepository.findById(any()))
                    .willReturn(Optional.of(orderTable));

            // when
            OrderTable actual = orderTableService.sit(orderTableId);

            // then
            assertThat(actual).isNotNull();
            assertThat(actual.isOccupied()).isTrue();
        }
    }

    @DisplayName("빈 테이블로 변경")
    @Nested
    class OrderTableClearTestGroup {

        @DisplayName("등록된 테이블이 아닌 경우 예외 발생")
        @Test
        void clearTest1() {

            // given
            final UUID orderTableId = UUID.randomUUID();

            given(orderTableRepository.findById(any()))
                    .willReturn(Optional.empty());

            // when + then
            assertThatExceptionOfType(NoSuchElementException.class)
                    .isThrownBy(() -> orderTableService.clear(orderTableId));
        }

        @DisplayName("완료(COMPLETED)되지 않은 주문이 있다면 예외 발생")
        @Test
        void clearTest2() {

            // given
            final UUID orderTableId = UUID.randomUUID();
            final OrderTable orderTable = OrderTableFixture.createOrderTable();

            given(orderTableRepository.findById(any()))
                    .willReturn(Optional.of(orderTable));
            given(orderRepository.existsByOrderTableAndStatusNot(any(), any()))
                    .willReturn(true);

            // when + then
            assertThatExceptionOfType(IllegalStateException.class)
                    .isThrownBy(() -> orderTableService.clear(orderTableId));
        }

        @DisplayName("빈 테이블로 변경")
        @Test
        void clearTest3() {

            // given
            final UUID orderTableId = UUID.randomUUID();
            final OrderTable orderTable = OrderTableFixture.createOrderTableWithIsOccupied(false);

            given(orderTableRepository.findById(any()))
                    .willReturn(Optional.of(orderTable));
            given(orderRepository.existsByOrderTableAndStatusNot(any(), any()))
                    .willReturn(false);

            // when
            OrderTable actual = orderTableService.clear(orderTableId);

            // then
            assertThat(actual).isNotNull();
            assertThat(actual.isOccupied()).isFalse();
            assertThat(actual.getNumberOfGuests()).isZero();
        }
    }

    @DisplayName("손님 수 변경")
    @Nested
    class ChangeNumberOfGuestsTestGroup {

        @DisplayName("손님 수가 음수 값이면 예외 발생")
        @Test
        void changeNumberOfGuestsTest1() {

            // given
            final UUID orderTableId = UUID.randomUUID();
            final OrderTable request = OrderTableFixture.createOrderTableWithNumberOfGuests(-1);

            // when + then
            assertThatExceptionOfType(IllegalArgumentException.class)
                    .isThrownBy(() -> orderTableService.changeNumberOfGuests(orderTableId, request));
        }

        @DisplayName("등록된 테이블이 아니면 예외 발생")
        @Test
        void changeNumberOfGuestsTest2() {

            // given
            final UUID orderTableId = UUID.randomUUID();
            final OrderTable request = OrderTableFixture.createOrderTable();

            given(orderTableRepository.findById(any()))
                    .willReturn(Optional.empty());

            // when + then
            assertThatExceptionOfType(NoSuchElementException.class)
                    .isThrownBy(() -> orderTableService.changeNumberOfGuests(orderTableId, request));
        }

        @DisplayName("빈 테이블이면 예외 발생")
        @Test
        void changeNumberOfGuestsTest3() {

            // given
            final UUID orderTableId = UUID.randomUUID();
            final OrderTable orderTable = OrderTableFixture.createOrderTableWithIsOccupied(false);
            final OrderTable request = OrderTableFixture.createOrderTable();

            given(orderTableRepository.findById(any()))
                    .willReturn(Optional.of(orderTable));

            // when + then
            assertThatExceptionOfType(IllegalStateException.class)
                    .isThrownBy(() -> orderTableService.changeNumberOfGuests(orderTableId, request));
        }

        @DisplayName("손님 수 변경")
        @Test
        void changeNumberOfGuestsTest4() {

            // given
            final UUID orderTableId = UUID.randomUUID();
            final OrderTable orderTable = OrderTableFixture.createOrderTableWithIsOccupiedAndNumberOfGuests(true, 1);
            final OrderTable request = OrderTableFixture.createOrderTableWithNumberOfGuests(2);

            given(orderTableRepository.findById(any()))
                    .willReturn(Optional.of(orderTable));

            // when
            OrderTable actual = orderTableService.changeNumberOfGuests(orderTableId, request);

            // then
            assertThat(actual).isNotNull();
            assertThat(actual.getNumberOfGuests()).isEqualTo(request.getNumberOfGuests());
        }
    }

    @DisplayName("등록된 테이블을 모두 조회")
    @Test
    void findAllTest() {

        // given
        final OrderTable orderTable = OrderTableFixture.createOrderTable();

        given(orderTableService.findAll())
                .willReturn(List.of(orderTable));

        // when
        List<OrderTable> actual = orderTableService.findAll();

        // then
        assertThat(actual).isNotNull();
        assertThat(actual.size()).isOne();
    }
}