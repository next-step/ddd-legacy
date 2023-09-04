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

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertAll;
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

        @DisplayName("테이블 이름이 없으면 등록할 수 없다.")
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
            assertAll(
                    () -> assertThat(actual).isNotNull(),
                    () -> assertThat(Objects.requireNonNull(actual).getId()).isNotNull(),
                    () -> assertThat(Objects.requireNonNull(actual).getName()).isEqualTo(request.getName()),
                    () -> assertThat(Objects.requireNonNull(actual).getNumberOfGuests()).isEqualTo(request.getNumberOfGuests()),
                    () -> assertThat(Objects.requireNonNull(actual).isOccupied()).isEqualTo(request.isOccupied())
            );
        }
    }

    @DisplayName("테이블 앉기")
    @Nested
    class OrderTableSitTestGroup {

        @DisplayName("등록된 테이블이 아니면 테이블에 앉을 수 없다.")
        @Test
        void sitTest1() {

            // given
            given(orderTableRepository.findById(any()))
                    .willReturn(Optional.empty());

            // when + then
            assertThatExceptionOfType(NoSuchElementException.class)
                    .isThrownBy(() -> orderTableService.sit(UUID.randomUUID()));
        }

        @DisplayName("테이블에 손님이 앉음")
        @Test
        void sitTest2() {

            // given
            final OrderTable orderTable = OrderTableFixture.createOrderTableWithIsOccupied(false);

            given(orderTableRepository.findById(any()))
                    .willReturn(Optional.of(orderTable));

            // when
            OrderTable actual = orderTableService.sit(UUID.randomUUID());

            // then
            assertAll(
                    () -> assertThat(actual).isNotNull(),
                    () -> assertThat(Objects.requireNonNull(actual).isOccupied()).isTrue()
            );
        }
    }

    @DisplayName("빈 테이블로 변경")
    @Nested
    class OrderTableClearTestGroup {

        @DisplayName("등록된 테이블이 아닌 경우 빈 테이블로 변경 할 수 없다.")
        @Test
        void clearTest1() {

            // given
            given(orderTableRepository.findById(any()))
                    .willReturn(Optional.empty());

            // when + then
            assertThatExceptionOfType(NoSuchElementException.class)
                    .isThrownBy(() -> orderTableService.clear(UUID.randomUUID()));
        }

        @DisplayName("완료(COMPLETED)되지 않은 주문이 있다면 빈 테이블로 변경 할 수 없다.")
        @Test
        void clearTest2() {

            // given
            final OrderTable orderTable = OrderTableFixture.createOrderTable();

            given(orderTableRepository.findById(any()))
                    .willReturn(Optional.of(orderTable));
            given(orderRepository.existsByOrderTableAndStatusNot(any(), any()))
                    .willReturn(true);

            // when + then
            assertThatExceptionOfType(IllegalStateException.class)
                    .isThrownBy(() -> orderTableService.clear(UUID.randomUUID()));
        }

        @DisplayName("빈 테이블로 변경")
        @Test
        void clearTest3() {

            // given
            final OrderTable orderTable = OrderTableFixture.createOrderTableWithIsOccupied(false);

            given(orderTableRepository.findById(any()))
                    .willReturn(Optional.of(orderTable));
            given(orderRepository.existsByOrderTableAndStatusNot(any(), any()))
                    .willReturn(false);

            // when
            OrderTable actual = orderTableService.clear(UUID.randomUUID());

            // then
            assertAll(
                    () -> assertThat(actual).isNotNull(),
                    () -> assertThat(actual.isOccupied()).isFalse(),
                    () -> assertThat(actual.getNumberOfGuests()).isZero()
            );
        }
    }

    @DisplayName("손님 수 변경")
    @Nested
    class ChangeNumberOfGuestsTestGroup {

        @DisplayName("손님 수가 음수 값이면 변경 할 수 없다.")
        @Test
        void changeNumberOfGuestsTest1() {

            // given
            final OrderTable request = OrderTableFixture.createOrderTableWithNumberOfGuests(-1);

            // when + then
            assertThatExceptionOfType(IllegalArgumentException.class)
                    .isThrownBy(() -> orderTableService.changeNumberOfGuests(UUID.randomUUID(), request));
        }

        @DisplayName("등록된 테이블이 아니면 변경 할 수 없다.")
        @Test
        void changeNumberOfGuestsTest2() {

            // given
            final OrderTable request = OrderTableFixture.createOrderTable();

            given(orderTableRepository.findById(any()))
                    .willReturn(Optional.empty());

            // when + then
            assertThatExceptionOfType(NoSuchElementException.class)
                    .isThrownBy(() -> orderTableService.changeNumberOfGuests(UUID.randomUUID(), request));
        }

        @DisplayName("빈 테이블이면 변경 할 수 없다.")
        @Test
        void changeNumberOfGuestsTest3() {

            // given
            final OrderTable orderTable = OrderTableFixture.createOrderTableWithIsOccupied(false);
            final OrderTable request = OrderTableFixture.createOrderTable();

            given(orderTableRepository.findById(any()))
                    .willReturn(Optional.of(orderTable));

            // when + then
            assertThatExceptionOfType(IllegalStateException.class)
                    .isThrownBy(() -> orderTableService.changeNumberOfGuests(UUID.randomUUID(), request));
        }

        @DisplayName("손님 수 변경")
        @Test
        void changeNumberOfGuestsTest4() {

            // given
            final OrderTable orderTable = OrderTableFixture.createOrderTableWithIsOccupiedAndNumberOfGuests(true, 1);
            final OrderTable request = OrderTableFixture.createOrderTableWithNumberOfGuests(2);

            given(orderTableRepository.findById(any()))
                    .willReturn(Optional.of(orderTable));

            // when
            OrderTable actual = orderTableService.changeNumberOfGuests(UUID.randomUUID(), request);

            // then
            assertAll(
                    () -> assertThat(actual).isNotNull(),
                    () -> assertThat(actual.getNumberOfGuests()).isEqualTo(request.getNumberOfGuests())
            );
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