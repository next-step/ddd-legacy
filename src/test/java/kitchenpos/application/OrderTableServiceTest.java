package kitchenpos.application;

import kitchenpos.ApplicationMockTest;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static kitchenpos.fixture.OrderTableFixture.NAME_1번;
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
}
