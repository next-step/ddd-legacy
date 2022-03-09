package kitchenpos.application;

import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderTableRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;

class OrderTableServiceTest {
    @InjectMocks
    OrderTableService orderTableService;
    @Mock
    OrderTableRepository orderTableRepository;
    @Mock
    OrderRepository orderRepository;
}