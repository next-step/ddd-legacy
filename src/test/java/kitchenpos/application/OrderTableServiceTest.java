package kitchenpos.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import kitchenpos.constant.Fixtures;
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

@ExtendWith(MockitoExtension.class)
class OrderTableServiceTest {
    @Mock
    private OrderTableRepository orderTableRepository;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderTableService orderTableService;

    @BeforeEach
    void setUp() {
        Fixtures.initialize();
    }

    @DisplayName("테이블 등록")
    @Nested
    public class CreateTest {
        @DisplayName("정상 동작")
        @Test
        void create() {
            // given
            given(orderTableRepository.save(any())).willReturn(any());

            // when
            orderTableService.create(Fixtures.ORDER_TABLE);

            // then
            then(orderTableRepository).should().save(any());
        }

        @DisplayName("이름이 null 또는 빈값일 수 없음")
        @ParameterizedTest
        @NullAndEmptySource
        void createWithNullOrEmptyName(String name) {
            // given
            OrderTable request = new OrderTable();
            request.setName(name);

            // when then
            assertThatIllegalArgumentException().isThrownBy(
                () -> orderTableService.create(request)
            );
        }
    }

    @DisplayName("테이블에 앉을 경우 점유 상태로 변경 되어야함")
    @Test
    void sit() {
        // given
        UUID orderTableId = UUID.randomUUID();

        OrderTable request = new OrderTable();
        request.setId(orderTableId);
        request.setName("SampleOrderTable");
        request.setNumberOfGuests(0);
        request.setOccupied(false);

        given(orderTableRepository.findById(orderTableId)).willReturn(Optional.of(request));

        // when
        OrderTable result = orderTableService.sit(orderTableId);

        // then
        assertThat(result.isOccupied()).isTrue();
    }

    @DisplayName("테이블 치우기")
    @Nested
    public class ClearTest {
        @DisplayName("정상 동작")
        @Test
        void clear() {
            // given
            UUID orderTableId = UUID.randomUUID();

            OrderTable orderTable = new OrderTable();
            orderTable.setId(orderTableId);
            orderTable.setName("SampleOrderTable");
            orderTable.setNumberOfGuests(10);
            orderTable.setOccupied(true);

            given(orderTableRepository.findById(orderTableId)).willReturn(Optional.of(orderTable));
            given(orderRepository.existsByOrderTableAndStatusNot(any(), any()))
                .willReturn(Boolean.FALSE);

            // when
            OrderTable result = orderTableService.clear(orderTableId);

            // then
            assertAll(
                () -> assertThat(result.getNumberOfGuests()).isZero(),
                () -> assertThat(result.isOccupied()).isFalse()
            );
        }

        @DisplayName("해당 테이블에 주문완료 상태가 아닌 다른 주문이 있을 경우 변경할 수 없다")
        @Test
        void clearWithInvalidCondition() {
            // given
            UUID orderTableId = UUID.randomUUID();

            OrderTable orderTable = new OrderTable();
            orderTable.setId(orderTableId);
            orderTable.setName("SampleOrderTable");
            orderTable.setNumberOfGuests(10);
            orderTable.setOccupied(true);

            given(orderTableRepository.findById(orderTableId)).willReturn(Optional.of(orderTable));
            given(orderRepository.existsByOrderTableAndStatusNot(orderTable, OrderStatus.COMPLETED))
                .willReturn(Boolean.TRUE);

            // when then
            assertThatIllegalStateException().isThrownBy(
                () -> orderTableService.clear(orderTableId)
            );
        }
    }

    @DisplayName("테이블 손님수 변경")
    @Nested
    public class ChangeNumberOfGuestsTest {
        @DisplayName("정상 동작")
        @Test
        void changeNumberOfGuests() {
            // given
            UUID orderTableId = UUID.randomUUID();

            OrderTable request = new OrderTable();
            request.setName("SampleOrderTable");
            request.setNumberOfGuests(10);

            OrderTable orderTable = new OrderTable();
            orderTable.setId(orderTableId);
            orderTable.setName("SampleOrderTable");
            orderTable.setNumberOfGuests(0);
            orderTable.setOccupied(true);

            given(orderTableRepository.findById(orderTableId)).willReturn(Optional.of(orderTable));

            // when
            OrderTable result = orderTableService.changeNumberOfGuests(orderTableId, request);

            // then
            assertThat(result.getNumberOfGuests()).isEqualTo(request.getNumberOfGuests());
        }

        @DisplayName("변경하려는 손님수는 0명 이상 이어야함")
        @Test
        void changeNumberOfGuestsWithInvalidNumberOfGuests() {
            // given
            UUID orderTableId = UUID.randomUUID();

            OrderTable request = new OrderTable();
            request.setName("SampleOrderTable");
            request.setNumberOfGuests(-1);

            // when then
            assertThatIllegalArgumentException().isThrownBy(
                () -> orderTableService.changeNumberOfGuests(orderTableId, request)
            );
        }

        @DisplayName("테이블이 점유 상태인 경우에만 가능함")
        @Test
        void changeNumberOfGuestsWithInvalidField() {
            // given
            UUID orderTableId = UUID.randomUUID();

            OrderTable request = new OrderTable();
            request.setName("SampleOrderTable");
            request.setNumberOfGuests(10);

            OrderTable orderTable = new OrderTable();
            orderTable.setId(orderTableId);
            orderTable.setName("SampleOrderTable");
            orderTable.setNumberOfGuests(0);
            orderTable.setOccupied(false);

            given(orderTableRepository.findById(orderTableId)).willReturn(Optional.of(orderTable));

            // when then
            assertThatIllegalStateException().isThrownBy(
                () -> orderTableService.changeNumberOfGuests(orderTableId, request)
            );
        }
    }

    @DisplayName("모든 테이블 조회")
    @Test
    void findAll() {
        // given
        given(orderTableRepository.findAll()).willReturn(List.of(Fixtures.ORDER_TABLE));

        // when
        List<OrderTable> results = orderTableService.findAll();

        // then
        assertThat(results).containsExactly(Fixtures.ORDER_TABLE);
    }
}
