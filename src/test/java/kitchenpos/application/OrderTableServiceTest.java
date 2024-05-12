package kitchenpos.application;

import kitchenpos.ApplicationMockTest;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static kitchenpos.fixture.OrderTableFixture.NAME_1번;
import static kitchenpos.fixture.OrderTableFixture.NAME_2번;
import static kitchenpos.fixture.OrderTableFixture.changeNumberOfGuestsRequest;
import static kitchenpos.fixture.OrderTableFixture.orderTableCreateRequest;
import static kitchenpos.fixture.OrderTableFixture.orderTableResponse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@DisplayName("주문테이블 서비스 테스트")
@ApplicationMockTest
class OrderTableServiceTest {
    @Mock
    OrderTableRepository orderTableRepository;
    @Mock
    OrderRepository orderRepository;

    @InjectMocks
    OrderTableService orderTableService;

    @DisplayName("주문테이블을 등록한다.")
    @Test
    void createOrderTable() {
        // given
        OrderTable ORDER_TABLE_1번 = orderTableResponse(NAME_1번, 0, false);
        when(orderTableRepository.save(any())).thenReturn(ORDER_TABLE_1번);
        OrderTable request = orderTableCreateRequest(NAME_1번);

        // when
        OrderTable result = orderTableService.create(request);

        // then
        assertAll(
                () -> assertThat(result.getId()).isEqualTo(ORDER_TABLE_1번.getId()),
                () -> assertThat(result.getName()).isEqualTo(NAME_1번),
                () -> assertThat(result.getNumberOfGuests()).isZero(),
                () -> assertThat(result.isOccupied()).isFalse()
        );
    }

