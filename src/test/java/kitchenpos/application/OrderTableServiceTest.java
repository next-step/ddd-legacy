package kitchenpos.application;

import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import kitchenpos.fixture.OrderFixture;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderTableServiceTest {

    @InjectMocks
    private OrderTableService orderTableService;

    @Mock
    private OrderTableRepository orderTableRepository;

    @Mock
    private OrderRepository orderRepository;

    @Nested
    @DisplayName("주문 테이블 생성")
    class OrderTableCreation {

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("주문 테이블 이름이 null이거나 비어있으면 예외가 발생한다.")
        void throwIfNameIsNullOrEmpty(String name) {
            // given
            OrderTable request = OrderFixture.주문_테이블_생성();
            request.setName(name);

            // when & then
            Assertions.assertThatThrownBy(() -> orderTableService.create(request))
                      .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("주문 테이블을 생성할 수 있다.")
        void createOrderTable() {
            // given
            OrderTable request = OrderFixture.주문_테이블_생성();

            // when
            when(orderTableRepository.save(any())).thenReturn(request);
            OrderTable result = orderTableService.create(request);

            // then
            Assertions.assertThat(result.getId()).isNotNull();
            Assertions.assertThat(result.getName()).isEqualTo(request.getName());
            Assertions.assertThat(result.getNumberOfGuests()).isZero();
            Assertions.assertThat(result.isOccupied()).isFalse();

            verify(orderTableRepository, times(1)).save(any());
        }
    }

    @Nested
    @DisplayName("테이블 착석")
    class OrderTableSit {
        @Test
        @DisplayName("테이블이 존재하지 않으면 예외가 발생한다.")
        void throwIfOrderTableNotFound() {
            // given
            OrderTable request = OrderFixture.주문_테이블_생성();
            given(orderTableRepository.findById(any())).willReturn(Optional.empty());

            // when & then
            Assertions.assertThatThrownBy(() -> orderTableService.sit(request.getId()))
                      .isInstanceOf(NoSuchElementException.class);
        }

        @Test
        @DisplayName("테이블에 착석할 수 있다.")
        void sitOrderTable() {
            // given
            OrderTable request = OrderFixture.주문_테이블_생성();
            given(orderTableRepository.findById(any())).willReturn(Optional.of(request));

            // when
            orderTableService.sit(request.getId());

            // then
            Assertions.assertThat(request.isOccupied()).isTrue();
        }
    }

    @Nested
    @DisplayName("테이블 치움")
    class OrderTableClear {
        @Test
        @DisplayName("테이블이 존재하지 않으면 예외가 발생한다.")
        void throwIfOrderTableNotFound() {
            // given
            OrderTable request = OrderFixture.주문_테이블_생성();
            given(orderTableRepository.findById(any())).willReturn(Optional.empty());

            // when & then
            Assertions.assertThatThrownBy(() -> orderTableService.clear(request.getId()))
                      .isInstanceOf(NoSuchElementException.class);
        }

        @Test
        @DisplayName("테이블에 완료되지 않은 주문이 존재하면 예외가 발생한다.")
        void throwIfOrderExists() {
            // given
            OrderTable request = OrderFixture.주문_테이블_생성();
            given(orderTableRepository.findById(any())).willReturn(Optional.of(request));
            given(orderRepository.existsByOrderTableAndStatusNot(request, OrderStatus.COMPLETED)).willReturn(true);

            // when & then
            Assertions.assertThatThrownBy(() -> orderTableService.clear(request.getId()))
                      .isInstanceOf(IllegalStateException.class);
        }

        @Test
        @DisplayName("테이블을 치울 수 있다.")
        void clearOrderTable() {
            // given
            OrderTable request = OrderFixture.주문_테이블_생성();
            request.setNumberOfGuests(10);
            request.setOccupied(true);

            given(orderTableRepository.findById(any())).willReturn(Optional.of(request));
            given(orderRepository.existsByOrderTableAndStatusNot(request, OrderStatus.COMPLETED)).willReturn(false);

            // when
            orderTableService.clear(request.getId());

            // then
            Assertions.assertThat(request.getNumberOfGuests()).isEqualTo(0);
            Assertions.assertThat(request.isOccupied()).isFalse();
        }
    }

    @Nested
    @DisplayName("테이블 손님 수 변경")
    class OrderTableChangeNumberOfGuests {
        @Test
        @DisplayName("변경하고자 하는 손님 수가 0보다 작으면 예외가 발생한다.")
        void throwIfNumberOfGuestsIsNegative() {
            // given
            OrderTable request = OrderFixture.주문_테이블_생성();
            request.setNumberOfGuests(-1);

            // when & then
            Assertions.assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(request.getId(), request))
                      .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("테이블이 사용중이 아니면 예외가 발생한다.")
        void throwIfOrderTableIsEmpty() {
            // given
            OrderTable request = OrderFixture.주문_테이블_생성();
            request.setNumberOfGuests(10);
            request.setOccupied(false);

            given(orderTableRepository.findById(any())).willReturn(Optional.of(request));

            // when & then
            Assertions.assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(request.getId(), request))
                      .isInstanceOf(IllegalStateException.class);
        }

        @Test
        @DisplayName("테이블 손님 수를 변경할 수 있다.")
        void changeNumberOfGuests() {
            // given
            OrderTable request = OrderFixture.주문_테이블_생성();
            request.setNumberOfGuests(10);
            request.setOccupied(true);

            given(orderTableRepository.findById(any())).willReturn(Optional.of(request));

            // when
            orderTableService.changeNumberOfGuests(request.getId(), request);

            // then
            Assertions.assertThat(request.getNumberOfGuests()).isEqualTo(10);
        }
    }

}