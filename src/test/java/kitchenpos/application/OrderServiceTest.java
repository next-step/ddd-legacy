package kitchenpos.application;

import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderTableRepository;
import kitchenpos.infra.KitchenridersClient;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @InjectMocks OrderService orderService;
    @Mock
    OrderRepository orderRepository;
    @Mock
    MenuRepository menuRepository;
    @Mock
    OrderTableRepository orderTableRepository;
    @Mock
    KitchenridersClient kitchenridersClient;


    @Test
    @DisplayName("주문을 생성한다.")
    void createOrder(){

    }
}
