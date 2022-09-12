package kitchenpos.application;

import kitchenpos.fixture.OrderTableFixture;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.AdditionalAnswers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class OrderTableServiceTest {

    @Mock
    private OrderTableRepository orderTableRepository;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderTableService orderTableService;

    @DisplayName("테이블 등록")
    @Nested
    class createTest {

        @DisplayName("등록 성공")
        @Test
        void createdOrderTable() {
            // given
            final OrderTable request = new OrderTable();
            request.setName("1번 테이블");
            given(orderTableRepository.save(any())).will(AdditionalAnswers.returnsFirstArg());

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
        final OrderTable menu = OrderTableFixture.create("1번 테이블", 0, false);
        given(orderTableRepository.findById(any())).willReturn(Optional.of(menu));

        // when
        final OrderTable result = orderTableService.sit(menu.getId());

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
            final OrderTable menu = OrderTableFixture.create("1번 테이블", 5, true);
            given(orderTableRepository.findById(any())).willReturn(Optional.of(menu));
            given(orderRepository.existsByOrderTableAndStatusNot(any(), any())).willReturn(false);

            // when
            OrderTable result = orderTableService.clear(menu.getId());

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
            final OrderTable menu = OrderTableFixture.create("1번 테이블", 5, true);
            given(orderTableRepository.findById(any())).willReturn(Optional.of(menu));
            given(orderRepository.existsByOrderTableAndStatusNot(any(), any())).willReturn(true);

            // then
            assertThatThrownBy(() -> orderTableService.clear(menu.getId())).isInstanceOf(IllegalStateException.class);
        }
    }

    @DisplayName("테이블에 앉아있는 손님의 인원을 변경한다.")
    @Nested
    class ChangeNumberOfGuests {

        @DisplayName("변경 성공")
        @Test
        void changedNumberOfGuests() {
            // given
            final OrderTable request =  OrderTableFixture.create(null, 5, false);
            final OrderTable menu = OrderTableFixture.create("1번 테이블", 0, true);
            menu.setId(request.getId());
            given(orderTableRepository.findById(any())).willReturn(Optional.of(menu));

            // when
            OrderTable result = orderTableService.changeNumberOfGuests(request.getId(), request);

            // then
            assertThat(result.getNumberOfGuests()).isEqualTo(5);
        }

        @DisplayName("변경하려는 인원 수는 0명 보다 작을 수 없다.")
        @Test
        void negative_numberOfGuests() {
            // given
            final OrderTable request =  OrderTableFixture.create(null, -1, false);

            // then
            assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(request.getId(), request))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("사용중인 테이블만 가능하다.")
        @Test
        void not_occupied_table() {
            // given
            final OrderTable request =  OrderTableFixture.create(null, 5, false);
            final OrderTable menu = OrderTableFixture.create("1번 테이블", 0, false);
            menu.setId(request.getId());
            given(orderTableRepository.findById(any())).willReturn(Optional.of(menu));

            // then
            assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(request.getId(), request))
                    .isInstanceOf(IllegalStateException.class);
        }
    }
}
