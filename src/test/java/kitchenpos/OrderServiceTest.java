package kitchenpos;

import kitchenpos.application.OrderService;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderTableRepository;
import kitchenpos.domain.ProductRepository;
import kitchenpos.infra.ProfanityClient;
import kitchenpos.infra.RidersClient;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    private MenuRepository menuRepository;
    private ProductRepository productRepository;
    private ProfanityClient profanityClient;
    private OrderRepository orderRepository;
    private OrderTableRepository orderTableRepository;
    private RidersClient ridersClient;

    @InjectMocks
    private OrderService orderService;


}
