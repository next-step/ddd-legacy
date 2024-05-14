package kitchenpos.config;

import kitchenpos.application.OrderService;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderTableRepository;
import kitchenpos.fakeClient.FakeKitchenridersClient;
import kitchenpos.infra.KitchenridersClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class OrderTestContextConfiguration {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private MenuRepository menuRepository;
    @Autowired
    private OrderTableRepository orderTableRepository;


    @Bean
    public KitchenridersClient kitchenridersClient(){
        return new FakeKitchenridersClient();
    };

    @Bean
    public OrderService orderService(){
        return new OrderService(orderRepository, menuRepository, orderTableRepository, kitchenridersClient());
    }
}
