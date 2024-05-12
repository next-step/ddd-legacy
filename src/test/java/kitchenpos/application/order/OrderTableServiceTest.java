package kitchenpos.application.order;

import kitchenpos.application.OrderTableService;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderTableRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("Application: 주문 테이블 서비스 테스트")
@ExtendWith(MockitoExtension.class)
class OrderTableServiceTest {

    @InjectMocks
    private OrderTableService orderTableService;
    @Mock
    private OrderTableRepository orderTableRepository;
    @Mock
    private OrderRepository orderRepository;


    @Nested
    @DisplayName("주문 테이블을 생성할 수 있다.")
    class create {

    }

    @Nested
    @DisplayName("주문 테이블에 손님을 입장시킬 수 있다.")
    class sit {

    }

    @Nested
    @DisplayName("주문 테이블의 손님을 퇴장시킬 수 있다.")
    class clear {

    }

    @Nested
    @DisplayName("주문 테이블의 손님 수를 변경할 수 있다.")
    class changeNumberOfGuests {

    }

}
