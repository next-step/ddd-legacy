package kitchenpos.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import kitchenpos.application.OrderTableService;
import kitchenpos.domain.InvalidNameException;
import kitchenpos.domain.InvalidNumberOfGuestsException;
import kitchenpos.domain.NotOccupiedOrderTableException;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderTable;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class OrderTableServiceTest {

    @Autowired
    private OrderTableService orderTableService;

    @MockBean
    private OrderRepository orderRepository;

    @Test
    void 주문테이블_생성_실패__이름이_null() {
        OrderTable request = OrderTableFixture.builder()
                .name(null)
                .build();

        assertThatThrownBy(() -> orderTableService.create(request))
                .isExactlyInstanceOf(InvalidNameException.class)
                .hasMessage("이름은 null이거나 비어있을 수 없습니다. 현재 값: [null]");
    }

    @Test
    void 주문테이블_생성_실패__이름이_비어있음() {
        OrderTable request = OrderTableFixture.builder()
                .name("")
                .build();

        assertThatThrownBy(() -> orderTableService.create(request))
                .isExactlyInstanceOf(InvalidNameException.class)
                .hasMessage("이름은 null이거나 비어있을 수 없습니다. 현재 값: []");
    }

    @Test
    void 주문테이블_정리_실패__해당_주문테이블에_완료되지_않은_주문이_존재() {
        OrderTable orderTable = orderTableService.create(OrderTableFixture.builder().build());
        when(orderRepository.existsByOrderTableAndStatusNot(any(), any())).thenReturn(true);

        assertThatThrownBy(() -> orderTableService.clear(orderTable.getId()))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void 주문테이블_손님수_변경_실패__손님수가_음수() {
        OrderTable orderTable = orderTableService.create(OrderTableFixture.builder().build());
        OrderTable request = new OrderTable();
        request.setNumberOfGuests(-1);

        assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(orderTable.getId(), request))
                .isExactlyInstanceOf(InvalidNumberOfGuestsException.class)
                .hasMessage("방문한 손님 수는 음수일 수 없습니다. 현재 값: [-1]");
    }

    @Test
    void 주문테이블_손님수_변경_실패__주문테이블이_착석상태가_아님() {
        OrderTable createRequest = OrderTableFixture.builder()
                .occupied(false)
                .build();
        OrderTable orderTable = orderTableService.create(createRequest);
        OrderTable request = new OrderTable();

        assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(orderTable.getId(), request))
                .isExactlyInstanceOf(NotOccupiedOrderTableException.class)
                .hasMessage(String.format("착석상태가 아닌 주문테이블입니다. OrderTable id 값: [%s]", orderTable.getId()));
    }
}
