package kitchenpos.application;

import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderTableRepository;
import kitchenpos.domain.ProductRepository;
import kitchenpos.helper.MenuGroupTestHelper;
import kitchenpos.helper.MenuTestHelper;
import kitchenpos.helper.OrderLineItemTestHelper;
import kitchenpos.helper.OrderTableTestHelper;
import kitchenpos.helper.OrderTestHelper;
import kitchenpos.helper.ProductTestHelper;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class SetupTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private MenuGroupRepository menuGroupRepository;

    @Autowired
    private OrderTableRepository orderTableRepository;

    @Autowired
    private OrderRepository orderRepository;

    protected static MenuGroupTestHelper menuGroupTestHelper;
    protected static ProductTestHelper productTestHelper;
    protected static MenuTestHelper menuTestHelper;
    protected static OrderTableTestHelper orderTableTestHelper;
    protected static OrderTestHelper orderTestHelper;
    protected static OrderLineItemTestHelper orderLineItemTestHelper;

    @BeforeEach
    void setUp() {
        this.menuGroupTestHelper = new MenuGroupTestHelper(menuGroupRepository);
        this.productTestHelper = new ProductTestHelper(productRepository);
        this.menuTestHelper = new MenuTestHelper(menuRepository);
        this.orderTableTestHelper = new OrderTableTestHelper(orderTableRepository);
        this.orderTestHelper = new OrderTestHelper(orderRepository);
        this.orderLineItemTestHelper = new OrderLineItemTestHelper();
    }
}
