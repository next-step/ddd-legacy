package kitchenpos.application;

import kitchenpos.ApplicationMockTest;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static kitchenpos.fixture.OrderTableFixture.NAME_1번;
import static kitchenpos.fixture.OrderTableFixture.orderTableCreateRequest;
import static kitchenpos.fixture.OrderTableFixture.orderTableResponse;
import static org.assertj.core.api.Assertions.assertThat;
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
}
