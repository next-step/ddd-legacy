package kitchenpos.application;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Optional;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import kitchenpos.fixture.OrderTableFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.actuate.management.ThreadDumpEndpoint.ThreadDumpDescriptor;

@ExtendWith(MockitoExtension.class)
class OrderTableServiceTest {

    @Mock
    OrderTableRepository orderTableRepository;
    @InjectMocks
    OrderTableService orderTableService;

    private OrderTable orderTable;

    @DisplayName("주문 테이블을 생성시 이름 값 필수")
    @Test
    public void 주문테이블_이름값필수() throws Exception {
        orderTable = OrderTableFixture.create("", 0, false);

        assertThatThrownBy(() -> orderTableService.create(orderTable))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("주문 테이블 착석")
    @Test
    public void 주문테이블_착석() throws Exception {
        orderTable = OrderTableFixture.create("1번테이블", 0, false);
        when(orderTableRepository.findById(any())).thenReturn(Optional.of(orderTable));

        orderTable = orderTableService.sit(orderTable.getId());

        assertThat(orderTable.isOccupied()).isEqualTo(true);
    }

    @DisplayName("주문 테이블 청소")
    @Test
    public void 주문테이블_청소() throws Exception {
        orderTable = OrderTableFixture.create("1번테이블", 4, true);
        when(orderTableRepository.findById(any())).thenReturn(Optional.of(orderTable));

        orderTable = orderTableService.clear(orderTable.getId());

        assertThat(orderTable.isOccupied()).isEqualTo(false);
        assertThat(orderTable.getNumberOfGuests()).isEqualTo(0);
    }

    @DisplayName("주문 테이블 고객 수 변경 성공")
    @Test
    public void 주문테이블_고객수변경_성공() throws Exception {
        orderTable = OrderTableFixture.create("1번테이블", 4, true);
        when(orderTableRepository.findById(any())).thenReturn(Optional.of(orderTable));
        orderTable.setNumberOfGuests(5);

        orderTable = orderTableService.changeNumberOfGuests(orderTable.getId(), orderTable);

        assertThat(orderTable.getNumberOfGuests()).isEqualTo(5);
    }

    @DisplayName("주문 테이블 고객 수 변경 실패")
    @Test
    public void 주문테이블_고객수변경_살패() throws Exception {
        orderTable = OrderTableFixture.create("1번테이블", 4, true);
        orderTable.setNumberOfGuests(-1);

        assertThatThrownBy(
            () -> orderTableService.changeNumberOfGuests(orderTable.getId(), orderTable))
            .isInstanceOf(IllegalArgumentException.class);
    }

}