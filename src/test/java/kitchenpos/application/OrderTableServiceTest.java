package kitchenpos.application;

import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static kitchenpos.fixture.application.OrderTableFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderTableServiceTest {
    private OrderTable 테이블_1번;
    private OrderTable 테이블_2번;
    private UUID 테이블_1번_ID;
    private UUID 테이블_2번_ID;

    @Mock
    private OrderTableRepository orderTableRepository;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderTableService orderTableService;

    @BeforeEach
    void setUp() {
        테이블_1번 = 테이블_생성(false);
        테이블_2번 = 테이블_생성(true);
        테이블_1번_ID = 테이블_1번.getId();
        테이블_2번_ID = 테이블_2번.getId();
    }

    @Nested
    @DisplayName("등록")
    class OrderTableCreate {
        @Test
        @DisplayName("주문 테이블을 생성한다")
        void orderTableCreate() {
            when(orderTableRepository.save(any())).thenReturn(테이블_1번);
            OrderTable result = orderTableService.create(테이블_1번);

            assertThat(result.getId()).isEqualTo(테이블_1번_ID);
            assertThat(result.getName()).isEqualTo("1번");
            assertThat(result.getNumberOfGuests()).isZero();
            assertThat(result.isOccupied()).isFalse();
        }

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("주문 테이블의 이름은 null 이거나 공백 일 수 없다.")
        void orderTableNameIsNotNullAndEmpty(String name) {
            OrderTable request = 테이블_1번_생성(name);

            assertThatThrownBy(() -> orderTableService.create(request))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("손님이 테이블에 앉는다.")
    class OrderTableOccupied {
        @Test
        @DisplayName("손님이 테이블에 앉는다.")
        void orderTableOccupired() {
            // given
            when(orderTableRepository.findById(any())).thenReturn(Optional.of(테이블_1번));

            // when
            OrderTable result = orderTableService.sit(테이블_1번_ID);

            // then
            assertThat(result.isOccupied()).isTrue();
        }

        @Test
        @DisplayName("테이블이 등록되어 있어야 한다.")
        void throwNoSuchElementException() {
            // given
            when(orderTableRepository.findById(any())).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> orderTableService.sit(테이블_1번_ID))
                    .isInstanceOf(NoSuchElementException.class);
        }
    }

    @Nested
    @DisplayName("테이블 초기화")
    class OrderTableClear {
        @Test
        @DisplayName("테이블을 초기화 한다")
        void orderTableClear() {
            // given
            when(orderTableRepository.findById(any())).thenReturn(Optional.of(테이블_2번));
            when(orderRepository.existsByOrderTableAndStatusNot(테이블_2번, OrderStatus.COMPLETED)).thenReturn(false);

            // when
            OrderTable result = orderTableService.clear(테이블_2번_ID);

            // then
            assertThat(result.getNumberOfGuests()).isZero();
            assertThat(result.isOccupied()).isFalse();
        }

        @Test
        @DisplayName("테이블이 등록되어 있어야 한다")
        void throwNoSuchElementException() {
            when(orderTableRepository.findById(any())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> orderTableService.sit(테이블_2번_ID))
                    .isInstanceOf(NoSuchElementException.class);
        }

        @Test
        @DisplayName("주문의 상태가 완료여야 한다")
        void throwIllegalStateException() {
            when(orderTableRepository.findById(any())).thenReturn(Optional.of(테이블_2번));
            when(orderRepository.existsByOrderTableAndStatusNot(테이블_2번, OrderStatus.COMPLETED)).thenReturn(true);

            assertThatThrownBy(() -> orderTableService.clear(테이블_2번_ID))
                    .isInstanceOf(IllegalStateException.class);
        }
    }

    @Nested
    @DisplayName("테이블 손님 인원 변경")
    class OrderTableChangeNumberOfGuest {
        @Test
        @DisplayName("테이블의 손님의 수를 변경한다")
        void changeNumber() {
            when(orderTableRepository.findById(테이블_2번_ID)).thenReturn(Optional.ofNullable(테이블_2번));
            OrderTable request = 손님_인원_변경(4);

            OrderTable result = orderTableService.changeNumberOfGuests(테이블_2번_ID, request);

            assertThat(result.getId()).isEqualTo(테이블_2번_ID);
            assertThat(result.getNumberOfGuests()).isEqualTo(request.getNumberOfGuests());
        }

        @Test
        @DisplayName("손님의 수는 0이상 이어야 한다")
        void greaterThenZero() {
            OrderTable request = 손님_인원_변경(-1);

            assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(테이블_2번_ID, request))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("테이블이 등록되어 있어야 한다")
        void throwNoSuchElementException() {
            when(orderTableRepository.findById(any())).thenReturn(Optional.empty());
            OrderTable request = 손님_인원_변경(4);

            assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(테이블_2번_ID, request))
                    .isInstanceOf(NoSuchElementException.class);
        }

        @Test
        @DisplayName("테이블이 사용중이어야 한다")
        void tableIsOccupied() {
            OrderTable request = 손님_인원_변경(4);
            when(orderTableRepository.findById(테이블_1번_ID)).thenReturn(Optional.ofNullable(테이블_1번));

            assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(테이블_1번_ID, request))
                    .isInstanceOf(IllegalStateException.class);
        }
    }

    @Test
    @DisplayName("테이블의 목록을 볼 수 있다.")
    void orderTables() {
        when(orderTableRepository.findAll()).thenReturn(List.of(테이블_1번, 테이블_2번));

        List<OrderTable> orderTables = orderTableService.findAll();

        assertThat(orderTables.size()).isEqualTo(2);
        assertThat(orderTables).containsOnly(테이블_1번, 테이블_2번);
    }
}
