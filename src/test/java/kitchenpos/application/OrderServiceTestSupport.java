package kitchenpos.application;

import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderTableRepository;
import kitchenpos.infra.KitchenridersClient;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public abstract class OrderServiceTestSupport {
    @Mock
    protected OrderRepository orderRepository;
    @Mock
    protected MenuRepository menuRepository;
    @Mock
    protected OrderTableRepository orderTableRepository;
    @Mock
    protected KitchenridersClient kitchenridersClient;

    @InjectMocks
    protected OrderService testService;
}
