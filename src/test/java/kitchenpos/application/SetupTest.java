package kitchenpos.application;

import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.ProductRepository;
import kitchenpos.helper.MenuGroupTestHelper;
import kitchenpos.helper.MenuTestHelper;
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

    private MenuGroupTestHelper menuGroupTestHelper;
    private ProductTestHelper productTestHelper;
    private MenuTestHelper menuTestHelper;

    @BeforeEach
    void setUp() {
        this.menuGroupTestHelper = new MenuGroupTestHelper(menuGroupRepository);
        this.productTestHelper = new ProductTestHelper(productRepository);
        this.menuTestHelper = new MenuTestHelper(menuRepository);
    }
}
