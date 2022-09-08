package kitchenpos;

import kitchenpos.application.MenuGroupService;
import kitchenpos.application.MenuService;
import kitchenpos.application.ProductService;
import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.domain.MenuProductRepository;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.ProductRepository;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

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
    protected MenuRepository menuRepository;

    @Autowired
    protected MenuGroupRepository menuGroupRepository;

    @Autowired
    protected ProductRepository productRepository;

    @Autowired
    protected MenuProductRepository menuProductRepository;


    @BeforeEach
    void setup() {
        Mockito.when(purgomalumClient.containsProfanity(any())).thenReturn(false);

        menuProductRepository.deleteAllInBatch();
        menuRepository.deleteAllInBatch();
        productRepository.deleteAllInBatch();
        menuGroupRepository.deleteAllInBatch();

    }

}