    @DisplayName("주문테이블을 등록할 때, 이름이 공백이면 예외가 발생한다.")
    @NullAndEmptySource
    @ParameterizedTest
    void createOrderTable_NullOrEmptyNameException(String name) {
        // given
        OrderTable request = orderTableCreateRequest(name);

        // when
        // then
        assertThatThrownBy(() -> orderTableService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("고객이 주문 테이블에 앉는다.")
    @Test
    void sitOrderTable() {
        // given
        OrderTable ORDER_TABLE_1번 = orderTableResponse(NAME_1번, 0, false);
        when(orderTableRepository.findById(any())).thenReturn(Optional.of(ORDER_TABLE_1번));

        // when
        OrderTable result = orderTableService.sit(ORDER_TABLE_1번.getId());

        // then
        assertAll(
                () -> assertThat(result.getId()).isEqualTo(ORDER_TABLE_1번.getId()),
                () -> assertThat(result.getName()).isEqualTo(NAME_1번),
                () -> assertThat(result.getNumberOfGuests()).isZero(),
                () -> assertThat(result.isOccupied()).isTrue()
        );
    }

    @DisplayName("해당 주문 테이블이 초기화 된다.")
    @Test
    void clearOrderTable() {
        // given
        OrderTable ORDER_TABLE_1번 = orderTableResponse(NAME_1번, 2, true);
        when(orderTableRepository.findById(any())).thenReturn(Optional.of(ORDER_TABLE_1번));
        when(orderRepository.existsByOrderTableAndStatusNot(ORDER_TABLE_1번, OrderStatus.COMPLETED)).thenReturn(false);

        // when
        OrderTable result = orderTableService.clear(ORDER_TABLE_1번.getId());

        // then
        assertAll(
                () -> assertThat(result.getId()).isEqualTo(ORDER_TABLE_1번.getId()),
                () -> assertThat(result.getName()).isEqualTo(NAME_1번),
                () -> assertThat(result.getNumberOfGuests()).isZero(),
                () -> assertThat(result.isOccupied()).isFalse()
        );
    }

    @DisplayName("사용여부를 수정하려고 하는 주문테이블이 미리 등록되어있지 않으면 예외가 발생한다.")
    @Test
    void changeOccupied_notExistsOrderTableException() {
        // given
        UUID orderTableId = UUID.randomUUID();
        when(orderTableRepository.findById(any())).thenReturn(Optional.empty());

        // when
        // then
        assertAll(
                () -> assertThatThrownBy(() -> orderTableService.sit(orderTableId))
                    .isInstanceOf(NoSuchElementException.class),
                () -> assertThatThrownBy(() -> orderTableService.clear(orderTableId))
                        .isInstanceOf(NoSuchElementException.class)
        );
    }

    @DisplayName("해당 주문 테이블이 초기화 할 때, 주문의 상태가 '주문완료'가 아니면 예외 발생한다.")
    @Test
    void clearOrderTable_notCompletedOrderStatusException() {
        // given
        OrderTable ORDER_TABLE_1번 = orderTableResponse(NAME_1번, 2, true);
        when(orderTableRepository.findById(any())).thenReturn(Optional.of(ORDER_TABLE_1번));
        when(orderRepository.existsByOrderTableAndStatusNot(ORDER_TABLE_1번, OrderStatus.COMPLETED)).thenReturn(true);
        UUID orderTableId = ORDER_TABLE_1번.getId();

        // when
        // then
        assertThatThrownBy(() -> orderTableService.clear(orderTableId))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("해당 주문 테이블 고객의 수를 변경한다.")
    @Test
    void changeNumberOfGuestsOrderTable() {
        // given
        OrderTable ORDER_TABLE_1번 = orderTableResponse(NAME_1번, 2, true);
        when(orderTableRepository.findById(any())).thenReturn(Optional.of(ORDER_TABLE_1번));
        OrderTable request = changeNumberOfGuestsRequest(4);

        // when
        OrderTable result = orderTableService.changeNumberOfGuests(ORDER_TABLE_1번.getId(), request);

        // then
        assertAll(
                () -> assertThat(result.getId()).isEqualTo(ORDER_TABLE_1번.getId()),
                () -> assertThat(result.getName()).isEqualTo(NAME_1번),
                () -> assertThat(result.getNumberOfGuests()).isEqualTo(4),
                () -> assertThat(result.isOccupied()).isTrue()
        );
    }

    @DisplayName("고객의 수를 변경하려고 하는 해당 테이블이 없으면 예외 발생한다.")
    @Test
    void changeNumberOfGuestsOrderTable_notExistsOrderTableException() {
        // given
        OrderTable ORDER_TABLE_1번 = orderTableResponse(NAME_1번, 2, true);
        UUID orderTableId = ORDER_TABLE_1번.getId();
        when(orderTableRepository.findById(any())).thenReturn(Optional.empty());
        OrderTable request = changeNumberOfGuestsRequest(4);

        // when
        // then
        assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(orderTableId, request))
                .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("고객의 수를 변경하려고 하는 해당 테이블의 사용여부가 미사용중이면 예외 발생한다.")
    @Test
    void changeNumberOfGuestsOrderTable_occupiedException() {
        // given
        OrderTable ORDER_TABLE_1번 = orderTableResponse(NAME_1번, 2, false);
        UUID orderTableId = ORDER_TABLE_1번.getId();
        when(orderTableRepository.findById(any())).thenReturn(Optional.of(ORDER_TABLE_1번));
        OrderTable request = changeNumberOfGuestsRequest(4);

        // when
        // then
        assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(orderTableId, request))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("해당 주문 테이블 고객의 수를 변경할 때, 0명보다 작으면 예외 발생한다.")
    @ValueSource(ints = {-1, -111, -99999})
    @ParameterizedTest
    void changeNumberOfGuestsOrderTable_negativeException(int numberOfGuests) {
        // given
        OrderTable ORDER_TABLE_1번 = orderTableResponse(NAME_1번, 2, true);
        UUID orderTableId = ORDER_TABLE_1번.getId();
        OrderTable request = changeNumberOfGuestsRequest(numberOfGuests);

        // when
        // then
        assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(orderTableId, request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("해당 주문테이블목록을 볼 수 있다.")
    @Test
    void getOrderTables() {
        // given
        OrderTable ORDER_TABLE_1번 = orderTableResponse(NAME_1번, 2, true);
        OrderTable ORDER_TABLE_2번 = orderTableResponse(NAME_2번, 0, false);
        when(orderTableRepository.findAll()).thenReturn(List.of(ORDER_TABLE_1번, ORDER_TABLE_2번));

        // when
        List<OrderTable> result = orderTableService.findAll();

        // then
        assertThat(result).containsExactly(ORDER_TABLE_1번, ORDER_TABLE_2번);
    }
}
