package kitchenpos;

import kitchenpos.application.*;
import kitchenpos.domain.*;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
public class IntegrationTest {

    @MockBean
    PurgomalumClient purgomalumClient;

    @Autowired
    protected MenuService menuService;

    @Autowired
    protected ProductService productService;

    @Autowired
    protected MenuGroupService menuGroupService;

    @Autowired
    protected OrderTableService orderTableService;

    @Autowired
    protected OrderService orderService;

    @Autowired
    protected MenuRepository menuRepository;

    @Autowired
    protected MenuGroupRepository menuGroupRepository;

    @Autowired
    protected ProductRepository productRepository;

    @Autowired
    protected MenuProductRepository menuProductRepository;

    @Autowired
    protected OrderTableRepository orderTableRepository;

    @Autowired
    protected OrderRepository orderRepository;

    @Autowired
    protected OrderLineRepository orderLineRepository;


    @BeforeEach
    void setup() {
        Mockito.when(purgomalumClient.containsProfanity(any())).thenReturn(false);


        orderLineRepository.deleteAllInBatch();
        orderRepository.deleteAllInBatch();
        orderTableRepository.deleteAllInBatch();

        menuProductRepository.deleteAllInBatch();
        menuRepository.deleteAllInBatch();
        productRepository.deleteAllInBatch();
        menuGroupRepository.deleteAllInBatch();


    }

    protected List<MenuProduct> toMenuProductList(Product... products) {
        return Stream.of(products)
            .map(product -> {
                    MenuProduct menuProduct = new MenuProduct();
                    menuProduct.setProduct(product);
                    menuProduct.setProductId(product.getId());
                    menuProduct.setQuantity(1L);
                    return menuProduct;
                }
            )
            .collect(Collectors.toList());
    }

    protected List<MenuProduct> toMenuProductList(List<Product> products) {
        return products.stream()
            .map(product -> {
                    MenuProduct menuProduct = new MenuProduct();
                    menuProduct.setProduct(product);
                    menuProduct.setProductId(product.getId());
                    menuProduct.setQuantity(1L);
                    return menuProduct;
                }
            )
            .collect(Collectors.toList());
    }


}
