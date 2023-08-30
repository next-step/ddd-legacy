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
        OrderTable request = new OrderTable();
        request.setName(null);

        assertThatThrownBy(() -> orderTableService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 주문테이블_생성_실패__이름이_비어있음() {
        OrderTable request = new OrderTable();
        request.setName("");

        assertThatThrownBy(() -> orderTableService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 주문테이블_정리_실패__해당_주문테이블에_완료되지_않은_주문이_존재() {
        OrderTable request = new OrderTable();
        request.setName("1번 테이블");
        OrderTable orderTable = orderTableService.create(request);
        when(orderRepository.existsByOrderTableAndStatusNot(any(), any())).thenReturn(true);

        assertThatThrownBy(() -> orderTableService.clear(orderTable.getId()))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void 주문테이블_손님수_변경_실패__손님수가_음수() {
        OrderTable createRequest = new OrderTable();
        createRequest.setName("1번 테이블");
        OrderTable orderTable = orderTableService.create(createRequest);
        OrderTable request = new OrderTable();
        request.setNumberOfGuests(-1);

        assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(orderTable.getId(), request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 주문테이블_손님수_변경_실패__주문테이블이_착석상태가_아님() {
        OrderTable createRequest = new OrderTable();
        createRequest.setName("1번 테이블");
        OrderTable orderTable = orderTableService.create(createRequest);
        OrderTable request = new OrderTable();

        assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(orderTable.getId(), request))
                .isInstanceOf(IllegalStateException.class);
    }
}
